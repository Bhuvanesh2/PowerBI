package com.example.erp.auth;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtAuthFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring("Bearer ".length()).trim();
      try {
        Claims claims = jwtService.parse(token);
        String username = claims.getSubject();
        Object rolesObj = claims.get("roles");
        Collection<? extends GrantedAuthority> authorities = toAuthorities(rolesObj);

        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception ignored) {
        // Invalid token; continue without auth (will be rejected by security rules)
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }

  private static Collection<? extends GrantedAuthority> toAuthorities(Object rolesObj) {
    if (rolesObj instanceof List<?> list) {
      return list.stream()
          .map(Object::toString)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toUnmodifiableList());
    }
    return List.of();
  }
}

