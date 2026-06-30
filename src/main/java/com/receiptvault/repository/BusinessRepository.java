package com.receiptvault.repository;

import com.receiptvault.entity.Business;
import com.receiptvault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
}