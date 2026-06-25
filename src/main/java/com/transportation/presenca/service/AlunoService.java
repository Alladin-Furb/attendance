package com.transportation.presenca.service;

import com.transportation.presenca.dto.AlunoDTO;
import com.transportation.presenca.model.Aluno;
import com.transportation.presenca.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {
    
    private final AlunoRepository alunoRepository;
    
    /**
     * Criar novo aluno
     */
    @Transactional
    public AlunoDTO criarAluno(AlunoDTO dto) {
        // Validar se matricula já existe
        alunoRepository.findByMatricula(dto.getMatricula())
                .ifPresent(a -> {
                    throw new IllegalArgumentException("Matrícula já cadastrada");
                });
        
        // Validar se email já existe
        if (dto.getEmail() != null) {
            alunoRepository.findByEmail(dto.getEmail())
                    .ifPresent(a -> {
                        throw new IllegalArgumentException("Email já cadastrado");
                    });
        }
        
        Aluno aluno = new Aluno();
        aluno.setMatricula(dto.getMatricula());
        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setRotaTransporte(dto.getRotaTransporte());
        aluno.setCursoId(dto.getCursoId());
        aluno.setNomeCurso(dto.getNomeCurso());
        aluno.setFaculdade(dto.getFaculdade());
        aluno.setAtivo(true);

        alunoRepository.save(aluno);
        aluno.setExternalId(aluno.getId());
        alunoRepository.save(aluno);
        return toDTO(aluno);
    }
    
    /**
     * Obter aluno por ID
     */
    public AlunoDTO obterAluno(UUID id) {
        var aluno = buscarPorProfileId(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        return toDTO(aluno);
    }
    
    /**
     * Obter aluno por matrícula
     */
    public AlunoDTO obterAlunoPorMatricula(String matricula) {
        var aluno = alunoRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        return toDTO(aluno);
    }
    
    /**
     * Listar alunos ativos
     */
    public List<AlunoDTO> listarAlunos() {
        return alunoRepository.findByAtivoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar alunos por rota de transporte
     */
    public List<AlunoDTO> listarAlunosPorRota(String rota) {
        return alunoRepository.findByRotaTransporteAndAtivoTrue(rota)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());    }
    
    /**
     * Buscar alunos por termo (nome, CPF ou matrícula) para autocomplete.
     */
    public List<AlunoDTO> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return List.of();
        }
        return alunoRepository.buscarPorTermo(termo.trim())
                .stream()
                .limit(10)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualizar aluno
     */
    @Transactional
    public AlunoDTO atualizarAluno(UUID id, AlunoDTO dto) {
        var aluno = buscarPorProfileId(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        
        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setRotaTransporte(dto.getRotaTransporte());
        
        alunoRepository.save(aluno);
        return toDTO(aluno);
    }
    
    /**
     * Desativar aluno
     */
    @Transactional
    public void desativarAluno(UUID id) {
        var aluno = buscarPorProfileId(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }
    
    /**
     * Mapear Aluno para DTO
     */
    private AlunoDTO toDTO(Aluno aluno) {
        return AlunoDTO.builder()
                .id(aluno.getExternalId() == null ? aluno.getId() : aluno.getExternalId())
                .matricula(aluno.getMatricula())
                .cpf(aluno.getCpf())
                .nome(aluno.getNome())
                .email(aluno.getEmail())
                .telefone(aluno.getTelefone())
                .rotaTransporte(aluno.getRotaTransporte())
                .cursoId(aluno.getCursoId())
                .nomeCurso(aluno.getNomeCurso())
                .faculdade(aluno.getFaculdade())
                .ativo(aluno.getAtivo())
                .criadoEm(aluno.getCriadoEm())
                .atualizadoEm(aluno.getAtualizadoEm())
                .build();
    }

    private java.util.Optional<Aluno> buscarPorProfileId(UUID profileId) {
        return alunoRepository.findByExternalId(profileId)
                .or(() -> alunoRepository.findById(profileId));
    }
}