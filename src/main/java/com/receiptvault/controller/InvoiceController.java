package com.receiptvault.controller;

import com.receiptvault.entity.Business;
import com.receiptvault.entity.Invoice;
import com.receiptvault.entity.Receipt;
import com.receiptvault.entity.User;
import com.receiptvault.service.BusinessService;
import com.receiptvault.service.EmailService;
import com.receiptvault.service.InvoiceService;
import com.receiptvault.service.PropertyService;
import com.receiptvault.service.ReceiptService;
import com.receiptvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private static final BigDecimal LATE_FEE = new BigDecimal("50.00");

    @GetMapping
    public String getInvoices(@RequestParam(required = false) String property,
                          @RequestParam(required = false) Integer month,
                          Model model) {
        LocalDate today = LocalDate.now();
        int selectedMonth = (month != null) ? month : today.getMonthValue();
        int currentYear = today.getYear();

        List<Invoice> invoices = invoiceService.searchInvoices(property, selectedMonth, currentYear);
        List<Business> businesses = businessService.getAllBusinesses();

        model.addAttribute("invoices", invoices);
        model.addAttribute("businesses", businesses);
        model.addAttribute("lateFee", LATE_FEE);
        model.addAttribute("today", today);
        model.addAttribute("selectedProperty", property);
        model.addAttribute("selectedMonth", selectedMonth);
        return "invoice";
    }

    @PostMapping("/mark-paid")
    public String markPaid(@RequestParam Long invoiceId,
                           @RequestParam Long businessId,
                           @RequestParam(required = false) String paymentMethod,
                           @RequestParam(required = false) String paymentType,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false, defaultValue = "false") Boolean includeLateFee,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        Invoice invoice = invoiceService.getInvoiceById(invoiceId).orElse(null);
        Business business = businessService.getBusinessById(businessId).orElse(null);

        if (invoice == null) return "redirect:/invoices";

        BigDecimal lateFee = includeLateFee ? LATE_FEE : BigDecimal.ZERO;
        BigDecimal totalAmount = invoice.getAmountDue().add(lateFee);

        String notes = (includeLateFee ? "Late payment — includes $50.00 late fee. " : "") +
                (description != null ? description : "");

        Receipt saved = receiptService.createReceipt(totalAmount, LocalDate.now(),
                notes, business, invoice.getProperty(), lateFee, paymentMethod, paymentType,
                BigDecimal.ZERO, user);

        emailService.sendReceiptEmail(saved);

        invoiceService.deleteInvoice(invoiceId);

        redirectAttributes.addFlashAttribute("success",
                "Invoice marked as paid. Receipt created for " +
                        invoice.getProperty().getPropertyName() + " — $" + totalAmount);
        return "redirect:/invoices";
    }

    // Manual trigger for testing — generates invoices right now instead of waiting for the 1st
    @PostMapping("/generate-now")
    public String generateNow(RedirectAttributes redirectAttributes) {
        LocalDate today = LocalDate.now();
        List<com.receiptvault.entity.Property> properties = propertyService.getAllProperties();
        for (com.receiptvault.entity.Property property : properties) {
            invoiceService.generateInvoiceForProperty(property,
                    today.getMonthValue(), today.getYear());
        }
        redirectAttributes.addFlashAttribute("success", "Invoices generated for this month.");
        return "redirect:/invoices";
    }
}