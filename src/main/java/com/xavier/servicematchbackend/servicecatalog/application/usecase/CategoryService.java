package com.xavier.servicematchbackend.servicecatalog.application.usecase;

import com.xavier.servicematchbackend.servicecatalog.application.dto.CategoryRequest;
import com.xavier.servicematchbackend.servicecatalog.application.dto.CategoryResponse;
import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;
import com.xavier.servicematchbackend.servicecatalog.infra.persistence.CategoryRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        List<Category> categories = categoryRepository.findAll(
                Sort.by(Sort.Order.asc("path"), Sort.Order.asc("name"))
        );
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID id) {
        return CategoryResponse.from(findCategory(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        CategoryRequest input = requireRequest(request);
        Instant now = Instant.now();
        Category parent = resolveParent(input.parentId(), null);
        Category category = Category.create(input.name(), parent, input.path(), now);
        categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        CategoryRequest input = requireRequest(request);
        Category category = findCategory(id);
        Category parent = resolveParent(input.parentId(), category.id());
        category.update(input.name(), parent, input.path(), Instant.now());
        categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    private CategoryRequest requireRequest(CategoryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        return request;
    }

    private Category findCategory(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("categoryId must not be null");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("category not found"));
    }

    private Category resolveParent(String parentId, UUID selfId) {
        if (parentId == null || parentId.isBlank()) {
            return null;
        }
        UUID parentUuid = parseUuid(parentId, "parentId");
        if (selfId != null && selfId.equals(parentUuid)) {
            throw new IllegalArgumentException("parentId must not match category id");
        }
        return categoryRepository.findById(parentUuid)
                .orElseThrow(() -> new IllegalArgumentException("parent category not found"));
    }

    private UUID parseUuid(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(field + " must be a valid UUID");
        }
    }
}
