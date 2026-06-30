package com.receiptvault.controller;

import com.receiptvault.entity.Business;
import com.receiptvault.entity.User;
import com.receiptvault.service.BusinessService;
import com.receiptvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listBusinesses(Model model, Principal principal) {
        model.addAttribute("businesses", businessService.getAllBusinesses());
        return "businesses";
    }

    @GetMapping("/new")
    public String newBusinessForm(Model model) {
        model.addAttribute("business", new Business());
        return "business-form";
    }

    @PostMapping("/new")
    public String createBusiness(@RequestParam String businessName,
                                 @RequestParam String ownerName,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 @RequestParam(required = false) String website,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        Business business = new Business();
        business.setBusinessName(businessName);
        business.setOwnerName(ownerName);
        business.setEmail(email);
        business.setPhone(phone);
        business.setAddress(address);
        business.setCity(city);
        business.setState(state);
        business.setZipCode(zipCode);
        business.setWebsite(website);
        business.setUser(user);
        businessService.saveBusiness(business);
        redirectAttributes.addFlashAttribute("success", "Business created successfully!");
        return "redirect:/businesses";
    }

    @GetMapping("/{id}/edit")
    public String editBusinessForm(@PathVariable Long id, Model model) {
        Business business = businessService.getBusinessById(id).orElse(null);
        if (business == null) return "redirect:/businesses";
        model.addAttribute("business", business);
        return "business-form";
    }

    @PostMapping("/{id}/edit")
    public String updateBusiness(@PathVariable Long id,
                                 @RequestParam String businessName,
                                 @RequestParam String ownerName,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 @RequestParam(required = false) String website,
                                 RedirectAttributes redirectAttributes) {
        Business business = businessService.getBusinessById(id).orElse(null);
        if (business == null) return "redirect:/businesses";
        business.setBusinessName(businessName);
        business.setOwnerName(ownerName);
        business.setEmail(email);
        business.setPhone(phone);
        business.setAddress(address);
        business.setCity(city);
        business.setState(state);
        business.setZipCode(zipCode);
        business.setWebsite(website);
        businessService.saveBusiness(business);
        redirectAttributes.addFlashAttribute("success", "Business updated successfully!");
        return "redirect:/businesses";
    }

    @PostMapping("/{id}/delete")
    public String deleteBusiness(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        businessService.deleteBusiness(id);
        redirectAttributes.addFlashAttribute("success", "Business deleted.");
        return "redirect:/businesses";
    }
}