package com.shareexpenses.server.config;

import lombok.*;

// TODO: Limit access to fields
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountConfig {
    String owner;
    String type;
    String profileId;
    Long accountId;
    String apiBearerToken;
    String privateKey;
}
