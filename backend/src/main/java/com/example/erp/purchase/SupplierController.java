package com.example.erp.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/api/purchase/suppliers")
public class SupplierController {
  private final SupplierRepository repo;

  public SupplierController(SupplierRepository repo) {
    this.repo = repo;
  }

  public record SupplierUpsert(
      @NotBlank @Size(max = 200) String name,
      @Size(max = 160) String email,
      @Size(max = 40) String phone,
      String address
  ) {}

  @GetMapping
  public List<Supplier> list() {
    return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Supplier create(@Valid @RequestBody SupplierUpsert req) {
    Supplier s = new Supplier();
    apply(s, req);
    return repo.save(s);
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Supplier update(@PathVariable long id, @Valid @RequestBody SupplierUpsert req) {
    Supplier s = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    apply(s, req);
    return repo.save(s);
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void delete(@PathVariable long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    repo.deleteById(id);
  }

  private static void apply(Supplier s, SupplierUpsert req) {
    s.setName(req.name());
    s.setEmail(req.email());
    s.setPhone(req.phone());
    s.setAddress(req.address());
  }
}

