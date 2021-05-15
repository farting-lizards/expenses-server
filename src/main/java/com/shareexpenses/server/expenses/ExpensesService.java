package com.shareexpenses.server.expenses;

import com.shareexpenses.server.account.Account;
import com.shareexpenses.server.account.AccountRepository;
import com.shareexpenses.server.currency.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpensesService {

  private final ExpensesRepository expensesRepository;
  private final AccountRepository accountRepository;

  public List<Expense> getAllExpenses() {
    return expensesRepository.findAll();
  }


  public Expense addExpense(IncomingExpenseDTO newExpense) {
    Account account = this
      .accountRepository
      .findById(newExpense.getAccountId())
      .orElseThrow(() -> new EntityNotFoundException("No account found with id " + newExpense.getAccountId() + "."));

    Expense expense = Expense
      .builder()
      .description(newExpense.getDescription())
      .amount(newExpense.getAmount())
      .currency(newExpense.getCurrency().label)
      .timestamp(newExpense.getTimestamp())
      .account(account)
      .category(newExpense.getCategory())
      .build();
    return this.expensesRepository.save(expense);
  }
}
