# EduTrack Tuition Management System

A professional Spring Boot-based Tuition Class Student Management System for managing students, class batches, enrollments, attendance, payments, monthly dues, reports, and PDF payment receipts.

This project is built as a full-stack Java web application using Spring Boot, Thymeleaf, MySQL, Bootstrap, and OpenPDF.

---

## Project Overview

EduTrack helps tuition class owners or administrators manage day-to-day academic and payment operations in one system.

The system supports:

- Student management
- Class / batch management
- Student enrollments
- Attendance marking and attendance records
- Monthly payment recording
- Payment dues tracking
- PDF payment receipt generation
- Search, filters, pagination, validation, and alerts
- Dashboard charts and reports

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Main programming language |
| Spring Boot 4.1.0 | Backend framework |
| Spring MVC | Web layer |
| Spring Data JPA | Database operations |
| Hibernate | ORM |
| MySQL | Database |
| Thymeleaf | Server-side HTML rendering |
| Bootstrap 5 | UI design |
| Bootstrap Icons | Icons |
| Chart.js | Dashboard charts |
| OpenPDF | PDF receipt generation |
| Maven | Dependency management |
| Lombok | Boilerplate code reduction |

---

## Main Features

### 1. Dashboard

The dashboard provides a quick overview of the system.

Includes:

- Total students
- Active students
- Total batches
- Active enrollments
- Today attendance count
- Current month income
- Pending payments
- Charts for attendance and payment summaries
- Quick action cards

---

### 2. Student Management

The student module allows the admin to manage all student records.

Features:

- Add new student
- Edit student
- Delete student
- Search students by name, code, phone, parent phone, or school
- Filter by education level and grade
- Pagination
- Form validation
- Success and error alerts
- Auto-generated student code

Student information includes:

- Student code
- Full name
- Phone number
- Parent phone number
- Address
- School
- Education level
- Grade
- Stream
- Joined date
- Status

---

### 3. Batch Management

The batch module is used to manage tuition class batches.

Features:

- Add new batch
- Edit batch
- Delete batch
- Search by batch name, subject, or teacher
- Filter by education level, grade, and stream
- Pagination
- Validation
- Success and error alerts

Batch information includes:

- Batch name
- Education level
- Grade
- Stream
- Subject
- Teacher name
- Monthly fee
- Class day
- Start time
- End time
- Status

---

### 4. Enrollment Management

The enrollment module connects students with class batches.

Features:

- Enroll students into batches
- Prevent duplicate enrollments
- Reactivate inactive enrollments
- Activate / deactivate enrollments
- Search enrollments
- Filter enrollments
- Pagination
- Success and error alerts

Duplicate enrollment protection:

If the same student is already enrolled in the same batch, the system shows a clean error message instead of creating duplicate records.

---

### 5. Attendance Management

The attendance module helps track student attendance.

Features:

- Select batch and date
- Mark attendance for students
- Attendance statuses:
  - PRESENT
  - ABSENT
  - LATE
- Search attendance records
- Filter by batch, date range, and status
- Export attendance records to CSV
- Attendance report by student

---

### 6. Payment Management

The payment module handles monthly class fee payments.

Features:

- Record monthly payments
- Prevent duplicate monthly payments
- Track paid, partial, and unpaid statuses
- Search payment records
- Filter by month, year, and status
- Export payments to CSV
- Success and error alerts
- Redirect back to payment form when an error occurs
- Generate professional PDF receipts

Payment statuses:

- PAID
- PARTIAL
- UNPAID

Payment methods:

- CASH
- BANK_TRANSFER
- CARD
- ONLINE

---

### 7. Payment Dues

The payment dues module shows payment status for active enrollments for a selected month.

Features:

- Select month and year
- View all due records
- Show expected amount
- Show paid amount
- Show balance amount
- Show payment status
- Identify unpaid and partially paid students easily

---

### 8. PDF Payment Receipt

The system generates a professional PDF payment receipt using OpenPDF.

Receipt includes:

- Receipt number
- EduTrack receipt badge
- Payment date
- Student details
- Batch details
- Payment details
- Paid amount
- Balance amount
- Payment method
- Payment status seal
- Authorized signature section

Receipt status seal supports:

- PAID
- PARTIALLY PAID
- UNPAID

Example receipt URLs:

    /payments/receipt/{id}
    /payments/{id}/receipt

---

### 9. Reports

The report module provides useful summaries for administration.

Includes:

- Monthly income report
- Student payment report
- Student attendance report

---

## Project Structure

    tuition-management
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── lk
    │   │   │       └── janith
    │   │   │           └── tuitionmanagement
    │   │   │               ├── controller
    │   │   │               ├── entity
    │   │   │               ├── enums
    │   │   │               ├── repository
    │   │   │               ├── service
    │   │   │               └── TuitionManagementApplication.java
    │   │   └── resources
    │   │       ├── static
    │   │       │   └── css
    │   │       │       └── app.css
    │   │       ├── templates
    │   │       │   ├── fragments
    │   │       │   ├── dashboard.html
    │   │       │   ├── students
    │   │       │   ├── batches
    │   │       │   ├── enrollments
    │   │       │   ├── attendance
    │   │       │   ├── payments
    │   │       │   └── reports
    │   │       └── application.properties
    │   └── test
    ├── pom.xml
    └── README.md

---

## Database Name

The project uses MySQL.

Default database name:

    tuition_management_db

The database can be created automatically if this property is enabled:

    spring.datasource.url=jdbc:mysql://localhost:3306/tuition_management_db?createDatabaseIfNotExist=true

---

## Application Properties

Create or update this file:

    src/main/resources/application.properties

Recommended configuration:

    spring.application.name=tuition-management

    spring.datasource.url=jdbc:mysql://localhost:3306/tuition_management_db?createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=${DB_PASSWORD}

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true

    spring.thymeleaf.cache=false

    server.port=8080

    spring.docker.compose.enabled=false

---

## Environment Variable

This project uses an environment variable for the database password.

Set this environment variable:

    DB_PASSWORD=your_mysql_password

Example for Windows PowerShell:

    setx DB_PASSWORD "your_mysql_password"

After setting it, restart IntelliJ IDEA or your terminal.

---

## How to Run the Project

### 1. Clone the Repository

    git clone https://github.com/your-username/tuition-management-system.git

### 2. Open Project

Open the project using IntelliJ IDEA.

### 3. Configure MySQL

Make sure MySQL is running.

Create the database manually if needed:

    CREATE DATABASE tuition_management_db;

### 4. Reload Maven

In IntelliJ IDEA:

    Right click pom.xml
    Maven
    Reload Project

### 5. Build the Project

    mvn clean compile

### 6. Run the Application

Run the main class:

    TuitionManagementApplication.java

Or use Maven:

    mvn spring-boot:run

### 7. Open in Browser

    http://localhost:8080/dashboard

---

## Important Routes

| Module | URL |
|---|---|
| Dashboard | `/dashboard` |
| Students | `/students` |
| Add Student | `/students/new` |
| Batches | `/batches` |
| Add Batch | `/batches/new` |
| Enrollments | `/enrollments` |
| New Enrollment | `/enrollments/new` |
| Attendance | `/attendance` |
| Attendance Records | `/attendance/records` |
| Payments | `/payments` |
| Add Payment | `/payments/new` |
| Payment Dues | `/payments/dues` |
| Reports | `/reports` |

---

## Validation

The system includes backend validation using Spring Boot Validation.

Examples:

Student validation:

- Full name is required
- Phone number must contain exactly 10 digits
- Parent phone number is required
- School name is required
- Education level is required
- Grade is required
- Stream is required
- Joined date is required
- Student status is required

Batch validation:

- Batch name is required
- Subject is required
- Teacher name is required
- Monthly fee must be greater than 0
- Class day is required
- Start time is required
- End time is required
- Batch status is required

---

## Alerts and Error Handling

The system includes clean success and error alerts.

Examples:

    Student added successfully.
    Student updated successfully.
    Batch cannot be deleted because related enrollments, payments, or attendance records may exist.
    This student is already enrolled in the selected batch.
    This enrollment already has a payment record for July 2026.
    Payment recorded successfully.

---

## Duplicate Protection

The system prevents important duplicate records.

Protected actions:

- Same student cannot be enrolled into the same batch twice
- Same enrollment cannot have duplicate payments for the same month and year
- Delete actions are protected when related records exist

---

## Export Features

The system supports CSV export for:

- Attendance records
- Payment records

This helps admins analyze records in Excel or Google Sheets.

---

## PDF Receipt Feature

The project uses OpenPDF to generate receipts.

Dependency:

    com.github.librepdf:openpdf

Receipt URLs:

    /payments/receipt/{id}
    /payments/{id}/receipt

The PDF receipt is generated dynamically from payment data.

---

## Screenshots

Add your screenshots here after running the project.

### Dashboard

    screenshots/dashboard.png

### Student Management

    screenshots/students.png

### Batch Management

    screenshots/batches.png

### Payment Receipt

    screenshots/payment-receipt.png

---

## Maven Dependencies Used

Main dependencies include:

- spring-boot-starter-webmvc
- spring-boot-starter-thymeleaf
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-security
- mysql-connector-j
- lombok
- openpdf
- spring-boot-devtools

---

## Build Command

    mvn clean compile

---

## Run Command

    mvn spring-boot:run

---

## Git Commands

Check status:

    git status

Add files:

    git add .

Commit:

    git commit -m "Update tuition management system"

Push:

    git push

---

## Future Improvements

Possible future updates:

- Admin login and role-based access
- Student portal
- Parent portal
- SMS or WhatsApp payment reminders
- Email receipt sending
- Online payment gateway integration
- QR code attendance marking
- Advanced report dashboard
- Backup and restore feature
- Cloud deployment

---

## Author

Developed by Janith Dasanayaka.

GitHub: @janithcd

