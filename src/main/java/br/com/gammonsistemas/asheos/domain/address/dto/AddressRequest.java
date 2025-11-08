package br.com.gammonsistemas.asheos.domain.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "O nome da rua é obrigatório.") @Size(max = 255) String street,
        @NotBlank(message = "O nome da cidade é obrigatório.") @Size(max = 100) String city,
        @NotBlank(message = "O estado é obrigatório.") @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres (ex: SP).") String state,
        @NotBlank(message = "O CEP é obrigatório.") @Size(max = 20) String zipCode) {

}
