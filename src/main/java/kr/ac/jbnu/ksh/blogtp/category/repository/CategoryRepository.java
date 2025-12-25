package kr.ac.jbnu.ksh.blogtp.category.repository;

import kr.ac.jbnu.ksh.blogtp.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
