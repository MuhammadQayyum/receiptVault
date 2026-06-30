package com.receiptvault.repository;

import com.receiptvault.entity.Receipt;
import com.receiptvault.entity.Property;
import com.receiptvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByUserOrderByCreatedAtDesc(User user);
    List<Receipt> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(r.amount) FROM Receipt r")
    BigDecimal getTotalAmountAll();

    @Query("SELECT SUM(r.amount) FROM Receipt r WHERE r.receiptDate BETWEEN :start AND :end")
    BigDecimal getTotalAmountAllAndDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT r FROM Receipt r WHERE r.property = :property AND MONTH(r.receiptDate) = :month AND YEAR(r.receiptDate) = :year")
    List<Receipt> findByPropertyAndMonthAndYear(Property property, int month, int year);
}