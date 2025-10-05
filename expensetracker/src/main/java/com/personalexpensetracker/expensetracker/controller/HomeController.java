package com.personalexpensetracker.expensetracker.controller;

import com.personalexpensetracker.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final ExpenseService expenseService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Get current month's total
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now();
        BigDecimal currentMonthTotal = expenseService.getTotalAmountByDateRange(startOfMonth, endOfMonth);
        
        // Get total expenses
        BigDecimal totalExpenses = expenseService.getTotalExpenses();
        
        // Get category summary for current month
        var categorySummary = expenseService.getCategorySummaryByDateRange(startOfMonth, endOfMonth);
        
        // Get recent expenses (last 5)
        var recentExpenses = expenseService.findAll().stream()
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .limit(5)
                .toList();
        
        model.addAttribute("currentMonthTotal", currentMonthTotal);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("categorySummary", categorySummary);
        model.addAttribute("recentExpenses", recentExpenses);
        model.addAttribute("currentMonth", LocalDate.now().getMonth().name());
        
        return "index";
    }
}
