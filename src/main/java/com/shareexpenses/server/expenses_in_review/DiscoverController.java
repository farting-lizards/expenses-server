package com.shareexpenses.server.expenses_in_review;

import com.shareexpenses.server.expenses.Expense;
import com.shareexpenses.server.expenses.IncomingExpenseDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/expenses-in-review")
public class DiscoverController {

    @Autowired WiseService wiseService;

    // dates are in format 'YYYY-MM-DD' (i.e., no time info is provided)
    @GetMapping("/discover")
    public int discoverExpenses(@RequestParam String fromDate, @RequestParam String toDate) {
        return wiseService.discoverExpensesBetween(fromDate, toDate);
    }

    @GetMapping("/count")
    public long getExpensesToReviewCount() {
        return wiseService.getExpensesToReviewCount();
    }

    @GetMapping("/start-review")
    public List<ExpenseInReview> startReview() { return  wiseService.startReview(); }

    @PostMapping("/release")
    public void releaseExpenses(@RequestBody List<String> expenseIds) {
        wiseService.releaseExpenses(expenseIds);
    }

    @PostMapping("/{externalId}")
    public Expense acceptExpenses(@PathVariable String externalId, @RequestBody IncomingExpenseDTO updatedExpense) {
        return wiseService.acceptExpense(externalId, updatedExpense);
    }

    @DeleteMapping("/{externalId}")
    public void rejectExpense(@PathVariable String externalId) {
        wiseService.rejectExpense(externalId);
    }

}
