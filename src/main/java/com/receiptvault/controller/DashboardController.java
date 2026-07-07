package com.receiptvault.controller;

import com.receiptvault.entity.Receipt;
import com.receiptvault.entity.User;
import com.receiptvault.service.ReceiptService;
import com.receiptvault.service.TenantService;
import com.receiptvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String property,
                            @RequestParam(required = false) Integer month,
                            Model model, Principal principal) {
        int year = Year.now().getValue();
        List<Receipt> receipts = receiptService.searchAllReceipts(property, month);
        model.addAttribute("receipts", receipts);
        model.addAttribute("currentYear", year);
        model.addAttribute("totalCount", receiptService.getTotalReceiptCountAll());
        model.addAttribute("totalSpent", receiptService.getTotalSpentAll());
        model.addAttribute("thisMonthTotal", receiptService.getThisMonthTotalAll());
        model.addAttribute("selectedProperty", property);
        model.addAttribute("selectedMonth", month);
        return "dashboard";
    }
}