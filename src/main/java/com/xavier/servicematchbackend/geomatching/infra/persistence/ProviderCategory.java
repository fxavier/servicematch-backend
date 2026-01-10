package com.xavier.servicematchbackend.geomatching.infra.persistence;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "provider_categories")
public class ProviderCategory {

    @EmbeddedId
    private ProviderCategoryId id;

    protected ProviderCategory() {
    }

    public ProviderCategory(ProviderCategoryId id) {
        this.id = id;
    }

    public UUID providerId() {
        return id != null ? id.providerId() : null;
    }

    public UUID categoryId() {
        return id != null ? id.categoryId() : null;
    }
}
