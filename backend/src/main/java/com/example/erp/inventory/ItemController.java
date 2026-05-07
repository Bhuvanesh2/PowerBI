package com.example.erp.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
@RequestMapping("/api/inventory/items")
public class ItemController {
  private final ItemRepository repo;

  public ItemController(ItemRepository repo) {
    this.repo = repo;
  }

  public record ItemUpsert(
      @NotBlank @Size(max = 64) String sku,
      @NotBlank @Size(max = 180) String name,
      @NotBlank @Size(max = 24) String unit,
      @NotNull @DecimalMin("0.00") BigDecimal unitPrice
  ) {}

  @GetMapping
  public List<Item> list() {
    return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Item create(@Valid @RequestBody ItemUpsert req) {
    repo.findBySku(req.sku()).ifPresent(i -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
    });
    Item i = new Item();
    apply(i, req);
    return repo.save(i);
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Item update(@PathVariable long id, @Valid @RequestBody ItemUpsert req) {
    Item i = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!i.getSku().equals(req.sku())) {
      repo.findBySku(req.sku()).ifPresent(existing -> {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
      });
    }
    apply(i, req);
    return repo.save(i);
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void delete(@PathVariable long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    repo.deleteById(id);
  }

  private static void apply(Item i, ItemUpsert req) {
    i.setSku(req.sku());
    i.setName(req.name());
    i.setUnit(req.unit());
    i.setUnitPrice(req.unitPrice());
  }
}

