package com.shareexpenses.server.expenses;

import com.shareexpenses.server.currency.Currency;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;

@Data
@Setter
public class IncomingExpenseDTO {
  Double amount;
  Currency currency;
  Instant timestamp;
  String category;
  String description;
  Long accountId;
}