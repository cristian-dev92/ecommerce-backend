package com.tienda.ecommerce.security;

import com.tienda.ecommerce.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService; // El único y verdadero gestor de usuarios

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Evitar inspección en peticiones preflight de Angular (CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Permitir acceso directo a endpoints públicos sin buscar token
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("[FILTRO JWT] Cabecera recibida: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        System.out.println("[FILTRO JWT] Email extraído del token: " + email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Buscamos en Neon usando la infraestructura de Spring Security implementada en UserService
            UserDetails userDetails = userService.loadUserByUsername(email);
            System.out.println("[FILTRO JWT] Usuario encontrado en Neon: " + userDetails.getUsername());

            if (jwtService.isTokenValid(token, userDetails)) {

                System.out.println("[FILTRO JWT]  ¡El token es VÁLIDO! Entrando a mapear roles...");
                // Extraemos la colección de roles empaquetados en el JWT
                List<String> rolesClaim = jwtService.extractRoles(token);
                System.out.println("[FILTRO JWT] Roles extraídos del Claim: " + rolesClaim);
                Collection<? extends GrantedAuthority> authorities;

                if (rolesClaim != null && !rolesClaim.isEmpty()) {
                    authorities = rolesClaim.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                } else {
                    authorities = userDetails.getAuthorities();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecemos la sesión en el contexto para que @AuthenticationPrincipal funcione
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("[FILTRO JWT] 🛡️ Usuario autenticado con éxito en el contexto de Spring.");
            }
        }
        System.out.println("[FILTRO JWT] ❌ El token fue RECHAZADO por jwtService.isTokenValid()");
        filterChain.doFilter(request, response);
    }
}