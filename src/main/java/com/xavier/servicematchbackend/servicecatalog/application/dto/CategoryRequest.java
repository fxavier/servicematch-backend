package com.xavier.servicematchbackend.servicecatalog.application.dto;

public record CategoryRequest(String name, String parentId, String path) {
}
