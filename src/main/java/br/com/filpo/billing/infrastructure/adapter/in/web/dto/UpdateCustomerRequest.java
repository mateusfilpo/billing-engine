package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(
        @NotBlank(message = "O nome é obrigatório") String name,

        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O e-mail deve ser válido") String email) {
}