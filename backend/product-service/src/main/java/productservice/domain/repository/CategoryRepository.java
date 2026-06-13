package productservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import productservice.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
