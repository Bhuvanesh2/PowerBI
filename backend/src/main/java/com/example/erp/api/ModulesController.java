package com.example.erp.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModulesController {
  @GetMapping("/api/hr")
  public Map<String, Object> hr() {
    return Map.of("module", "HR", "status", "placeholder");
  }

  @GetMapping("/api/inventory")
  public Map<String, Object> inventory() {
    return Map.of("module", "Inventory", "status", "placeholder");
  }

  @GetMapping("/api/purchase")
  public Map<String, Object> purchase() {
    return Map.of("module", "Purchase", "status", "placeholder");
  }

  @GetMapping("/api/reporting")
  public Map<String, Object> reporting() {
    return Map.of("module", "Reporting", "status", "placeholder");
  }

  @GetMapping("/api/accounts")
  public Map<String, Object> accounts() {
    return Map.of("module", "Accounts", "status", "placeholder");
  }
}

