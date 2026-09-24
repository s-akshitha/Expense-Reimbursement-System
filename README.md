# Expense Reimbursement System

## Project Overview

This is the initial skeleton of the **Expense Reimbursement System** project.  
The project is structured using a layered architecture to separate responsibilities and make the application easier to develop, maintain, and extend.

## Technologies & Dependencies
- **Java** – JDK 19
- **Maven** – Project build and dependency management
- **JDBC** – Database connectivity
- **MySQL** – Database
- **MySQL Connector/J** – 9.4.0
- **JUnit Jupiter** – 5.13.4

## Project Structure

The project contains the following packages:

- **model** – Contains Java classes representing the database tables/entities:
    - User
    - Employee
    - Department
    - FinanceExecutive
    - ExpenseCategory
    - ExpenseClaim
    - ClaimItem
    - Reimbursement

- **controller** – Contains controllers responsible for handling application requests and communicating with the service layer.

- **service** – Contains business logic and application rules.

- **dao** – Contains Data Access Objects responsible for performing database operations such as CRUD operations.

- **util** – Contains utility classes, mainly for establishing and managing the JDBC database connection.

- **exception** – Contains custom exception classes used for handling application-specific errors.

## Data Flow

The application follows the flow:

**MainController → Specific Controller → Service → DAO → Database**

### Flow Explanation

1. **MainController**
    - Acts as the main entry point of the application.
    - Routes the operation to the appropriate specific controller.

2. **Specific Controller**
    - Handles operations related to a particular module.
    - Example: `UserController`, `EmployeeController`, `ExpenseClaimController`, etc.
    - Calls the corresponding service methods.

3. **Service**
    - Contains the **business logic** of the application.
    - Validates data and applies business rules.
    - Calls the DAO layer to perform database operations.

4. **DAO (Data Access Object)**
    - Responsible for **database operations**.
    - Performs operations such as INSERT, SELECT, UPDATE, and DELETE using JDBC.

5. **Database**
    - Stores and retrieves the application data.

## Architecture

```text
                    MainController
                          |
                          v
                 Specific Controller
                          |
                          v
                       Service
                  (Business Logic)
                          |
                          v
                         DAO
                  (Database Operations)
                          |
                          v
                      Database
