package com.xavier.servicematchbackend.servicecatalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "catalog_categories")
public class Category {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name = "path", length = 500)
    private String path;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Category() {
    }

    private Category(UUID id, String name, Category parent, String path, Instant now) {
        this.id = requireNonNull(id, "id must not be null");
        this.name = requireName(name);
        this.parent = parent;
        this.path = normalizePath(path);
        this.createdAt = requireNonNull(now, "createdAt must not be null");
        this.updatedAt = now;
    }

    public static Category create(String name, Category parent, String path, Instant now) {
        return new Category(UUID.randomUUID(), name, parent, path, now);
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Category parent() {
        return parent;
    }

    public String path() {
        return path;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void update(String name, Category parent, String path, Instant now) {
        this.name = requireName(name);
        this.parent = parent;
        this.path = normalizePath(path);
        this.updatedAt = requireNonNull(now, "updatedAt must not be null");
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name.trim();
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return path.trim();
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
