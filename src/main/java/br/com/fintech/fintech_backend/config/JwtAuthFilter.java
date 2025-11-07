package br.com.fintech.fintech_backend.config;

import br.com.fintech.fintech_backend.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // --- IMPORT MANTIDO ---
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    // --- INJEÇÃO ADICIONADA ---
    // Agora o Spring injeta o Bean que criamos no SecurityConfig
    private final UserDetailsService userDetailsService;

    // --- LINHA PROBLEMÁTICA REMOVIDA ---
    // (A linha que criava o userDetailsService aqui foi apagada)

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userCpf;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Remove "Bearer "

        try {
            userCpf = tokenService.getCpfFromToken(jwt);
        } catch (Exception e) {
            // Token inválido (expirado, assinatura errada, etc)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido ou expirado.");
            return;
        }

        if (userCpf != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Agora usa o userDetailsService injetado
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userCpf);

            if (tokenService.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}