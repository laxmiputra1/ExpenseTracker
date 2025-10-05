package com.personalexpensetracker.expensetracker.repository;

import com.personalexpensetracker.expensetracker.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Find expenses by category
    List<Expense> findByCategoryId(Long categoryId);
    
    // Find expenses by date range
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find expenses by category and date range
    List<Expense> findByCategoryIdAndDateBetween(Long categoryId, LocalDate startDate, LocalDate endDate);
    
    // Find expenses by note containing text (case insensitive)
    List<Expense> findByNoteContainingIgnoreCase(String note);
    
    // Paginated queries
    Page<Expense> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Expense> findByCategoryIdAndDateBetween(Long categoryId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Summary queries
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId")
    BigDecimal getTotalAmountByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByCategoryAndDateRange(@Param("categoryId") Long categoryId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    // Monthly summary
    @Query("SELECT MONTH(e.date) as month, SUM(e.amount) as total FROM Expense e WHERE YEAR(e.date) = :year GROUP BY MONTH(e.date) ORDER BY MONTH(e.date)")
    List<Object[]> getMonthlySummary(@Param("year") int year);
    
    // Category summary
    @Query("SELECT c.name, SUM(e.amount) FROM Expense e JOIN e.category c GROUP BY c.id, c.name ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategorySummary();
    
    // Category summary for date range
    @Query("SELECT c.name, SUM(e.amount) FROM Expense e JOIN e.category c WHERE e.date BETWEEN :startDate AND :endDate GROUP BY c.id, c.name ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategorySummaryByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find expense with category (to avoid lazy loading issues)
    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id")
    Optional<Expense> findByIdWithCategory(@Param("id") Long id);
}
