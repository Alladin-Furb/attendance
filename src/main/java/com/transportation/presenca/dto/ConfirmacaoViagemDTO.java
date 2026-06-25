package com.transportation.presenca.dto;

import com.transportation.presenca.model.StatusPresenca;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Projeção enxuta de uma confirmação de viagem do dia, consumida pelo serviço
 * de roteirização (route-gen) para montar a rota com os alunos confirmados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmacaoViagemDTO {

    private UUID alunoId;

    private String alunoMatricula;

    private String alunoNome;

    private UUID cursoId;

    private String cursoNome;

    private StatusPresenca status;

    private LocalDate dataPresenca;
}
