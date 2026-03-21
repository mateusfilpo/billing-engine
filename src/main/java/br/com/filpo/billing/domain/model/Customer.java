package br.com.filpo.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Customer {

    private final UUID id;
    private final String name;
    private final String email;
    private final String document;
    private String status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Customer createNew(String name, String email, String document) {
        return Customer.builder()
                .name(name)
                .email(email)
                .document(document)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void deactivate() {
        this.status = "INACTIVE";
        this.updatedAt = LocalDateTime.now();
    }
}