package com.example.erp.hr;

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
@RequestMapping("/api/hr/employees")
public class EmployeeController {
  private final EmployeeRepository repo;

  public EmployeeController(EmployeeRepository repo) {
    this.repo = repo;
  }

  public record EmployeeUpsert(
      @NotBlank @Size(max = 40) String employeeCode,
      @NotBlank @Size(max = 160) String fullName,
      @Size(max = 160) String email,
      @Size(max = 40) String phone,
      @Size(max = 120) String department,
      Boolean active
  ) {}

  @GetMapping
  public List<Employee> list() {
    return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
  }

  @PostMapping
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Employee create(@Valid @RequestBody EmployeeUpsert req) {
    repo.findByEmployeeCode(req.employeeCode()).ifPresent(e -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee code already exists");
    });
    Employee e = new Employee();
    apply(e, req);
    return repo.save(e);
  }

  @PutMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Employee update(@PathVariable long id, @Valid @RequestBody EmployeeUpsert req) {
    Employee e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!e.getEmployeeCode().equals(req.employeeCode())) {
      repo.findByEmployeeCode(req.employeeCode()).ifPresent(existing -> {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee code already exists");
      });
    }
    apply(e, req);
    return repo.save(e);
  }

  @DeleteMapping("/{id}")
  @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void delete(@PathVariable long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    repo.deleteById(id);
  }

  private static void apply(Employee e, EmployeeUpsert req) {
    e.setEmployeeCode(req.employeeCode());
    e.setFullName(req.fullName());
    e.setEmail(req.email());
    e.setPhone(req.phone());
    e.setDepartment(req.department());
    if (req.active() != null) e.setActive(req.active());
  }
}

