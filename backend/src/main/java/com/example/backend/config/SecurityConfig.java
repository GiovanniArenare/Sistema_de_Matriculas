
package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Cadastro público somente de alunos
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/usuarios/alunos"
                ).permitAll()

                .requestMatchers("/error").permitAll()

                // Secretaria
                .requestMatchers("/api/secretaria/**")
                    .hasRole("SECRETARIA")

                // Professores
                .requestMatchers("/api/professores/**")
                    .hasRole("PROFESSOR")

                // Alunos
                .requestMatchers("/api/alunos/**")
                    .hasRole("ALUNO")

                // Demais endpoints
                .anyRequest().authenticated()
            )

            // API sem sessão de autenticação
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configuração temporária para API HTTP Basic
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )

            .httpBasic(basic -> {});

        return http.build();
    }
}
