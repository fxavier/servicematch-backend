package com.xavier.servicematchbackend.common.domain.entity;

/**
 * Base class for aggregate roots in the domain.
 *
 * <p>
 * An aggregate root is an entity that serves as the entry point
 * to an aggregate, ensuring the integrity and consistency of the
 * entire aggregate.
 * </p>
 *
 * @param <ID> type of the aggregate root identifier
 */
public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(ID id) {
        super(id);
    }
}
