package com.receiptvault.service;

import com.receiptvault.entity.Invoice;
import com.receiptvault.entity.Property;
import com.receiptvault.repository.InvoiceRepository;
import com.receiptvault.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    // Generates an invoice for a property for the given month/year if one doesn't already exist
    public void generateInvoiceForProperty(Property property, int month, int year) {
        boolean invoiceExists = invoiceRepository
                .existsByPropertyAndBillingMonthAndBillingYear(property, month, year);
        boolean receiptExists = !receiptRepository
                .findByPropertyAndMonthAndYear(property, month, year).isEmpty();

        if (!invoiceExists && !receiptExists) {
            Invoice invoice = new Invoice();
            invoice.setProperty(property);
            invoice.setAmountDue(property.getRentPrice());
            invoice.setBillingMonth(month);
            invoice.setBillingYear(year);
            invoice.setDueDate(LocalDate.of(year, month, 5));
            invoiceRepository.save(invoice);
        }
    }

    public List<Invoice> searchInvoices(String propertyAddress, Integer month, Integer year) {
        List<Invoice> all = invoiceRepository.findAllByOrderByDueDateAsc();
        return all.stream()
                .filter(inv -> propertyAddress == null || propertyAddress.isEmpty() ||
                        inv.getProperty().getAddress().toLowerCase().contains(propertyAddress.toLowerCase()))
                .filter(inv -> month == null || inv.getBillingMonth().equals(month))
                .filter(inv -> year == null || inv.getBillingYear().equals(year))
                .collect(java.util.stream.Collectors.toList());
    }
}