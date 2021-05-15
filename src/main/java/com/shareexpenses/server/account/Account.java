package com.shareexpenses.server.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shareexpenses.server.expenses.Expense;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "account")
  @JsonIgnore
  private Set<Expense> expenses;
}
