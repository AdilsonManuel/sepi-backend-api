/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.config;

import com.sepi.sepi_backend.security.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author azm
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true) // ATIVA @PreAuthorize
public class SecurityConfig
{

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder ()
    {
        // Encriptação de senha obrigatória (RNF10)
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception
    {
        http
                // 1. Desabilita CSRF
                .csrf(csrf -> csrf.disable())
                // 2. Define a política de sessão como Stateless (uso de JWT)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. Define as regras de autorização
                .authorizeHttpRequests(authorize -> authorize
                // Endpoints públicos (Acesso total sem autenticação)
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/localidades/**").permitAll()
                // ANTERIORMENTE: .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR") -> REMOVIDO

                // Todos os outros endpoints requerem autenticação
                .anyRequest().authenticated()
                );

        // 4. Adiciona o filtro JWT
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
