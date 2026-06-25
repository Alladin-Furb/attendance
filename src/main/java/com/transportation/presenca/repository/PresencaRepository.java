package com.transportation.presenca.repository;

import com.transportation.presenca.model.Presenca;
import com.transportation.presenca.model.StatusPresenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, UUID> {
    
    // Buscar presença de um aluno em uma data específica
    Optional<Presenca> findByAlunoIdAndDataPresenca(UUID alunoId, LocalDate dataPresenca);
    
    // Buscar presença de um aluno em um curso e data
    Optional<Presenca> findByAlunoIdAndCursoIdAndDataPresenca(UUID alunoId, UUID cursoId, LocalDate dataPresenca);
    
    // Listar presenças de um aluno em um período
    @Query("SELECT p FROM Presenca p WHERE p.alunoId = :alunoId AND p.dataPresenca BETWEEN :dataInicio AND :dataFim ORDER BY p.dataPresenca DESC")
    List<Presenca> findByAlunoIdAndPeriodo(@Param("alunoId") UUID alunoId, 
                                           @Param("dataInicio") LocalDate dataInicio,
                                           @Param("dataFim") LocalDate dataFim);
    
    // Listar presenças de um curso em um período
    @Query("SELECT p FROM Presenca p WHERE p.cursoId = :cursoId AND p.dataPresenca BETWEEN :dataInicio AND :dataFim ORDER BY p.dataPresenca DESC")
    List<Presenca> findByCursoIdAndPeriodo(@Param("cursoId") UUID cursoId,
                                           @Param("dataInicio") LocalDate dataInicio,
                                           @Param("dataFim") LocalDate dataFim);
    
    // Contar faltas não justificadas de um aluno
    @Query("SELECT COUNT(p) FROM Presenca p WHERE p.alunoId = :alunoId AND p.status IN ('AUSENTE', 'FALTA_NAO_JUSTIFICADA') AND p.dataPresenca BETWEEN :dataInicio AND :dataFim")
    Integer countFaltasNaoJustificadas(@Param("alunoId") UUID alunoId,
                                       @Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim);
    
    // Listar presenças de uma data específica
    List<Presenca> findByDataPresenca(LocalDate dataPresenca);
    
    // Confirmados de viagem em uma data (intenção de embarque ou já presentes)
    @Query("SELECT p FROM Presenca p WHERE p.dataPresenca = :data AND p.status IN :statuses ORDER BY p.alunoNome ASC")
    List<Presenca> findConfirmados(@Param("data") LocalDate data,
                                   @Param("statuses") List<StatusPresenca> statuses);

    // Confirmados de viagem em uma data, filtrados por curso
    @Query("SELECT p FROM Presenca p WHERE p.dataPresenca = :data AND p.cursoId = :cursoId AND p.status IN :statuses ORDER BY p.alunoNome ASC")
    List<Presenca> findConfirmadosByCurso(@Param("data") LocalDate data,
                                          @Param("cursoId") UUID cursoId,
                                          @Param("statuses") List<StatusPresenca> statuses);
    
    // Listar presenças não justificadas de um aluno
    @Query("SELECT p FROM Presenca p WHERE p.alunoId = :alunoId AND p.justificado = false AND p.status IN ('AUSENTE', 'FALTA_NAO_JUSTIFICADA') ORDER BY p.dataPresenca DESC")
    List<Presenca> findFaltasNaoJustificadas(@Param("alunoId") UUID alunoId);
    
    // Listar presenças com status específico
    List<Presenca> findByStatusAndDataPresenca(StatusPresenca status, LocalDate dataPresenca);

    // Reservas (PENDENTE) cuja data já passou — usadas para cancelamento automático
    List<Presenca> findByStatusAndDataPresencaBefore(StatusPresenca status, LocalDate data);
}
