package com.ers.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.model.ExpenseCategory;
import com.ers.service.ExpenseCategoryServiceImpl;
import com.ers.service.IExpenseCategoryService;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class ExpenseCategoryController {
    private final Scanner scanner;
    private final IExpenseCategoryService expenseCategoryService;
    private static final Logger logger=(Logger)LoggerFactory.getLogger(ExpenseCategoryController.class);

    public ExpenseCategoryController() {
        this.scanner = new Scanner(System.in);
        this.expenseCategoryService = new ExpenseCategoryServiceImpl();
    }

    public void showMenu() {
        logger.info("Started ExpenseCategoryController.showMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("EXPENSE CATEGORY MANAGEMENT\n1. Add Category\n2. Update Category\n3. Get Category By ID\n4. List All Categories\n5. Delete Category\n6. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: addCategory(); break;
                case 2: updateCategory(); break;
                case 3: getCategoryById(); break;
                case 4: listAllCategories(); break;
                case 5: deleteCategory(); break;
                case 6: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        logger.info("Ending ExpenseCategoryController.showMenu()");
    }

    private void addCategory() {
        try {
            System.out.print("Enter category name: ");
            String name = scanner.nextLine();
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            ExpenseCategory category = new ExpenseCategory(name, description);
            ExpenseCategory saved = expenseCategoryService.addExpenseCategory(category);
            System.out.println("Category added successfully. Category ID: " + saved.getCategoryId());
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryController.addCategory()", e);
            System.out.println("Failed to add category: " + e.getMessage());
        }
    }

    private void updateCategory() {
        try {
            System.out.print("Enter category ID: ");
            int categoryId = readInt();
            System.out.print("Enter new category name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new description: ");
            String description = scanner.nextLine();
            ExpenseCategory category = new ExpenseCategory(name, description);
            category.setCategoryId(categoryId);
            boolean updated = expenseCategoryService.updateExpenseCategory(category);
            System.out.println(updated ? "Category updated successfully." : "Category update failed.");
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryController.updateCategory()", e);
            System.out.println("Failed to update category: " + e.getMessage());
        }
    }

    private void getCategoryById() {
        try {
            System.out.print("Enter category ID: ");
            int categoryId = readInt();
            ExpenseCategory category = expenseCategoryService.getExpenseCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }
            displayCategory(category);
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryController.getCategoryById()", e);
            System.out.println("Failed to get category: " + e.getMessage());
        }
    }

    private void listAllCategories() {
        try {
            List<ExpenseCategory> categories = expenseCategoryService.getAllExpenseCategories();
            if (categories.isEmpty()) {
                System.out.println("No categories found.");
                return;
            }
            categories.forEach(this::displayCategory);
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryController.listAllCategories()", e);
            System.out.println("Failed to list categories: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        try {
            System.out.print("Enter category ID: ");
            int categoryId = readInt();
            boolean deleted = expenseCategoryService.deleteExpenseCategoryById(categoryId);
            System.out.println(deleted ? "Category deleted successfully." : "Category deletion failed.");
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryController.deleteCategory()", e);
            System.out.println("Failed to delete category: " + e.getMessage());
        }
    }

    private void displayCategory(ExpenseCategory category) {
        System.out.println("\nEXPENSE CATEGORY");
        System.out.println("Category ID: " + category.getCategoryId());
        System.out.println("Name: " + category.getCategoryName());
        System.out.println("Description: " + category.getDescription());
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer input");
            return -1;
        }
    }
}