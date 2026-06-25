package com.transportation.presenca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;
    
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;
    
    @Column(name = "descricao", length = 500)
    private String descricao;
    
    @Column(name = "faculdade", nullable = false, length = 255)
    private String faculdade;
    
    @Column(name = "campus", length = 255)
    private String campus;
    
    @Column(name = "endereco_destino", length = 500)
    private String enderecoDestino;
    
    @Column(name = "cidade", length = 255)
    private String cidade;
    
    @Column(name = "horario_partida")
    private LocalTime horarioPartida;
    
    @Column(name = "horario_retorno")
    private LocalTime horarioRetorno;
    
    @Column(name = "data_inicio")
    private LocalDate dataInicio;
    
    @Column(name = "data_fim")
    private LocalDate dataFim;
    
    @Column(name = "periodo", nullable = false, length = 50)
    private String periodo;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
