package com.commercecore.api.category.repository;

import com.commercecore.api.category.entity.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    @EntityGraph(attributePaths = {"category", "seo", "contentBlocks"})
    Optional<SubCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<SubCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Paginated search for subcategories belonging to a parent category.
     */
    Page<SubCategory> findByCategoryId(UUID categoryId, Pageable pageable);

}
