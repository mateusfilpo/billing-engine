package br.com.filpo.billing.infrastructure.adapter.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.CustomerJpaEntity;

@Repository
public interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
}
