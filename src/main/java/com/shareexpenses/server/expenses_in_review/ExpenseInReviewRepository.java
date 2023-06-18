package com.shareexpenses.server.expenses_in_review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface ExpenseInReviewRepository extends JpaRepository<ExpenseInReview, String> {

    boolean existsById(String s);

    List<ExpenseInReview> findByReviewUntilBefore(Timestamp timestamp);

}
