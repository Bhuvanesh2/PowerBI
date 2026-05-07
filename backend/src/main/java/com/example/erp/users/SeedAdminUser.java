package com.example.erp.users;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedAdminUser implements ApplicationRunner {
  private final AppUserRepository users;
  private final AppRoleRepository roles;
  private final PasswordEncoder encoder;

  public SeedAdminUser(AppUserRepository users, AppRoleRepository roles, PasswordEncoder encoder) {
    this.users = users;
    this.roles = roles;
    this.encoder = encoder;
  }

  @Override
  public void run(ApplicationArguments args) {
    AppRole adminRole = roles.findByName("ROLE_ADMIN").orElseGet(() -> roles.save(new AppRole("ROLE_ADMIN")));
    AppRole userRole = roles.findByName("ROLE_USER").orElseGet(() -> roles.save(new AppRole("ROLE_USER")));

    users.findByUsername("admin").orElseGet(() -> {
      AppUser u = new AppUser();
      u.setUsername("admin");
      u.setPasswordHash(encoder.encode("admin"));
      u.getRoles().add(adminRole);
      u.getRoles().add(userRole);
      return users.save(u);
    });
  }
}

