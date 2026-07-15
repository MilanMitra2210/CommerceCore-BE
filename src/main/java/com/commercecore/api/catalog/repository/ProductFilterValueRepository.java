package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.ProductFilterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductFilterValueRepository extends JpaRepository<ProductFilterValue, UUID> {

    void deleteByProductId(UUID productId);

    List<ProductFilterValue> findByProductId(UUID productId);

}
