package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String status,
        String createdAt) {
}