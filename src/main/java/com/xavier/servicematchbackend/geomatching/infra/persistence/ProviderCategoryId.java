package com.xavier.servicematchbackend.geomatching.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProviderCategoryId implements Serializable {

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    protected ProviderCategoryId() {
    }

    public ProviderCategoryId(UUID providerId, UUID categoryId) {
        this.providerId = providerId;
        this.categoryId = categoryId;
    }

    public UUID providerId() {
        return providerId;
    }

    public UUID categoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderCategoryId that = (ProviderCategoryId) o;
        return Objects.equals(providerId, that.providerId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, categoryId);
    }
}
