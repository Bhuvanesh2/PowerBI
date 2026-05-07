package com.example.erp.purchase;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
  Optional<PurchaseOrder> findByPoNumber(String poNumber);
}

