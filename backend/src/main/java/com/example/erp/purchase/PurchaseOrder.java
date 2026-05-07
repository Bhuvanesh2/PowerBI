package com.example.erp.purchase;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "po_number", nullable = false, unique = true, length = 40)
  private String poNumber;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "supplier_id")
  private Supplier supplier;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

  @Column(name = "ordered_at")
  private Instant orderedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PurchaseOrderLine> lines = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public String getPoNumber() {
    return poNumber;
  }

  public void setPoNumber(String poNumber) {
    this.poNumber = poNumber;
  }

  public Supplier getSupplier() {
    return supplier;
  }

  public void setSupplier(Supplier supplier) {
    this.supplier = supplier;
  }

  public PurchaseOrderStatus getStatus() {
    return status;
  }

  public void setStatus(PurchaseOrderStatus status) {
    this.status = status;
  }

  public Instant getOrderedAt() {
    return orderedAt;
  }

  public void setOrderedAt(Instant orderedAt) {
    this.orderedAt = orderedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<PurchaseOrderLine> getLines() {
    return lines;
  }
}

