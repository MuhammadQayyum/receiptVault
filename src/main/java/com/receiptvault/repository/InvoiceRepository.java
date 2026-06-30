package com.receiptvault.repository;

import com.receiptvault.entity.Invoice;
import com.receiptvault.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByOrderByDueDateAsc();

    boolean existsByPropertyAndBillingMonthAndBillingYear(
            Property property, Integer billingMonth, Integer billingYear);
}