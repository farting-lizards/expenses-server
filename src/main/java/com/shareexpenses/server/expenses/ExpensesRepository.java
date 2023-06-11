package com.shareexpenses.server.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExpensesRepository extends JpaRepository<Expense, Long> {
  List<Expense> findAll();

  @Query(value = "SELECT 1 FROM expenses limit 1", nativeQuery = true)
  // The return value does not really matter, just that it did not blow up
  List<Integer> checkDB();

  boolean existsByExternalId(String externalId);
}
