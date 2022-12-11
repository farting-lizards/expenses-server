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
@RequestMapping("/api/discover")
public class DiscoverController {

    @Autowired WiseService wiseService;

    @GetMapping
    public int discoverExpenses() {
        return wiseService.discoverExpensesBetween();
    }
}
