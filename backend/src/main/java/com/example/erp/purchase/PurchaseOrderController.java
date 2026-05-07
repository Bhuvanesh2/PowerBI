package com.example.erp.purchase;

import com.example.erp.inventory.Item;
import com.example.erp.inventory.ItemRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/purchase/orders")
public class PurchaseOrderController {
  private final PurchaseOrderRepository orders;
  private final SupplierRepository suppliers;
  private final ItemRepository items;

  public PurchaseOrderController(PurchaseOrderRepository orders, SupplierRepository suppliers, ItemRepository items) {
    this.orders = orders;
    this.suppliers = suppliers;
    this.items = items;
  }

  public record LineReq(
      @NotNull Long itemId,
      @NotNull @DecimalMin("0.01") BigDecimal qty,
      @NotNull @DecimalMin("0.00") BigDecimal unitPrice
  ) {}

  public record OrderUpsert(
      @NotBlank @Size(max = 40) String poNumber,
      @NotNull Long supplierId,
      PurchaseOrderStatus status,
      Instant orderedAt,
      @NotNull List<@Valid LineReq> lines
  ) {}

  public record OrderView(
      Long id,
      String poNumber,
      Long supplierId,
      String supplierName,
      PurchaseOrderStatus status,
      Instant orderedAt,
      Instant createdAt,
      List<LineView> lines
  ) {}

  public record LineView(Long id, Long itemId, String itemSku, String itemName, BigDecimal qty, BigDecimal unitPrice) {}

  @GetMapping
  public List<OrderView> list() {
    return orders.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(PurchaseOrderController::toView).toList();
  }

  @GetMapping("/{id}")
  public OrderView get(@PathVariable long id) {
    PurchaseOrder po = orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return toView(po);
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public OrderView create(@Valid @RequestBody OrderUpsert req) {
    orders.findByPoNumber(req.poNumber()).ifPresent(existing -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "PO number already exists");
    });
    PurchaseOrder po = new PurchaseOrder();
    apply(po, req);
    return toView(orders.save(po));
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public OrderView update(@PathVariable long id, @Valid @RequestBody OrderUpsert req) {
    PurchaseOrder po = orders.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!po.getPoNumber().equals(req.poNumber())) {
      orders.findByPoNumber(req.poNumber()).ifPresent(existing -> {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "PO number already exists");
      });
    }
    apply(po, req);
    return toView(orders.save(po));
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void delete(@PathVariable long id) {
    if (!orders.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    orders.deleteById(id);
  }

  private void apply(PurchaseOrder po, OrderUpsert req) {
    Supplier supplier = suppliers.findById(req.supplierId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid supplierId"));

    po.setPoNumber(req.poNumber());
    po.setSupplier(supplier);
    po.setStatus(req.status() == null ? PurchaseOrderStatus.DRAFT : req.status());
    po.setOrderedAt(req.orderedAt());

    po.getLines().clear();
    for (LineReq lr : req.lines()) {
      Item item = items.findById(lr.itemId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid itemId: " + lr.itemId()));
      PurchaseOrderLine line = new PurchaseOrderLine();
      line.setPurchaseOrder(po);
      line.setItem(item);
      line.setQty(lr.qty());
      line.setUnitPrice(lr.unitPrice());
      po.getLines().add(line);
    }
  }

  private static OrderView toView(PurchaseOrder po) {
    Supplier s = po.getSupplier();
    List<LineView> lines = po.getLines().stream().map(l -> {
      Item i = l.getItem();
      return new LineView(l.getId(), i.getId(), i.getSku(), i.getName(), l.getQty(), l.getUnitPrice());
    }).toList();
    return new OrderView(
        po.getId(),
        po.getPoNumber(),
        s.getId(),
        s.getName(),
        po.getStatus(),
        po.getOrderedAt(),
        po.getCreatedAt(),
        lines
    );
  }
}

