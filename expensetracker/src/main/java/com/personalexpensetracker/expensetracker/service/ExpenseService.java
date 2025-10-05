package com.personalexpensetracker.expensetracker.service;

import com.personalexpensetracker.expensetracker.model.Expense;
import com.personalexpensetracker.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }
    
    public Optional<Expense> findByIdWithCategory(Long id) {
        return expenseRepository.findByIdWithCategory(id);
    }
    
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }
    
    public Page<Expense> findAll(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }
    
    public List<Expense> findByCategoryId(Long categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }
    
    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate);
    }
    
    public List<Expense> findByCategoryAndDateRange(Long categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByCategoryIdAndDateBetween(categoryId, startDate, endDate);
    }
    
    public List<Expense> searchByNote(String note) {
        return expenseRepository.findByNoteContainingIgnoreCase(note);
    }
    
    public Page<Expense> findByCategoryId(Long categoryId, Pageable pageable) {
        return expenseRepository.findByCategoryId(categoryId, pageable);
    }
    
    public Page<Expense> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findByDateBetween(startDate, endDate, pageable);
    }
    
    public Page<Expense> findByCategoryAndDateRange(Long categoryId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findByCategoryIdAndDateBetween(categoryId, startDate, endDate, pageable);
    }
    
    public Expense updateExpense(Long id, Expense updatedExpense) {
        return expenseRepository.findById(id)
                .map(existingExpense -> {
                    existingExpense.setAmount(updatedExpense.getAmount());
                    existingExpense.setDate(updatedExpense.getDate());
                    existingExpense.setNote(updatedExpense.getNote());
                    existingExpense.setCategory(updatedExpense.getCategory());
                    return expenseRepository.save(existingExpense);
                })
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }
    
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
    
    public BigDecimal getTotalAmountByCategory(Long categoryId) {
        return expenseRepository.getTotalAmountByCategory(categoryId);
    }
    
    public BigDecimal getTotalAmountByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getTotalAmountByDateRange(startDate, endDate);
    }
    
    public BigDecimal getTotalAmountByCategoryAndDateRange(Long categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getTotalAmountByCategoryAndDateRange(categoryId, startDate, endDate);
    }
    
    public List<Object[]> getMonthlySummary(int year) {
        return expenseRepository.getMonthlySummary(year);
    }
    
    public List<Object[]> getCategorySummary() {
        return expenseRepository.getCategorySummary();
    }
    
    public List<Object[]> getCategorySummaryByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getCategorySummaryByDateRange(startDate, endDate);
    }
    
    public BigDecimal getTotalExpenses() {
        return expenseRepository.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
