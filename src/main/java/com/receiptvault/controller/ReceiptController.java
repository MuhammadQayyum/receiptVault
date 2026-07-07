package com.receiptvault.controller;

import com.receiptvault.entity.*;
import com.receiptvault.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TenantService tenantService;

    @GetMapping("/new")
    public String newReceiptForm(Model model) {
        model.addAttribute("businesses", businessService.getAllBusinesses());
        model.addAttribute("properties", propertyService.getAllProperties());
        Map<Long, Boolean> tenantMap = new java.util.HashMap<>();
        for (Property p : propertyService.getAllProperties()) {
            tenantMap.put(p.getPropertyID(), tenantService.propertyHasCurrentTenant(p));
        }
        model.addAttribute("tenantMap", tenantMap);
        return "new-receipt";
    }

    @PostMapping("/new")
    public String createReceipt(@RequestParam(required = false) Long businessId,
                                @RequestParam(required = false) Long propertyId,
                                @RequestParam BigDecimal amount,
                                @RequestParam String receiptDate,
                                @RequestParam(required = false) String notes,
                                @RequestParam(required = false) String paymentMethod,
                                @RequestParam(required = false) String paymentType,
                                @RequestParam(required = false) BigDecimal securityDeposit,
                                @RequestParam(required = false, defaultValue = "save") String action,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        Business business = businessId != null ?
                businessService.getBusinessById(businessId).orElse(null) : null;
        Property property = propertyId != null ?
                propertyService.getPropertyById(propertyId).orElse(null) : null;

        if (property != null && !tenantService.propertyHasCurrentTenant(property)) {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot create receipt — no tenant is assigned to " + property.getPropertyName() + ". Please assign a tenant first.");
            return "redirect:/receipts/new";
        }

        // Get current tenant name at time of receipt creation
        String tenantName = null;
        TenantHistory tenantHistory = null;
        if (property != null) {
            var history = tenantService.getCurrentHistoryByProperty(property);
            if (history.isPresent()) {
                tenantName = history.get().getTenant().getFullName();
                tenantHistory = history.get();
            }
        }

        Receipt saved = receiptService.createReceipt(amount, LocalDate.parse(receiptDate),
                notes, business, property, BigDecimal.ZERO, paymentMethod, paymentType,
                securityDeposit, tenantHistory, user);
        if ("print".equals(action)) {
            return "redirect:/receipts/" + saved.getReceiptID() + "/print-redirect";
        }
        redirectAttributes.addFlashAttribute("success", "Receipt created successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String receiptDetail(@PathVariable Long id, Model model) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        if (receipt.isEmpty()) return "redirect:/dashboard";
        model.addAttribute("receipt", receipt.get());
        model.addAttribute("businesses", businessService.getAllBusinesses());
        model.addAttribute("properties", propertyService.getAllProperties());
        Map<Long, Boolean> tenantMap = new java.util.HashMap<>();
        for (Property p : propertyService.getAllProperties()) {
            tenantMap.put(p.getPropertyID(), tenantService.propertyHasCurrentTenant(p));
        }
        model.addAttribute("tenantMap", tenantMap);
        return "edit-receipt";
    }

    @GetMapping("/{id}/edit")
    public String editReceiptForm(@PathVariable Long id, Model model) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        if (receipt.isEmpty()) return "redirect:/dashboard";
        model.addAttribute("receipt", receipt.get());
        model.addAttribute("businesses", businessService.getAllBusinesses());
        model.addAttribute("properties", propertyService.getAllProperties());
        Map<Long, Boolean> tenantMap = new java.util.HashMap<>();
        for (Property p : propertyService.getAllProperties()) {
            tenantMap.put(p.getPropertyID(), tenantService.propertyHasCurrentTenant(p));
        }
        model.addAttribute("tenantMap", tenantMap);
        return "edit-receipt";
    }

    @PostMapping("/{id}/edit")
    public String updateReceipt(@PathVariable Long id,
                                @RequestParam(required = false) Long businessId,
                                @RequestParam(required = false) Long propertyId,
                                @RequestParam BigDecimal amount,
                                @RequestParam String receiptDate,
                                @RequestParam(required = false) String notes,
                                @RequestParam(required = false) String paymentMethod,
                                @RequestParam(required = false) String paymentType,
                                @RequestParam(required = false) BigDecimal securityDeposit,
                                @RequestParam(required = false, defaultValue = "save") String action,
                                RedirectAttributes redirectAttributes) {
        Receipt receipt = receiptService.getReceiptById(id).orElse(null);
        if (receipt == null) return "redirect:/dashboard";
        Business business = businessId != null ?
                businessService.getBusinessById(businessId).orElse(null) : null;
        Property property = propertyId != null ?
                propertyService.getPropertyById(propertyId).orElse(null) : null;
        receipt.setAmount(amount);
        receipt.setReceiptDate(LocalDate.parse(receiptDate));
        receipt.setNotes(notes);
        receipt.setBusiness(business);
        receipt.setProperty(property);
        receipt.setPaymentMethod(paymentMethod);
        receipt.setPaymentType(paymentType);
        receipt.setSecurityDeposit(securityDeposit != null ? securityDeposit : BigDecimal.ZERO);
        receiptService.saveReceipt(receipt);
        emailService.sendReceiptEmail(receipt);
        if ("print".equals(action)) {
            return "redirect:/receipts/" + id + "/print-redirect";
        }
        redirectAttributes.addFlashAttribute("success", "Receipt updated successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/delete")
    public String deleteReceipt(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        receiptService.deleteReceipt(id);
        redirectAttributes.addFlashAttribute("success", "Receipt deleted successfully.");
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/print")
    public String printReceipt(@PathVariable Long id, Model model) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        if (receipt.isEmpty()) return "redirect:/dashboard";
        model.addAttribute("receipt", receipt.get());
        return "receipt-print";
    }

    @GetMapping("/{id}/print-redirect")
    public String printRedirect(@PathVariable Long id, Model model) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        if (receipt.isEmpty()) return "redirect:/dashboard";
        model.addAttribute("receipt", receipt.get());
        return "print-redirect";
    }
}