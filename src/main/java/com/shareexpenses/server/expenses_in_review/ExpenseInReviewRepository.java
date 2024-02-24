package com.shareexpenses.server.expenses_in_review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface ExpenseInReviewRepository extends JpaRepository<ExpenseInReview, String> {

    boolean existsById(String s);

    List<ExpenseInReview> findByReviewUntilBefore(Timestamp timestamp);



    @Modifying
    @Query("update ExpenseInReview e set e.reviewUntil = current_timestamp() where e.externalId = :externalId")
    int updateReviewUntilForExternalId(@Param("externalId") String externalId);
}
