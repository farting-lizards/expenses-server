package com.shareexpenses.server.expenses;

import com.shareexpenses.server.account.Account;
import com.shareexpenses.server.account.AccountRepository;
import com.shareexpenses.server.config.AccountsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpensesService {

  private final ExpensesRepository expensesRepository;
  private final AccountRepository accountRepository;

  @Autowired
  AccountsConfig accountConfig;

  public List<Expense> getAllExpenses() {
    accountConfig.accounts.forEach(accountConfig1 -> {
      log.warn("Account profile {}", accountConfig1.getProfileId());
    });
    return expensesRepository.findAll();
  }

  public Expense addExpense(IncomingExpenseDTO newExpense) {
    Account account = this.accountRepository
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

  public Expense updateExpense(Long id, IncomingExpenseDTO updatedExpense) {
    Account account = this.accountRepository
        .findById(updatedExpense.getAccountId())
        .orElseThrow(
            () -> new EntityNotFoundException("No account found with id " + updatedExpense.getAccountId() + "."));

    Expense expense = Expense
        .builder()
        .id(id)
        .description(updatedExpense.getDescription())
        .amount(updatedExpense.getAmount())
        .currency(updatedExpense.getCurrency().label)
        .timestamp(updatedExpense.getTimestamp())
        .account(account)
        .category(updatedExpense.getCategory())
        .build();
    return this.expensesRepository.save(expense);
  }

  public void deleteExpense(long expenseId) {
    Expense expense = this.expensesRepository.findById(expenseId)
        .orElseThrow(() -> new EntityNotFoundException("No expense found with id " + expenseId + "."));
    this.expensesRepository.delete(expense);
  }

  public void checkDB() {
    this.expensesRepository.checkDB();
  }
}
