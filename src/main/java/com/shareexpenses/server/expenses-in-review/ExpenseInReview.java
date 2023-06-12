package com.shareexpenses.server.discovery;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "expenses_in_review")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseInReview {

    @Id
    private String externalId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Timestamp date;

    @Column
    private Timestamp reviewUntil;

    @Column(nullable = false)
    private Float amount;

    // TODO: Enum for currency
    @Column(nullable = false)
    private String currency;

    @Column
    private String externalCategory;

    @Column
    private String description;

    @Column
    private String merchantName;

}
