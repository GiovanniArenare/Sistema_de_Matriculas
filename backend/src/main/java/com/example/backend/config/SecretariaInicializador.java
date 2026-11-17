
package com.example.backend.config;

import com.example.backend.model.Secretaria;
import com.example.backend.model.enums.PerfilUsuario;
import com.example.backend.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class SecretariaInicializador {

    @Bean
    public CommandLineRunner criarSecretaria(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "secretaria@universidade.com";
            String senha = System.getenv("SECRETARIA_TEST_PASSWORD");

            if (usuarioRepository.existsByEmail(email)) {
                System.out.println("Secretaria de teste já cadastrada.");
                return;
            }

            if (senha == null || senha.isBlank()) {
                throw new IllegalStateException(
                        "Configure SECRETARIA_TEST_PASSWORD para criar a secretaria."
                );
            }

            Secretaria secretaria = new Secretaria();

            secretaria.setNome("Secretaria Acadêmica");
            secretaria.setEmail(email);
            secretaria.setSenhaHash(passwordEncoder.encode(senha));
            secretaria.setPerfil(PerfilUsuario.SECRETARIA);
            secretaria.setRegistroFuncional("SEC001");

            usuarioRepository.save(secretaria);

            System.out.println("Secretaria de teste cadastrada com sucesso!");
        };
    }
}
