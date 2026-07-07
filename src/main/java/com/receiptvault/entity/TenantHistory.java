package com.receiptvault.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tenant_history")
public class TenantHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historyID")
    private Long historyID;

    @ManyToOne
    @JoinColumn(name = "tenantID", nullable = false)
    private Tenant tenant;

    @ManyToOne
    @JoinColumn(name = "propertyID", nullable = false)
    private Property property;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    public boolean isCurrent() {
        return moveOutDate == null;
    }

    // Getters and Setters
    public Long getHistoryID() {
        return historyID;
    }

    public void setHistoryID(Long historyID) {
        this.historyID = historyID;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }
}