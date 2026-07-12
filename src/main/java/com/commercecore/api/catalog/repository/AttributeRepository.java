package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {

    Optional<Attribute> findBySlug(String slug);

    boolean existsBySlug(String slug);

}
