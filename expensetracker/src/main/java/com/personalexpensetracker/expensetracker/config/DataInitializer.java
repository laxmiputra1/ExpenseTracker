package com.personalexpensetracker.expensetracker.config;

import com.personalexpensetracker.expensetracker.model.Category;
import com.personalexpensetracker.expensetracker.model.Expense;
import com.personalexpensetracker.expensetracker.service.CategoryService;
import com.personalexpensetracker.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CategoryService categoryService;
    private final ExpenseService expenseService;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("DataInitializer: Starting data initialization...");
            // Only initialize if no categories exist
            if (categoryService.findAll().isEmpty()) {
                System.out.println("DataInitializer: No categories found, initializing data...");
                initializeCategories();
                initializeExpenses();
                System.out.println("DataInitializer: Data initialization completed successfully!");
            } else {
                System.out.println("DataInitializer: Categories already exist, skipping initialization.");
            }
        } catch (Exception e) {
            System.err.println("DataInitializer: Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeCategories() {
        List<Category> categories = List.of(
            new Category("Food & Dining", "Restaurants, groceries, and food-related expenses"),
            new Category("Transportation", "Gas, public transport, rideshare, and vehicle maintenance"),
            new Category("Bills & Utilities", "Electricity, water, internet, phone, and other utilities"),
            new Category("Shopping", "Clothing, electronics, and general shopping"),
            new Category("Entertainment", "Movies, games, subscriptions, and leisure activities"),
            new Category("Healthcare", "Medical expenses, pharmacy, and health-related costs"),
            new Category("Travel", "Hotels, flights, and vacation expenses"),
            new Category("Education", "Books, courses, and educational materials"),
            new Category("Miscellaneous", "Other expenses that don't fit into specific categories")
        );
        
        categories.forEach(categoryService::saveCategory);
    }
    
    private void initializeExpenses() {
        List<Category> categories = categoryService.findAll();
        
        if (categories.isEmpty()) return;
        
        // Get some categories for sample data
        Category foodCategory = categories.stream()
                .filter(c -> "Food & Dining".equals(c.getName()))
                .findFirst().orElse(categories.get(0));
        
        Category transportCategory = categories.stream()
                .filter(c -> "Transportation".equals(c.getName()))
                .findFirst().orElse(categories.get(0));
        
        Category billsCategory = categories.stream()
                .filter(c -> "Bills & Utilities".equals(c.getName()))
                .findFirst().orElse(categories.get(0));
        
        Category shoppingCategory = categories.stream()
                .filter(c -> "Shopping".equals(c.getName()))
                .findFirst().orElse(categories.get(0));
        
        // Create sample expenses
        List<Expense> sampleExpenses = List.of(
            createExpense(new BigDecimal("25.50"), LocalDate.now().minusDays(1), "Lunch at restaurant", foodCategory),
            createExpense(new BigDecimal("45.20"), LocalDate.now().minusDays(2), "Grocery shopping", foodCategory),
            createExpense(new BigDecimal("15.00"), LocalDate.now().minusDays(3), "Uber ride", transportCategory),
            createExpense(new BigDecimal("120.00"), LocalDate.now().minusDays(5), "Electricity bill", billsCategory),
            createExpense(new BigDecimal("89.99"), LocalDate.now().minusDays(7), "New shirt", shoppingCategory),
            createExpense(new BigDecimal("12.50"), LocalDate.now().minusDays(8), "Coffee and pastry", foodCategory),
            createExpense(new BigDecimal("35.00"), LocalDate.now().minusDays(10), "Gas for car", transportCategory),
            createExpense(new BigDecimal("200.00"), LocalDate.now().minusDays(12), "Internet bill", billsCategory),
            createExpense(new BigDecimal("15.99"), LocalDate.now().minusDays(15), "Movie ticket", categories.stream()
                    .filter(c -> "Entertainment".equals(c.getName()))
                    .findFirst().orElse(categories.get(0))),
            createExpense(new BigDecimal("67.80"), LocalDate.now().minusDays(18), "Dinner with friends", foodCategory)
        );
        
        sampleExpenses.forEach(expenseService::saveExpense);
    }
    
    private Expense createExpense(BigDecimal amount, LocalDate date, String note, Category category) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setNote(note);
        expense.setCategory(category);
        return expense;
    }
}
