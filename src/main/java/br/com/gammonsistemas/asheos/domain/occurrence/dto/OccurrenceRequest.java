package br.com.gammonsistemas.asheos.domain.occurrence.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OccurrenceRequest(
        @NotNull(message = "O título é obrigatório") @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres") String title,
        @NotNull(message = "A descrição é obrigatória") @Size(max = 2000, message = "A descrição não pode exceder 2000 caracteres") String description) {
}
