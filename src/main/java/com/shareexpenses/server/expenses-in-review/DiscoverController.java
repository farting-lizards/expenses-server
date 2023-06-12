package com.shareexpenses.server.discovery;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

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
    public int getExpensesToReviewCount() {
        return wiseService.getExpensesToReviewCount();
    }
}
