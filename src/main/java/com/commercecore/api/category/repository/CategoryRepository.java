package com.commercecore.api.category.repository;

import com.commercecore.api.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Looks up a category by its unique URL slug.
     *
     * <p>Uses {@code @EntityGraph} to eagerly load the sub-categories, seo,
     * and content blocks relations in a single JOIN query, preventing the N+1 problem.
     */
    @EntityGraph(attributePaths = {"subCategories", "seo", "contentBlocks"})
    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    /**
     * Retrieves a paginated list of categories matching a search term on name.
     */
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
