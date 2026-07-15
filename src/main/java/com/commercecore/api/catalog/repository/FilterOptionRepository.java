package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.FilterOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FilterOptionRepository extends JpaRepository<FilterOption, UUID> {
}
