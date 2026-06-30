package com.receiptvault.service;

import com.receiptvault.entity.Business;
import com.receiptvault.entity.Property;
import com.receiptvault.entity.Receipt;
import com.receiptvault.entity.User;
import com.receiptvault.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    public List<Receipt> getReceiptsByUser(User user) {
        return receiptRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Optional<Receipt> getReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public Receipt createReceipt(BigDecimal amount, LocalDate receiptDate,
                                 String description, Business business,
                                 Property property, User user) {
        return createReceipt(amount, receiptDate, description, business,
                property, BigDecimal.ZERO, null, null, BigDecimal.ZERO, user);
    }

    public Receipt createReceipt(BigDecimal amount, LocalDate receiptDate,
                                 String description, Business business,
                                 Property property, BigDecimal lateFee, User user) {
        return createReceipt(amount, receiptDate, description, business,
                property, lateFee, null, null, BigDecimal.ZERO, user);
    }

    public Receipt createReceipt(BigDecimal amount, LocalDate receiptDate,
                                 String description, Business business,
                                 Property property, BigDecimal lateFee,
                                 String paymentMethod, String paymentType,
                                 BigDecimal securityDeposit, User user) {
        Receipt receipt = new Receipt();
        receipt.setStoreName(property != null ? property.getPropertyName() : "Unknown");
        receipt.setAmount(amount);
        receipt.setReceiptDate(receiptDate);
        receipt.setDescription(description);
        receipt.setBusiness(business);
        receipt.setProperty(property);
        receipt.setLateFee(lateFee != null ? lateFee : BigDecimal.ZERO);
        receipt.setSecurityDeposit(securityDeposit != null ? securityDeposit : BigDecimal.ZERO);
        receipt.setPaymentMethod(paymentMethod);
        receipt.setPaymentType(paymentType);
        receipt.setUser(user);
        return receiptRepository.save(receipt);
    }

    public Receipt saveReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }

    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }

    public List<Receipt> searchAllReceipts(String propertyName, Integer month) {
        List<Receipt> all = receiptRepository.findAllByOrderByCreatedAtDesc();
        return all.stream()
                .filter(r -> propertyName == null || propertyName.isEmpty() ||
                        (r.getProperty() != null &&
                                r.getProperty().getAddress()
                                        .toLowerCase().contains(propertyName.toLowerCase())))
                .filter(r -> month == null ||
                        r.getReceiptDate().getMonthValue() == month)
                .collect(java.util.stream.Collectors.toList());
    }

    public BigDecimal getTotalSpentAll() {
        BigDecimal total = receiptRepository.getTotalAmountAll();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getThisMonthTotalAll() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        BigDecimal total = receiptRepository.getTotalAmountAllAndDateBetween(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    public long getTotalReceiptCountAll() {
        return receiptRepository.findAllByOrderByCreatedAtDesc().size();
    }
}