package com.shareexpenses.server.expenses_in_review;

import com.shareexpenses.server.config.AccountsConfig;
import com.shareexpenses.server.expenses.ExpensesRepository;
import com.shareexpenses.server.expenses_in_review.wise_entities.Balance;
import com.shareexpenses.server.expenses_in_review.wise_entities.BalanceStatement;
import com.shareexpenses.server.expenses_in_review.wise_entities.Transaction;
import com.shareexpenses.server.utils.DateTimeUtils;
import com.shareexpenses.server.utils.DigitalSignatures;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class WiseService {

    @Autowired
    private ExpenseInReviewRepository expensesInReviewQueue;

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private DateTimeUtils dateTimeUtils;
    private static final String WISE_BASE_URL = "https://api.transferwise.com/";

    @Autowired
    AccountsConfig accountsConfig;

    public int discoverExpensesBetween(String fromDate, String toDate) {
        AtomicInteger newTransactions = new AtomicInteger();

        Timestamp fromTimestamp = dateTimeUtils.timestampFromDateStringWithoutTime(fromDate);
        Timestamp toTimestamp = dateTimeUtils.timestampFromDateStringWithoutTime(toDate);

        // fromDate should be before or equal to the toDate
        assert (fromTimestamp.before(toTimestamp));

        accountsConfig.accounts.forEach(account -> {
            String balanceUrl = getBalancesForProfile(account.getProfileId());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + account.getApiBearerToken());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            RestTemplate template = new RestTemplate();
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<Balance[]> wiseBalancesResp = template.exchange(balanceUrl, HttpMethod.GET, request, Balance[].class);

            Arrays.asList(wiseBalancesResp.getBody()).forEach(wiseBalance -> {
                log.info("Wise Balance {} owner {}", wiseBalance.getId(), account.getOwner());
                var count = getStatementForBalanceId(
                    account.getProfileId(),
                    account.getAccountId(),
                    wiseBalance.getId(),
                    fromDate,
                    toDate,
                    account.getApiBearerToken(),
                    account.getPrivateKey()
                );
                newTransactions.getAndAdd(count);
            });
        });
        return newTransactions.get();
    }

    // YYYY-MM-DD or  YYYY-MM-DD HH:MM:SS or TImestamp
    @SneakyThrows
    private int getStatementForBalanceId(String profileId, Long accountId, String balanceId, String from, String to, String bearerToken, String privateKey) {
        String statementUrl = getBalanceStatementUrl(profileId, balanceId, from, to);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> params = new HashMap<>();
        params.put("fromTime", dateTimeUtils.dateStringToWiseString(from));
        params.put("toTime", dateTimeUtils.dateStringToWiseString(to));
        params.put("type", "COMPACT");

        RestTemplate template = new RestTemplate();
        template.setErrorHandler(new Wise2faErrorHandler());
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<BalanceStatement> maybeBalanceStatement = template.exchange(statementUrl, HttpMethod.GET, request, BalanceStatement.class, params);

        if (maybeBalanceStatement.getStatusCode() == HttpStatus.FORBIDDEN) {
            String x2faApprovalHeader = maybeBalanceStatement.getHeaders().getFirst("x-2fa-approval");
            assert x2faApprovalHeader != null;
            byte[] signedHeader = DigitalSignatures.sign(privateKey, x2faApprovalHeader.getBytes(StandardCharsets.UTF_8));
            String base64EncodedSignature = DigitalSignatures.encodeToBase64(signedHeader);

            HttpHeaders authorizedHeaders = new HttpHeaders();
            authorizedHeaders.set("Authorization", "Bearer " + bearerToken);
            authorizedHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            authorizedHeaders.set("X-Signature", base64EncodedSignature);
            authorizedHeaders.set("x-2fa-approval", x2faApprovalHeader);

            RestTemplate authTemplate = new RestTemplate();
            HttpEntity authRequest = new HttpEntity(authorizedHeaders);
            ResponseEntity<BalanceStatement> balanceStatement = authTemplate.exchange(statementUrl, HttpMethod.GET, authRequest, BalanceStatement.class, params);
            log.info("Balance Statement after 2fa approval {}", balanceStatement.getBody());

            AtomicInteger newUnreviewedTransactions = new AtomicInteger();

            balanceStatement.getBody().getTransactions().forEach(wiseTransaction -> {
                log.info("Checking wise transaction {}", wiseTransaction.getReferenceNumber());

                if(isCreditTransaction(wiseTransaction)
                    || expensesInReviewQueue.existsById(wiseTransaction.getReferenceNumber())
                    || expensesRepository.existsByExternalId(wiseTransaction.getReferenceNumber())
                ) {
                    // We can ignore the `wiseTransaction` if it represents a credit (money added to TW account)
                    // or if it is already added in the expensesInReview table
                    // or an expense already exists with same externalId
                    return;
                }

                ExpenseInReview expenseInReview = ExpenseInReview.builder()
                    .externalId(wiseTransaction.getReferenceNumber())
                    .accountId(accountId)
                    .date(dateTimeUtils.unixTimestampFromISOString(wiseTransaction.getDate()))
                    .amount(Math.abs(wiseTransaction.getAmount().getValue()))
                    .currency(wiseTransaction.getAmount().getCurrency())
                    .externalCategory(wiseTransaction.getDetails().getCategory())
                    .description(wiseTransaction.getDetails().getDescription())
                    .merchantName(wiseTransaction.getDetails().getMerchant() == null
                        ? null
                        : wiseTransaction.getDetails().getMerchant().getName()
                    )
                    .reviewUntil(null)
                    .build();

                expensesInReviewQueue.save(expenseInReview);
                newUnreviewedTransactions.getAndIncrement();
                log.info("Wise transaction {}", wiseTransaction.getReferenceNumber());
            });

            return newUnreviewedTransactions.get();

        } else if (maybeBalanceStatement.getStatusCode() == HttpStatus.OK || maybeBalanceStatement.getStatusCode() == HttpStatus.CREATED) {
            log.info("Balance statement without 2fa approval {}", maybeBalanceStatement.getBody());
        } else {
            log.warn("There was an error fetching balance statement {}", maybeBalanceStatement.getStatusCode());
        }

        return 0;
    }

    private boolean isCreditTransaction(Transaction wiseTransaction) {
        return wiseTransaction.getType().equalsIgnoreCase("credit");
    }

    public long getExpensesToReviewCount() {
        return this.expensesInReviewQueue.count();
    }

    class Wise2faErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            log.info("Received Error", response.getStatusText());
        }
    }

    private String getBalancesForProfile(String profileId) {
        return WISE_BASE_URL + "v4/profiles/" + profileId + "/balances?types=STANDARD";
    }

    private String getBalanceStatementUrl(String profileId, String balanceId, String from, String to) {
        return WISE_BASE_URL + "v1/profiles/"
            + profileId
            + "/balance-statements/"
            + balanceId
            + "/statement.json?intervalStart={fromTime}"
            + "&intervalEnd={toTime}"
            + "&type={type}";
    }

    private String normalizePrivateKey(String privateKey) {
        return privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")
            .trim();
    }
}
