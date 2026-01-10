package com.xavier.servicematchbackend.servicecatalog.application.dto;

import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;

public record CategoryResponse(String id, String name, String parentId, String path) {

    public static CategoryResponse from(Category category) {
        String parentId = category.parent() != null ? category.parent().id().toString() : null;
        return new CategoryResponse(
                category.id().toString(),
                category.name(),
                parentId,
                category.path()
        );
    }
}
