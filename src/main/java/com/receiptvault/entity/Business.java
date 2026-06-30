package com.receiptvault.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "business")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessID")
    private Long businessID;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "website", length = 100)
    private String website;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    // Getters and Setters
    public Long getBusinessID() { return businessID; }
    public void setBusinessID(Long businessID) { this.businessID = businessID; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}