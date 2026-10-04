package com.ers.controller;

import ch.qos.logback.classic.Logger;
import com.ers.model.User;
import com.ers.service.IUserService;
import com.ers.service.UserServiceImpl;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class UserController {
    private final Scanner scanner;
    private final IUserService userService;
    private static final Logger logger=(Logger) LoggerFactory.getLogger(UserController.class);

    public UserController(){
        this.scanner = new Scanner(System.in);
        this.userService = new UserServiceImpl();
    }

    public void showMenu() {
        logger.info("Started UserController.showMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("USER MANAGEMENT\n1. List All Users\n2. Get User By ID\n3. Activate/Deactivate User\n4. Update Password\n5. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: listAllUsers(); break;
                case 2: getUserById(); break;
                case 3: updateUserStatus(); break;
                case 4: updatePassword(); break;
                case 5: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        logger.info("Ending UserController.showMenu()");
    }

    private void listAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }
            users.forEach(this::displayUser);
        } catch (Exception e) {
            logger.error("ERROR at UserController.listAllUsers()", e);
            System.out.println("Failed to list users: " + e.getMessage());
        }
    }

    private void getUserById() {
        try {
            System.out.print("Enter user ID: ");
            int userId = readInt();
            User user = userService.getUserById(userId);
            if (user == null) {
                System.out.println("User not found.");
                return;
            }
            displayUser(user);
        } catch (Exception e) {
            logger.error("ERROR at UserController.getUserById()", e);
            System.out.println("Failed to get user: " + e.getMessage());
        }
    }

    private void updateUserStatus() {
        try {
            System.out.print("Enter user ID: ");
            int userId = readInt();
            System.out.print("Activate account? (yes/no): ");
            boolean active = scanner.nextLine().trim().equalsIgnoreCase("yes");
            boolean updated = userService.updateUserStatus(userId, active);
            System.out.println(updated ? "User status updated successfully." : "User status update failed.");
        } catch (Exception e) {
            logger.error("ERROR at UserController.updateUserStatus()", e);
            System.out.println("Failed to update user status: " + e.getMessage());
        }
    }

    private void updatePassword() {
        try {
            System.out.print("Enter user ID: ");
            int userId = readInt();
            System.out.print("Enter new password: ");
            String password = scanner.nextLine();
            User user = new User();
            user.setUserId(userId);
            user.setPassword(password);
            boolean updated = userService.updateUser(user);
            System.out.println(updated ? "Password updated successfully." : "Password update failed.");
        } catch (Exception e) {
            logger.error("ERROR at UserController.updatePassword()", e);
            System.out.println("Failed to update password: " + e.getMessage());
        }
    }

    private void displayUser(User user) {
        System.out.println("\nUSER");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Username: " + user.getUserName());
        System.out.println("Role: " + user.getRole());
        System.out.println("Active: " + user.isActive());
        System.out.println("Created At: " + user.getCreatedAt());
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