package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = {"category", "subCategory", "seo", "variants", "badges", "infoTabs", "contentBlocks"})
    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);

    Page<Product> findBySubCategoryId(UUID subCategoryId, Pageable pageable);

}
