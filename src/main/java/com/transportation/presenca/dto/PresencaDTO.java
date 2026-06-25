package com.transportation.presenca.dto;

import com.transportation.presenca.model.StatusPresenca;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresencaDTO {
    
    private UUID id;
    
    private UUID alunoId;
    
    private String alunoMatricula;
    
    private String alunoNome;
    
    private UUID cursoId;
    
    private String cursoNome;
    
    private String alunoCurso;
    
    private String faculdade;
    
    private LocalDate dataPresenca;
    
    private StatusPresenca status;
    
    private LocalDateTime horaEntrada;
    
    private LocalDateTime horaSaida;
    
    private String motivoFalta;
    
    private String justificativa;
    
    private String observacoes;
    
    private Integer horasAulaComparecidas;
    
    private Integer horasAulaTotal;
    
    private UUID professorId;
    
    private String professorNome;
    
    private Boolean justificado;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
