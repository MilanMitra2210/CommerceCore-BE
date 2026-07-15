package com.commercecore.api.cms.repository;

import com.commercecore.api.cms.entity.CMSPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for CMSPage Entity.
 */
@Repository
public interface CMSPageRepository extends JpaRepository<CMSPage, UUID>, JpaSpecificationExecutor<CMSPage> {

    @EntityGraph(attributePaths = {"seo", "contentBlocks"})
    Optional<CMSPage> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Page<CMSPage> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
