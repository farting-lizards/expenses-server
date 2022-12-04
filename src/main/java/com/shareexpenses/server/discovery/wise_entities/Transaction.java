package com.shareexpenses.server.discovery.wise_entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
public class Transaction implements Serializable {
    private String referenceNumber;
    private String date;
    private Amount amount;
    private Details details;
    private String type;

    @Setter
    @Getter
    @NoArgsConstructor
    private static class Amount implements Serializable {
        private Double value;
        private String currency;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class Details implements Serializable {
        private String category;
        private String description;
        private Merchant merchant;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class Merchant implements Serializable {
        private String name;
    }
}
