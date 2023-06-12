package com.shareexpenses.server.discovery;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseInReviewRepository extends JpaRepository<ExpenseInReview, String> {

    boolean existsById(String s);


}
