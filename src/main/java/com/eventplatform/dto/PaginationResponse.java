package com.eventplatform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Pagination wrapper for list responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponse<T> {

    // List of items in current page
    private List<T> items;

    // Current page number (0-indexed)
    private Integer currentPage;

    // Total number of pages
    private Integer totalPages;

    // Total number of items across all pages
    private Long totalItems;

    // Number of items per page
    private Integer pageSize;

    // Whether this is the first page
    private Boolean isFirst;

    // Whether this is the last page
    private Boolean isLast;

    /**
     * Create pagination response from Spring Page object
     */
    public static <T> PaginationResponse<T> of(Page<T> page) {
        return PaginationResponse.<T>builder()
                .items(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .pageSize(page.getSize())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    /**
     * Create pagination response from list
     */
    public static <T> PaginationResponse<T> of(List<T> items, Integer page, Integer pageSize, Long total) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        return PaginationResponse.<T>builder()
                .items(items)
                .currentPage(page)
                .totalPages(totalPages)
                .totalItems(total)
                .pageSize(pageSize)
                .isFirst(page == 0)
                .isLast(page >= totalPages - 1)
                .build();
    }
}
