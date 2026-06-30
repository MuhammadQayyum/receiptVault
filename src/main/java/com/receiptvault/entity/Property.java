package com.receiptvault.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "property")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "propertyID")
    private Long propertyID;

    @Column(name = "property_name", nullable = false, length = 150)
    private String propertyName;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(name = "unit_number", length = 20)
    private String unitNumber;

    @Column(name = "rent_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentPrice;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    // Getters and Setters
    public Long getPropertyID() { return propertyID; }
    public void setPropertyID(Long propertyID) { this.propertyID = propertyID; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getUnitNumber() { return unitNumber; }
    public void setUnitNumber(String unitNumber) { this.unitNumber = unitNumber; }

    public BigDecimal getRentPrice() { return rentPrice; }
    public void setRentPrice(BigDecimal rentPrice) { this.rentPrice = rentPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}