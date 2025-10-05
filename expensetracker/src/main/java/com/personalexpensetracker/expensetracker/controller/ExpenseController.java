package com.personalexpensetracker.expensetracker.controller;

import com.personalexpensetracker.expensetracker.model.Expense;
import com.personalexpensetracker.expensetracker.service.ExpenseService;
import com.personalexpensetracker.expensetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    
    @GetMapping
    public String listExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Expense> expenses;
        
        if (categoryId != null && startDate != null && endDate != null) {
            expenses = expenseService.findByCategoryAndDateRange(
                    categoryId, LocalDate.parse(startDate), LocalDate.parse(endDate), pageable);
        } else if (categoryId != null) {
            expenses = expenseService.findByCategoryId(categoryId, pageable);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.findByDateRange(
                    LocalDate.parse(startDate), LocalDate.parse(endDate), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            List<Expense> searchResults = expenseService.searchByNote(search);
            // Convert to page manually for search results
            expenses = Page.empty();
            model.addAttribute("searchResults", searchResults);
        } else {
            expenses = expenseService.findAll(pageable);
        }
        
        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", expenses.getTotalPages());
        model.addAttribute("totalElements", expenses.getTotalElements());
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("search", search);
        
        return "expenses/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", categoryService.findAll());
        return "expenses/form";
    }
    
    @PostMapping("/save")
    public String saveExpense(@Valid @ModelAttribute("expense") Expense expense,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "expenses/form";
        }
        
        try {
            expenseService.saveExpense(expense);
            redirectAttributes.addFlashAttribute("successMessage", "Expense saved successfully!");
            return "redirect:/expenses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving expense: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            return "expenses/form";
        }
    }
    
    @GetMapping("/edit/{id}")
@Transactional(readOnly = true)
public String showEditForm(@PathVariable Long id, Model model) {
    try {
        System.out.println("Edit endpoint called with ID: " + id);
        Optional<Expense> expense = expenseService.findById(id);
        System.out.println("Expense found: " + expense.isPresent());
        if (expense.isPresent()) {
            System.out.println("Expense details: " + expense.get());
            try {
                if (expense.get().getCategory() != null) {
                    System.out.println("Category: " + expense.get().getCategory().getName());
                }
            } catch (Exception ex) {
                System.out.println("Warning: Category could not be loaded (possibly lazy init).");
            }

            model.addAttribute("expense", expense.get());
            model.addAttribute("categories", categoryService.findAll());
            System.out.println("Categories loaded: " + categoryService.findAll().size());
            return "expenses/form";
        } else {
            System.out.println("Expense not found, redirecting to expenses list");
            return "redirect:/expenses";
        }
    } catch (Exception e) {
        System.err.println("Error in edit endpoint: " + e.getMessage());
        e.printStackTrace();
        return "redirect:/expenses";
    }
}

    @GetMapping("/test/{id}")
    @ResponseBody
    @Transactional(readOnly = true)
    public String testEdit(@PathVariable Long id) {
        try {
            System.out.println("Test endpoint called with ID: " + id);
            Optional<Expense> expense = expenseService.findById(id);
            if (expense.isPresent()) {
                // Access the category to initialize it
                if (expense.get().getCategory() != null) {
                    System.out.println("Category: " + expense.get().getCategory().getName());
                }
                return "Expense found: " + expense.get().toString();
            } else {
                return "Expense not found";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateExpense(@PathVariable Long id,
                              @Valid @ModelAttribute("expense") Expense expense,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "expenses/form";
        }
        
        try {
            expenseService.updateExpense(id, expense);
            redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully!");
            return "redirect:/expenses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating expense: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            return "expenses/form";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            expenseService.deleteExpense(id);
            redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting expense: " + e.getMessage());
        }
        return "redirect:/expenses";
    }
    
    @GetMapping("/summary")
    public String showSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        BigDecimal totalAmount = expenseService.getTotalAmountByDateRange(start, end);
        List<Object[]> categorySummary = expenseService.getCategorySummaryByDateRange(start, end);
        List<Object[]> monthlySummary = expenseService.getMonthlySummary(LocalDate.now().getYear());
        
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("categorySummary", categorySummary);
        model.addAttribute("monthlySummary", monthlySummary);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        
        return "expenses/summary";
    }
    
    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<Expense>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(expenseService.findAll(pageable));
    }
    
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Expense> getExpense(@PathVariable Long id) {
        return expenseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        try {
            Expense savedExpense = expenseService.saveExpense(expense);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        try {
            Expense updatedExpense = expenseService.updateExpense(id, expense);
            return ResponseEntity.ok(updatedExpense);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
