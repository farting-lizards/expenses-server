package com.shareexpenses.server.expenses_in_review.wise_entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.Optional;

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
    public static class Amount implements Serializable {
        private Float value;
        private String currency;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Details implements Serializable {
        private String category;
        private String description;
        private Merchant merchant;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Merchant implements Serializable {
        private String name;
    }
}
