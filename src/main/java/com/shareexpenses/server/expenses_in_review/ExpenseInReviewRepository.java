package com.shareexpenses.server.expenses_in_review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseInReviewRepository extends JpaRepository<ExpenseInReview, String> {

    boolean existsById(String s);


}
