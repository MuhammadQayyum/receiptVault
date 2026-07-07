package com.receiptvault.service;

import com.receiptvault.entity.Property;
import com.receiptvault.entity.Tenant;
import com.receiptvault.entity.TenantHistory;
import com.receiptvault.repository.TenantHistoryRepository;
import com.receiptvault.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantHistoryRepository tenantHistoryRepository;

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAllByOrderByLastNameAsc();
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    public Optional<TenantHistory> getCurrentHistoryByProperty(Property property) {
        return tenantHistoryRepository.findByPropertyAndMoveOutDateIsNull(property);
    }

    public boolean propertyHasCurrentTenant(Property property) {
        return tenantHistoryRepository.existsByPropertyAndMoveOutDateIsNull(property);
    }

    public List<TenantHistory> getTenantHistoryByProperty(Property property) {
        return tenantHistoryRepository.findByPropertyOrderByCurrentFirst(property);
    }

    public List<TenantHistory> getTenantHistoryByTenant(Tenant tenant) {
        return tenantHistoryRepository.findByTenantOrderByMoveInDateDesc(tenant);
    }

    public Tenant saveTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    public TenantHistory saveHistory(TenantHistory history) {
        return tenantHistoryRepository.save(history);
    }

    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }

    public void moveOutTenant(Property property, LocalDate moveOutDate) {
        tenantHistoryRepository.findByPropertyAndMoveOutDateIsNull(property)
                .ifPresent(h -> {
                    h.setMoveOutDate(moveOutDate);
                    tenantHistoryRepository.save(h);
                });
    }

    public void assignTenantToProperty(Tenant tenant, Property property, LocalDate moveInDate) {
        // Move out current tenant if exists
        moveOutTenant(property, moveInDate);

        // Create new history record
        TenantHistory history = new TenantHistory();
        history.setTenant(tenant);
        history.setProperty(property);
        history.setMoveInDate(moveInDate);
        tenantHistoryRepository.save(history);
    }
}