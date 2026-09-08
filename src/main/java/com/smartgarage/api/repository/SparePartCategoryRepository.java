package com.smartgarage.api.repository;

import com.smartgarage.api.entity.SparePartCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SparePartCategoryRepository extends JpaRepository<SparePartCategory, Long> {
}
