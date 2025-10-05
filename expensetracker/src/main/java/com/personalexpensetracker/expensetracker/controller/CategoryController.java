package com.personalexpensetracker.expensetracker.controller;

import com.personalexpensetracker.expensetracker.model.Category;
import com.personalexpensetracker.expensetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public String listCategories(Model model) {
        List<Object[]> categoriesWithCount = categoryService.findAllWithExpenseCount();
        model.addAttribute("categoriesWithCount", categoriesWithCount);
        return "categories/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }
    
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category saved successfully!");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving category: " + e.getMessage());
            return "categories/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categories/form";
        } else {
            return "redirect:/categories";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                               @Valid @ModelAttribute("category") Category category,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
            return "redirect:/categories";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating category: " + e.getMessage());
            return "categories/form";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/categories";
    }
    
    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        try {
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
