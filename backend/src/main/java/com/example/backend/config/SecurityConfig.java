package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

                // Cadastro público de alunos
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/usuarios/alunos"
                ).permitAll()

                .requestMatchers("/error").permitAll()

                // Rotas exclusivas da secretaria
                .requestMatchers("/api/secretaria/**")
                    .hasRole("SECRETARIA")

                // Rotas exclusivas dos professores
                .requestMatchers("/api/professores/**")
                    .hasRole("PROFESSOR")

                // Rotas exclusivas dos alunos
                .requestMatchers("/api/alunos/**")
                    .hasRole("ALUNO")

                // Demais rotas exigem autenticação
                .anyRequest().authenticated()
            )

            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/api/usuarios/alunos"
                )
            )

            .formLogin(form -> form.permitAll())
            .httpBasic(basic -> {});

        return http.build();
    }
}