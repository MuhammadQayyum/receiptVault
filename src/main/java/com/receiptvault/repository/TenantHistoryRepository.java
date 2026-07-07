package com.receiptvault.repository;

import com.receiptvault.entity.Property;
import com.receiptvault.entity.Tenant;
import com.receiptvault.entity.TenantHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantHistoryRepository extends JpaRepository<TenantHistory, Long> {

    List<TenantHistory> findByPropertyOrderByMoveInDateDesc(Property property);

    Optional<TenantHistory> findByPropertyAndMoveOutDateIsNull(Property property);

    boolean existsByPropertyAndMoveOutDateIsNull(Property property);

    List<TenantHistory> findByTenantOrderByMoveInDateDesc(Tenant tenant);

    @Query("SELECT th FROM TenantHistory th WHERE th.property = :property ORDER BY CASE WHEN th.moveOutDate IS NULL THEN 0 ELSE 1 END, th.moveInDate DESC")
    List<TenantHistory> findByPropertyOrderByCurrentFirst(@Param("property") Property property);
}