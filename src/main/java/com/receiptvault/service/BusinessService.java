package com.receiptvault.service;

import com.receiptvault.entity.Business;
import com.receiptvault.entity.User;
import com.receiptvault.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessService {

    @Autowired
    private BusinessRepository businessRepository;

    public Optional<Business> getBusinessById(Long id) {
        return businessRepository.findById(id);
    }

    public Business saveBusiness(Business business) {
        return businessRepository.save(business);
    }

    public void deleteBusiness(Long id) {
        businessRepository.deleteById(id);
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }
}