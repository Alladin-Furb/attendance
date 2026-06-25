package com.transportation.presenca.dto;

import java.util.UUID;

public record PresencaEventDto(
    UUID id,
    UUID alunoId,
    String alunoNome,
    String dataPresenca,
    String status
) {}