package br.com.fintech.fintech_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
// --- Imports de PasswordEncoder e BCryptPasswordEncoder REMOVIDOS ---
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // --- O @Bean PasswordEncoder FOI REMOVIDO DAQUI ---
    // (Ele foi movido para o ApplicationConfig.java)

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {}) // Habilita a configuração de CORS do WebConfig
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (login e cadastro)
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/usuarios").permitAll()
                        // Todos os outros endpoints exigem autenticação
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}