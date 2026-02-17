package com.zyra.backend.Configuration;

import java.util.List;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configureHttpSecurity(http);
        return http.build();
    }

    private void configureHttpSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated())

                // 👇 Añadir cabeceras de seguridad recomendadas
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)) // 1 año
                        .contentTypeOptions(Customizer.withDefaults()) // X-Content-Type-Options:
                                                                       // nosniff
                        .cacheControl(Customizer.withDefaults()) // Cache-Control: no-cache,
                                                                 // no-store, must-revalidate
                        .frameOptions(frame -> frame.deny())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                        "script-src 'self'; " +
                                        "style-src 'self'; " +
                                        "img-src 'self' data: http://localhost:8080; "
                                        +
                                        "font-src 'self'; " +
                                        "connect-src 'self' http://localhost:8080; "
                                        +
                                        "object-src 'none'; " +
                                        "frame-ancestors 'none'; " +
                                        "base-uri 'self';")));

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Configuración general para toda la API
        source.registerCorsConfiguration("/**", configuration);

        // Configuración específica para Swagger UI
        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowedOrigins(List.of("http://localhost:3000"));
        swaggerConfig.setAllowedMethods(List.of("GET"));
        swaggerConfig.setAllowedHeaders(List.of("*"));
        swaggerConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);

        return source;
    }
}
