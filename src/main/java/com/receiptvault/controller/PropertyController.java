package com.receiptvault.controller;

import com.receiptvault.entity.Property;
import com.receiptvault.entity.User;
import com.receiptvault.service.PropertyService;
import com.receiptvault.service.TenantService;
import com.receiptvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public String listProperties(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        Map<Long, Boolean> tenantMap = new java.util.HashMap<>();
        for (Property p : properties) {
            tenantMap.put(p.getPropertyID(), tenantService.propertyHasCurrentTenant(p));
        }
        model.addAttribute("properties", properties);
        model.addAttribute("tenantMap", tenantMap);
        return "properties";
    }

    @GetMapping("/new")
    public String newPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "property-form";
    }

    @PostMapping("/new")
    public String createProperty(@RequestParam String propertyName,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 @RequestParam(required = false) String unitNumber,
                                 @RequestParam BigDecimal rentPrice,
                                 @RequestParam(required = false) String description,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        Property property = new Property();
        property.setPropertyName(propertyName);
        property.setAddress(address);
        property.setCity(city);
        property.setState(state);
        property.setZipCode(zipCode);
        property.setUnitNumber(unitNumber);
        property.setRentPrice(rentPrice);
        property.setDescription(description);
        property.setUser(user);
        propertyService.saveProperty(property);
        redirectAttributes.addFlashAttribute("success", "Property added successfully!");
        return "redirect:/properties";
    }

    @GetMapping("/{id}/edit")
    public String editPropertyForm(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id).orElse(null);
        if (property == null) return "redirect:/properties";
        model.addAttribute("property", property);
        return "property-form";
    }

    @PostMapping("/{id}/edit")
    public String updateProperty(@PathVariable Long id,
                                 @RequestParam String propertyName,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 @RequestParam(required = false) String unitNumber,
                                 @RequestParam BigDecimal rentPrice,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        Property property = propertyService.getPropertyById(id).orElse(null);
        if (property == null) return "redirect:/properties";
        property.setPropertyName(propertyName);
        property.setAddress(address);
        property.setCity(city);
        property.setState(state);
        property.setZipCode(zipCode);
        property.setUnitNumber(unitNumber);
        property.setRentPrice(rentPrice);
        property.setDescription(description);
        propertyService.saveProperty(property);
        redirectAttributes.addFlashAttribute("success", "Property updated successfully!");
        return "redirect:/properties";
    }

    @PostMapping("/{id}/delete")
    public String deleteProperty(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        propertyService.deleteProperty(id);
        redirectAttributes.addFlashAttribute("success", "Property deleted.");
        return "redirect:/properties";
    }
}