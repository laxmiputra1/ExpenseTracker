package com.personalexpensetracker.expensetracker.repository;

import com.personalexpensetracker.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find category by name (case insensitive)
    Optional<Category> findByNameIgnoreCase(String name);
    
    // Check if category exists by name (case insensitive)
    boolean existsByNameIgnoreCase(String name);
    
    // Find categories ordered by name
    List<Category> findAllByOrderByNameAsc();
    
    // Find categories with expense count
    @Query("SELECT c, COUNT(e) as expenseCount FROM Category c LEFT JOIN c.expenses e GROUP BY c.id ORDER BY c.name")
    List<Object[]> findAllWithExpenseCount();
}
