package vn.vuxnye.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vuxnye.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
