
package com.example.backend.service;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.Professor;
import com.example.backend.model.Usuario;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.dto.AlunoMatriculadoDTO;
import com.example.backend.model.Aluno;
import com.example.backend.model.enums.StatusMatricula;
import com.example.backend.repository.MatriculaRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorConsultaService {

    private final UsuarioRepository usuarioRepository;
    private final OfertaDisciplinaService ofertaService;
    private final MatriculaRepository matriculaRepository;

    public ProfessorConsultaService(
            UsuarioRepository usuarioRepository,
            OfertaDisciplinaService ofertaService,
            MatriculaRepository matriculaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.ofertaService = ofertaService;
        this.matriculaRepository = matriculaRepository;
    }

    // Recuperar o professor autenticado pelo e-mail
    private Professor buscarProfessorAutenticado(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException(
                        "Usuário autenticado não encontrado."));

        if (!(usuario instanceof Professor professor)) {
            throw new AccessDeniedException(
                    "Acesso permitido apenas para professores.");
        }

        return professor;
    }

    // Listar as ofertas atribuídas ao professor autenticado
    public List<OfertaDisciplina> listarMinhasOfertas(String email) {

        Professor professor = buscarProfessorAutenticado(email);

        return ofertaService.buscarPorProfessor(professor.getId());
    }

    public List<AlunoMatriculadoDTO> listarAlunosMatriculados(
            String emailProfessor,
            String ofertaId) {

        Professor professor = buscarProfessorAutenticado(emailProfessor);

        OfertaDisciplina oferta = ofertaService.buscarPorId(ofertaId);

        // Impedir que o professor consulte ofertas de outros professores
        if (!professor.getId().equals(oferta.getProfessorId())) {
            throw new AccessDeniedException(
                    "Você não possui permissão para consultar esta oferta.");
        }

        // Buscar somente matrículas inscritas
        return matriculaRepository
                .findByOfertaIdAndStatus(ofertaId, StatusMatricula.INSCRITA)
                .stream()
                .map(matricula -> {

                    var usuario = usuarioRepository.findById(matricula.getAlunoId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Aluno vinculado à matrícula não encontrado."));

                    if (!(usuario instanceof Aluno aluno)) {
                        throw new IllegalStateException(
                                "A matrícula não está vinculada a um aluno válido.");
                    }

                    return new AlunoMatriculadoDTO(
                            aluno.getId(),
                            aluno.getNome(),
                            aluno.getEmail(),
                            aluno.getNumeroMatricula());
                })
                .toList();
    }
}
