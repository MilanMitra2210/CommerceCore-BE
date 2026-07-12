package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.FilterGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FilterGroupRepository extends JpaRepository<FilterGroup, UUID> {

    /**
     * Loads category filter configurations along with their options.
     */
    @EntityGraph(attributePaths = {"options"})
    List<FilterGroup> findByCategoryIdOrderByDisplayOrderAsc(UUID categoryId);

}
