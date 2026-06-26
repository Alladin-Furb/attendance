package com.transportation.presenca.service;

import com.transportation.presenca.dto.CursoDTO;
import com.transportation.presenca.model.Curso;
import com.transportation.presenca.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoService {
    
    private final CursoRepository cursoRepository;
    
    /**
     * Criar novo curso
     */
    @Transactional
    public CursoDTO criarCurso(CursoDTO dto) {
        // Código gerado automaticamente caso não informado
        String codigo = (dto.getCodigo() == null || dto.getCodigo().isBlank())
                ? gerarCodigo()
                : dto.getCodigo();
        if (dto.getCodigo() != null && !dto.getCodigo().isBlank()) {
            cursoRepository.findByCodigo(codigo)
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("Código de viagem já cadastrado");
                    });
        }
        validarDatas(dto);
        
        Curso curso = new Curso();
        curso.setCodigo(codigo);
        curso.setNome(dto.getNome());
        curso.setDescricao(dto.getDescricao());
        curso.setFaculdade(dto.getFaculdade());
        curso.setCampus(dto.getCampus());
        curso.setEnderecoDestino(dto.getEnderecoDestino());
        curso.setCidade(dto.getCidade());
        curso.setDestinoLatitude(dto.getDestinoLatitude());
        curso.setDestinoLongitude(dto.getDestinoLongitude());
        curso.setHorarioPartida(dto.getHorarioPartida());
        curso.setHorarioRetorno(dto.getHorarioRetorno());
        curso.setDataInicio(dto.getDataInicio());
        curso.setDataFim(dto.getDataFim());
        curso.setPeriodo(dto.getPeriodo());
        curso.setAtivo(true);
        
        cursoRepository.save(curso);
        return toDTO(curso);
    }
    
    /**
     * Obter curso por ID
     */
    public CursoDTO obterCurso(UUID id) {
        var curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        return toDTO(curso);
    }
    
    /**
     * Obter curso por código
     */
    public CursoDTO obterCursoPorCodigo(String codigo) {
        var curso = cursoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        return toDTO(curso);
    }
    
    /**
     * Listar cursos ativos
     */
    public List<CursoDTO> listarCursos() {
        return cursoRepository.findByAtivoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar cursos por nome
     */
    public List<CursoDTO> buscarCursos(String nome) {
        return cursoRepository.findByNomeContains(nome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cursos de uma faculdade
     */
    public List<CursoDTO> listarCursosPorFaculdade(String faculdade) {
        return cursoRepository.findByFaculdadeAndAtivoTrue(faculdade)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cursos de um campus
     */
    public List<CursoDTO> listarCursosPorCampus(String campus) {
        return cursoRepository.findByCampusAndAtivoTrue(campus)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar cursos de uma faculdade e campus
     */
    public List<CursoDTO> listarCursosPorFaculdadeECampus(String faculdade, String campus) {
        return cursoRepository.findByFaculdadeAndCampus(faculdade, campus)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Atualizar curso
     */
    @Transactional
    public CursoDTO atualizarCurso(UUID id, CursoDTO dto) {
        var curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        
        curso.setNome(dto.getNome());
        curso.setDescricao(dto.getDescricao());
        curso.setFaculdade(dto.getFaculdade());
        curso.setCampus(dto.getCampus());
        curso.setEnderecoDestino(dto.getEnderecoDestino());
        curso.setCidade(dto.getCidade());
        curso.setDestinoLatitude(dto.getDestinoLatitude());
        curso.setDestinoLongitude(dto.getDestinoLongitude());
        curso.setHorarioPartida(dto.getHorarioPartida());
        curso.setHorarioRetorno(dto.getHorarioRetorno());
        curso.setDataInicio(dto.getDataInicio());
        curso.setDataFim(dto.getDataFim());
        curso.setPeriodo(dto.getPeriodo());
        
        cursoRepository.save(curso);
        return toDTO(curso);
    }
    
    /**
     * Desativar curso
     */
    @Transactional
    public void desativarCurso(UUID id) {
        var curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        curso.setAtivo(false);
        cursoRepository.save(curso);
    }
    
    /**
     * Mapear Curso para DTO
     */
    private CursoDTO toDTO(Curso curso) {
        return CursoDTO.builder()
                .id(curso.getId())
                .codigo(curso.getCodigo())
                .nome(curso.getNome())
                .descricao(curso.getDescricao())
                .faculdade(curso.getFaculdade())
                .campus(curso.getCampus())
                .enderecoDestino(curso.getEnderecoDestino())
                .cidade(curso.getCidade())
                .destinoLatitude(curso.getDestinoLatitude())
                .destinoLongitude(curso.getDestinoLongitude())
                .horarioPartida(curso.getHorarioPartida())
                .horarioRetorno(curso.getHorarioRetorno())
                .dataInicio(curso.getDataInicio())
                .dataFim(curso.getDataFim())
                .dias(diasUteis(curso))
                .periodo(curso.getPeriodo())
                .ativo(curso.getAtivo())
                .criadoEm(curso.getCriadoEm())
                .atualizadoEm(curso.getAtualizadoEm())
                .build();
    }

    /**
     * Dias úteis da viagem (exclui sábados e domingos).
     */
    private List<LocalDate> diasUteis(Curso curso) {
        if (curso.getDataInicio() == null || curso.getDataFim() == null) {
            return List.of();
        }
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = curso.getDataInicio(); !d.isAfter(curso.getDataFim()); d = d.plusDays(1)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                dias.add(d);
            }
        }
        return dias;
    }

    private void validarDatas(CursoDTO dto) {
        if (dto.getDataInicio() != null && dto.getDataInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de início não pode ser anterior a hoje");
        }
        if (dto.getDataInicio() != null && dto.getDataInicio().isBefore(LocalDate.now().plusDays(14))) {
            throw new IllegalArgumentException(
                    "A viagem deve ser criada com no mínimo 14 dias de antecedência da primeira execução");
        }
        if (dto.getDataInicio() != null && dto.getDataFim() != null
                && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new IllegalArgumentException("A data fim não pode ser anterior à data início");
        }
    }

    /**
     * Gera um código único para a viagem automaticamente.
     */
    private String gerarCodigo() {
        String codigo;
        do {
            codigo = "VG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (cursoRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }
}
