package com.shareexpenses.server.expenses;

import com.shareexpenses.server.account.Account;
import com.shareexpenses.server.currency.Currency;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name="expense_sequence", initialValue=1, allocationSize=100)
public class Expense  {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expense_sequence")
  private Long id;

  @Column
  private String externalId;

  @Column(nullable = false)
  private Double amount;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private Instant timestamp;

  // TODO: Create enum for category
  @Column(nullable = false)
  private String category;

  @Column
  private String description;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "account_id")
  private Account account;
}
