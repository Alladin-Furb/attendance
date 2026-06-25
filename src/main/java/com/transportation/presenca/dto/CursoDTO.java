package com.transportation.presenca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDTO {
    
    private UUID id;
    
    private String codigo;
    
    private String nome;
    
    private String descricao;
    
    private String faculdade;
    
    private String campus;
    
    private String enderecoDestino;
    
    private String cidade;
    
    private LocalTime horarioPartida;
    
    private LocalTime horarioRetorno;
    
    private LocalDate dataInicio;
    
    private LocalDate dataFim;
    
    // Dias úteis da viagem (exclui sábados e domingos), calculado na leitura.
    private List<LocalDate> dias;
    
    private String periodo;
    
    private Boolean ativo;
    
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
}
