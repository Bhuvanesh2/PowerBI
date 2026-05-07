package com.example.erp.accounts;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/api/accounts/accounts")
public class AccountController {
  private final AccountRepository repo;

  public AccountController(AccountRepository repo) {
    this.repo = repo;
  }

  public record AccountUpsert(
      @NotBlank @Size(max = 40) String code,
      @NotBlank @Size(max = 200) String name,
      @NotNull AccountType type,
      Boolean active
  ) {}

  @GetMapping
  public List<Account> list() {
    return repo.findAll(Sort.by(Sort.Direction.ASC, "code"));
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Account create(@Valid @RequestBody AccountUpsert req) {
    repo.findByCode(req.code()).ifPresent(a -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Account code already exists");
    });
    Account a = new Account();
    apply(a, req);
    return repo.save(a);
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Account update(@PathVariable long id, @Valid @RequestBody AccountUpsert req) {
    Account a = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!a.getCode().equals(req.code())) {
      repo.findByCode(req.code()).ifPresent(existing -> {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Account code already exists");
      });
    }
    apply(a, req);
    return repo.save(a);
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void delete(@PathVariable long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    repo.deleteById(id);
  }

  private static void apply(Account a, AccountUpsert req) {
    a.setCode(req.code());
    a.setName(req.name());
    a.setType(req.type());
    if (req.active() != null) a.setActive(req.active());
  }
}

