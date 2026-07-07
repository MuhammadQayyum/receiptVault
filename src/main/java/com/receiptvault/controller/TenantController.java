package com.receiptvault.controller;

import com.receiptvault.entity.Property;
import com.receiptvault.entity.Tenant;
import com.receiptvault.entity.TenantHistory;
import com.receiptvault.service.PropertyService;
import com.receiptvault.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private PropertyService propertyService;

    @GetMapping
    public String listTenants(Model model) {
        model.addAttribute("tenants", tenantService.getAllTenants());
        return "tenants";
    }

    @GetMapping("/new")
    public String newTenantForm(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("tenant", new Tenant());
        return "tenant-form";
    }

    @PostMapping("/new")
    public String createTenant(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String moveInDate,
                               @RequestParam(required = false) Long propertyId,
                               RedirectAttributes redirectAttributes) {
        Tenant tenant = new Tenant();
        tenant.setFirstName(firstName);
        tenant.setLastName(lastName);
        tenant.setEmail(email);
        tenant.setPhone(phone);
        tenantService.saveTenant(tenant);

        if (propertyId != null) {
            Property property = propertyService.getPropertyById(propertyId).orElse(null);
            if (property != null) {
                try {
                    LocalDate date = moveInDate != null && !moveInDate.isEmpty() ?
                            LocalDate.parse(moveInDate) : LocalDate.now();
                    tenantService.assignTenantToProperty(tenant, property, date);
                } catch (RuntimeException e) {
                    redirectAttributes.addFlashAttribute("error", e.getMessage());
                    return "redirect:/tenants";
                }
            }
        }

        redirectAttributes.addFlashAttribute("success", "Tenant added successfully!");
        return "redirect:/tenants";
    }

    @GetMapping("/{id}/edit")
    public String editTenantForm(@PathVariable Long id, Model model) {
        Tenant tenant = tenantService.getTenantById(id).orElse(null);
        if (tenant == null) return "redirect:/tenants";
        model.addAttribute("tenant", tenant);
        model.addAttribute("properties", propertyService.getAllProperties());
        model.addAttribute("tenantHistory", tenantService.getTenantHistoryByTenant(tenant));
        return "tenant-form";
    }

    @PostMapping("/{id}/edit")
    public String updateTenant(@PathVariable Long id,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String phone,
                               RedirectAttributes redirectAttributes) {
        Tenant tenant = tenantService.getTenantById(id).orElse(null);
        if (tenant == null) return "redirect:/tenants";
        tenant.setFirstName(firstName);
        tenant.setLastName(lastName);
        tenant.setEmail(email);
        tenant.setPhone(phone);
        tenantService.saveTenant(tenant);
        redirectAttributes.addFlashAttribute("success", "Tenant updated successfully!");
        return "redirect:/tenants";
    }

    @PostMapping("/{id}/assign")
    public String assignProperty(@PathVariable Long id,
                                 @RequestParam Long propertyId,
                                 @RequestParam(required = false) String moveInDate,
                                 RedirectAttributes redirectAttributes) {
        Tenant tenant = tenantService.getTenantById(id).orElse(null);
        Property property = propertyService.getPropertyById(propertyId).orElse(null);
        if (tenant == null || property == null) return "redirect:/tenants";

        try {
            LocalDate date = moveInDate != null && !moveInDate.isEmpty() ?
                    LocalDate.parse(moveInDate) : LocalDate.now();
            tenantService.assignTenantToProperty(tenant, property, date);
            redirectAttributes.addFlashAttribute("success",
                    tenant.getFullName() + " assigned to " + property.getPropertyName() + " successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/tenants/" + id + "/edit";
    }

    @PostMapping("/{id}/moveout")
    public String moveOutTenant(@PathVariable Long id,
                                @RequestParam(required = false) String moveOutDate,
                                RedirectAttributes redirectAttributes) {
        Tenant tenant = tenantService.getTenantById(id).orElse(null);
        if (tenant == null) return "redirect:/tenants";

        List<TenantHistory> history = tenantService.getTenantHistoryByTenant(tenant);
        TenantHistory current = history.stream()
                .filter(h -> h.getMoveOutDate() == null)
                .findFirst().orElse(null);

        if (current != null) {
            LocalDate date = moveOutDate != null && !moveOutDate.isEmpty() ?
                    LocalDate.parse(moveOutDate) : LocalDate.now();
            tenantService.moveOutTenant(current.getProperty(), date);
            redirectAttributes.addFlashAttribute("success", "Tenant moved out successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "This tenant has no current property.");
        }

        return "redirect:/tenants";
    }

    @PostMapping("/{id}/delete")
    public String deleteTenant(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        tenantService.deleteTenant(id);
        redirectAttributes.addFlashAttribute("success", "Tenant deleted.");
        return "redirect:/tenants";
    }

    @GetMapping("/property/{propertyId}")
    public String tenantHistory(@PathVariable Long propertyId, Model model) {
        Property property = propertyService.getPropertyById(propertyId).orElse(null);
        if (property == null) return "redirect:/tenants";
        model.addAttribute("property", property);
        model.addAttribute("tenants", tenantService.getTenantHistoryByProperty(property));
        return "tenant-history";
    }
}