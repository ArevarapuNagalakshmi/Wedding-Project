package com.eventplatform.config;

import com.eventplatform.service.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Stateless JWT
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Disable CSRF for APIs
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/search/**",
                                "/api/payments/webhook",
                                "/api/verify/**"
                        ).permitAll()

                        // 🔓 Public vendor READ APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/vendors/*",
                                "/api/vendors/verified"
                        ).permitAll()

                        // � Uploaded portfolio assets
                        .requestMatchers("/uploads/**").permitAll()

                        // �🔐 Availability APIs (VENDOR only)
                        .requestMatchers("/api/availability/**").hasRole("VENDOR")

                        // 🔐 Admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 🔐 Vendor profile APIs
                        .requestMatchers("/api/vendors/**").hasAnyRole("VENDOR", "ADMIN")

                        // 🔐 Service Package APIs ✅ FIXED
                        .requestMatchers(HttpMethod.POST, "/api/packages/**").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/packages/**").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/packages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/packages/**").permitAll()

                        // 🔐 Payments
                        .requestMatchers(HttpMethod.POST, "/api/payments/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/payments/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/payments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasRole("ADMIN")

                        // 🔐 Bookings
                        .requestMatchers("/api/bookings/**").hasRole("CUSTOMER")

                        // 🔐 Customer profile APIs
                        .requestMatchers("/api/customers/**").hasRole("CUSTOMER")

                        // 🔐 Chat APIs
                        .requestMatchers("/api/chat/**")
                        .hasAnyRole("CUSTOMER", "VENDOR", "ADMIN")

                        // 🔐 Everything else
                        .anyRequest().authenticated()
                )


                // JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }


}
