package com.shareexpenses.server.discovery;

import com.shareexpenses.server.config.AccountsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
public class WiseService {

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
            ResponseEntity<WiseBalance[]> wiseBalancesResp = template.exchange(balanceUrl, HttpMethod.GET, request, WiseBalance[].class);

            Arrays.asList(wiseBalancesResp.getBody()).forEach(wiseBalance -> {
                log.info("Wise Balance {} ownser {}", wiseBalance.getId(), account.getOwner());
            });
        });
    }


    private String getBalancesForProfile(String profileId) {
        return "https://api.transferwise.com/v4/profiles/" + profileId + "/balances?types=STANDARD";
    }
}
