package com.commercecore.api.catalog.repository;

import com.commercecore.api.catalog.entity.ProductFilterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductFilterValueRepository extends JpaRepository<ProductFilterValue, UUID> {

    @Modifying
    @Query("DELETE FROM ProductFilterValue p WHERE p.product.id = :productId")
    void deleteByProductId(@Param("productId") UUID productId);

    List<ProductFilterValue> findByProductId(UUID productId);

}
