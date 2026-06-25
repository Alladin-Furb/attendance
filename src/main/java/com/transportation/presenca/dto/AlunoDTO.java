package com.transportation.presenca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoDTO {
    
    private UUID id;
    
    private String matricula;
    
    private String cpf;
    
    private String nome;
    
    private String email;
    
    private String telefone;
    
    private String rotaTransporte;
    
    private UUID cursoId;
    
    private String nomeCurso;
    
    private String faculdade;
    
    private Boolean ativo;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
