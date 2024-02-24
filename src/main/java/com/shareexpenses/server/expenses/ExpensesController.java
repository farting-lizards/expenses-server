package com.shareexpenses.server.expenses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/expenses")
@Slf4j
public class ExpensesController {

  private final ExpensesService expensesService;

  @GetMapping
  public List<Expense> getAllExpenses() {
    return this.expensesService.getAllExpenses();
  }

  @PostMapping
  public Expense addExpense(@RequestBody IncomingExpenseDTO incomingExpenseDTO) {
    return this.expensesService.addExpense(incomingExpenseDTO, Optional.empty());
  }

  @PutMapping("/{expenseId}")
  public Expense updateExpense(@PathVariable long expenseId, @RequestBody IncomingExpenseDTO incomingExpenseDTO) {
    return this.expensesService.updateExpense(expenseId, incomingExpenseDTO);
  }

  @DeleteMapping("/{expenseId}")
  public long deleteExpense(@PathVariable long expenseId) {
    this.expensesService.deleteExpense(expenseId);
    return expenseId;
  }
}

