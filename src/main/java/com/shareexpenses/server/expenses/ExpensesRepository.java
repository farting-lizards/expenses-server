package com.shareexpenses.server.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpensesRepository extends JpaRepository<Expense, Long>  {
  List<Expense> findAll();

  boolean existsByExternalId(String externalId);
}
