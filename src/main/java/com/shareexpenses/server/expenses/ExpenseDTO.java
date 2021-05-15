package com.shareexpenses.server.expenses;

import com.shareexpenses.server.currency.Currency;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ExpenseDTO {
  String id;
  Double amount;
  Currency currency;
  Instant date;
  String category;
  String description;
}
