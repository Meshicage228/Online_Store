package com.example.adminblservice.repository;

import com.example.adminblservice.entity.product.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchases, Integer> {

    List<Purchases> findByDateOfPurchaseBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT p.user) FROM Purchases p WHERE p.dateOfPurchase >= :since")
    Long countActiveUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT p.user) FROM Purchases p WHERE p.dateOfPurchase >= :since AND p.user NOT IN " +
            "(SELECT DISTINCT p2.user FROM Purchases p2 WHERE p2.dateOfPurchase < :since)")
    Long countNewUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT p.user) FROM Purchases p")
    Long findDistinctUsersWithPurchases();
}