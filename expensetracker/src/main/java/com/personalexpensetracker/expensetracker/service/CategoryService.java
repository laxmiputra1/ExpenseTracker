package com.personalexpensetracker.expensetracker.service;

import com.personalexpensetracker.expensetracker.model.Category;
import com.personalexpensetracker.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public Category saveCategory(Category category) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        }
        return categoryRepository.save(category);
    }
    
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    public List<Object[]> findAllWithExpenseCount() {
        return categoryRepository.findAllWithExpenseCount();
    }
    
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }
    
    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Check if the new name conflicts with existing categories (excluding current one)
                    if (!existingCategory.getName().equalsIgnoreCase(updatedCategory.getName()) &&
                        categoryRepository.existsByNameIgnoreCase(updatedCategory.getName())) {
                        throw new RuntimeException("Category with name '" + updatedCategory.getName() + "' already exists");
                    }
                    existingCategory.setName(updatedCategory.getName());
                    existingCategory.setDescription(updatedCategory.getDescription());
                    return categoryRepository.save(existingCategory);
                })
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
    
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
}
