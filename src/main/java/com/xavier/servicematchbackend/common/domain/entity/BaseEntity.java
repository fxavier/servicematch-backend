package com.xavier.servicematch.common.domain.entity;

import java.util.Objects;

/**
 * Base class for all domain entities.
 *
 * <p>
 * Entities are objects defined by their identity rather than their attributes.
 * This base class can be extended to include common entity functionality
 * such as equality based on identity, auditing fields, etc.
 * </p>
 *
 * @param <ID> type of the entity identifier
 */
public abstract class BaseEntity<ID> {

    protected ID id;

    protected BaseEntity() {}

    protected BaseEntity(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public  void setId(ID id) {
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity<?> that = (BaseEntity<?>) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}
