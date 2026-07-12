package com.commercecore.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Paginated response wrapper for collection endpoints.
 *
 * <p>This contract matches the frontend's expected paginated format:
 * <pre>{@code
 * {
 *   "items": [ ... ],
 *   "page": 1,
 *   "limit": 10,
 *   "total": 156,
 *   "totalPages": 16
 * }
 * }</pre>
 *
 * <p>Usage in a service:
 * <pre>{@code
 * Page<Product> page = productRepository.findAll(pageable);
 * PaginatedResponse<ProductDto> response = PaginatedResponse.of(page, mapper::toDto);
 * return ApiResponse.success("Products retrieved", response);
 * }</pre>
 *
 * @param <T> the type of items in the collection
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> items;
    private int page;
    private int limit;
    private long total;
    private int totalPages;

    /**
     * Creates a PaginatedResponse from a Spring Data {@link org.springframework.data.domain.Page}.
     *
     * @param springPage the Spring Data page result
     * @param mapper     a function to convert entity to DTO
     * @param <E>        the entity type
     * @param <D>        the DTO type
     * @return a PaginatedResponse containing the mapped DTOs
     */
    public static <E, D> PaginatedResponse<D> of(
            org.springframework.data.domain.Page<E> springPage,
            java.util.function.Function<E, D> mapper) {

        return PaginatedResponse.<D>builder()
                .items(springPage.getContent().stream().map(mapper).toList())
                .page(springPage.getNumber() + 1)  // Convert 0-indexed to 1-indexed
                .limit(springPage.getSize())
                .total(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .build();
    }

}
