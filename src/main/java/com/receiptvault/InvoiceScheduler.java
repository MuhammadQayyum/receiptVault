package com.receiptvault;

import com.receiptvault.entity.Property;
import com.receiptvault.service.InvoiceService;
import com.receiptvault.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InvoiceScheduler {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private InvoiceService invoiceService;

    // Runs at 1:00 AM on the 1st of every month
    @Scheduled(cron = "0 0 1 1 * *")
    public void generateMonthlyInvoices() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        List<Property> properties = propertyService.getAllProperties();
        for (Property property : properties) {
            invoiceService.generateInvoiceForProperty(property, month, year);
        }
        System.out.println("Monthly invoices generated for " + month + "/" + year);
    }
}
