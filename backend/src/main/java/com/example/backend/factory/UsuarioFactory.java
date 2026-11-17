package com.example.backend.factory;

import com.example.backend.dto.CadastroUsuarioDTO;
import com.example.backend.model.Usuario;
import com.example.backend.model.Aluno;
import com.example.backend.model.Professor;
import com.example.backend.model.Secretaria;

import org.springframework.stereotype.Component;

@Component
public class UsuarioFactory {

    public Usuario criar(CadastroUsuarioDTO dto) {

        Usuario usuario;

        switch (dto.perfil()) {

            case ALUNO -> {
                Aluno aluno = new Aluno();
                aluno.setNumeroMatricula(dto.numeroMatricula());
                usuario = aluno;
            }

            case PROFESSOR -> {
                Professor professor = new Professor();
                professor.setRegistro(dto.registro());
                usuario = professor;
            }

            case SECRETARIA -> {
                Secretaria secretaria = new Secretaria();
                secretaria.setRegistroFuncional(dto.registroFuncional());
                usuario = secretaria;
            }

            default -> throw new IllegalArgumentException(
                    "Perfil de usuário inválido."
            );
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setPerfil(dto.perfil());

        return usuario;
    }
}