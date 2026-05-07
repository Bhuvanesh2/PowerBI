package com.example.erp.auth;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {
  private final AuthenticationManager authManager;
  private final JwtService jwtService;

  public AuthController(AuthenticationManager authManager, JwtService jwtService) {
    this.authManager = authManager;
    this.jwtService = jwtService;
  }

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

  @PostMapping("/api/auth/login")
  public Map<String, Object> login(@RequestBody LoginRequest req) {
    try {
      Authentication auth = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.username(), req.password())
      );
      String token = jwtService.createToken(auth.getName(), auth.getAuthorities());
      return Map.of(
          "token", token,
          "username", auth.getName(),
          "roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).toList()
      );
    } catch (BadCredentialsException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
  }
}

