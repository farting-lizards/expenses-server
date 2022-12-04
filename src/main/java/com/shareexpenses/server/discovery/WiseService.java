package com.shareexpenses.server.discovery;

import com.shareexpenses.server.config.AccountsConfig;
import com.shareexpenses.server.discovery.wise_entities.Balance;
import com.shareexpenses.server.discovery.wise_entities.BalanceStatement;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WiseService {

    private static final String WISE_BASE_URL = "https://api.transferwise.com/";

    @Autowired
    AccountsConfig accountsConfig;

    public void discoverExpensesBetween() {
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
                getStatementForBalanceId(
                    account.getProfileId(),
                    wiseBalance.getId(),
                    "2022-12-01",
                    "2022-12-05",
                    account.getApiBearerToken(),
                   account.getPrivateKey()
                );
            });
        });
    }

    @SneakyThrows
    private void getStatementForBalanceId(String profileId, String balanceId, String from, String to, String bearerToken, String privateKey) {
        String statementUrl = getBalanceStatementUrl(profileId, balanceId, from, to);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> params = new HashMap<>();
        params.put("fromTime", "2022-10-01T00:00:00.000Z");
        params.put("toTime", "2022-10-10T00:00:00.000Z");
        params.put("type", "COMPACT");

        RestTemplate template = new RestTemplate();
        template.setErrorHandler(new Wise2faErrorHandler());
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<Object> maybeBalanceStatement = template.exchange(statementUrl, HttpMethod.GET, request, Object.class, params);

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

            balanceStatement.getBody().getTransactions().forEach(wiseTransaction -> {
                log.info("Wise transaction {}", wiseTransaction.getReferenceNumber());
            });

        } else if (maybeBalanceStatement.getStatusCode() == HttpStatus.OK || maybeBalanceStatement.getStatusCode() == HttpStatus.CREATED) {
            log.info("Balance statement without 2fa approval {}", maybeBalanceStatement.getBody());
        } else {
            log.warn("There was an error fetching balance statement {}", maybeBalanceStatement.getStatusCode());
        }
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