package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        OffsetDateTime timestamp,
        List<FieldError> fields) {
    public record FieldError(String name, String message) {
    }
}