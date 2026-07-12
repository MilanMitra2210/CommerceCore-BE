package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {

    Optional<Badge> findByLabelIgnoreCase(String label);

    boolean existsByLabelIgnoreCase(String label);

}
