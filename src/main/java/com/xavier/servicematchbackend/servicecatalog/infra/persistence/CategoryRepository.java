package com.xavier.servicematchbackend.servicecatalog.infra.persistence;

import com.xavier.servicematchbackend.servicecatalog.domain.entity.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
