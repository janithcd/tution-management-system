# Tuition Management System

A Spring Boot-based Tuition Class Student Management System for managing students, class batches, enrollments, attendance, payments, reports, dashboard analytics, and PDF payment receipts.

This project is designed for tuition classes that manage O/L and A/L students, monthly fees, class attendance, and payment tracking.

---

## Features

### Dashboard

- Total students count
- Active students count
- Total batches count
- Active batches count
- Active enrollments count
- Today attendance count
- This month income
- Pending payment count
- Payment status chart
- Attendance status chart
- Monthly income by batch chart

### Student Management

- Add new students
- Edit student details
- Delete students
- Search students by:
    - Student code
    - Student name
    - Phone number
    - Parent phone number
    - School
- Filter students by:
    - Education level
    - Grade
- Pagination support

Supported education structure:

```text
O/L
 ├── Grade 10
 └── Grade 11

A/L
 ├── Grade 12
 └── Grade 13
```

### Batch / Class Management

- Add class batches
- Edit batch details
- Delete batches
- Search batches by:
    - Batch name
    - Subject
    - Teacher name
- Filter batches by:
    - Education level
    - Grade
    - Stream
- Pagination support

Example batches:

```text
O/L Grade 10 Mathematics - Sunday 8.00 AM
O/L Grade 11 Science - Saturday 2.00 PM
A/L Grade 12 Commerce Accounting - Monday 4.00 PM
A/L Grade 13 Technology ICT - Thursday 5.00 PM
```

### Student Enrollment

- Enroll students into batches
- Prevent duplicate enrollments
- Activate / deactivate enrollments
- Search enrollments by:
    - Student code
    - Student name
    - Batch name
    - Subject
- Filter enrollments by:
    - Education level
    - Grade
    - Stream
    - Enrollment status
- Pagination support

### Attendance Management

- Select batch and date
- Mark attendance for enrolled students
- Attendance status:
    - Present
    - Absent
    - Late
- Update attendance if already marked
- View attendance report by batch and date
- Search attendance records by:
    - Student code
    - Student name
    - Batch name
    - Subject
- Filter attendance records by:
    - Batch
    - Date range
    - Attendance status
- Pagination support

### Payment Management

- Add monthly payments
- Automatically calculate:
    - Expected amount
    - Paid amount
    - Balance amount
    - Payment status
- Payment statuses:
    - Paid
    - Partial
    - Unpaid
- Payment methods:
    - Cash
    - Bank transfer
    - Card
    - Online
- Search payments by:
    - Student code
    - Student name
    - Batch name
- Filter payments by:
    - Month
    - Year
    - Payment status
- Pagination support

### Payment Due Report

- Shows payment status for all active enrollments
- Detects unpaid students even when no payment record exists
- Shows:
    - Expected amount
    - Paid amount
    - Balance
    - Status

### Reports

Available reports:

- Monthly income report
- Student payment history
- Student attendance history
- Attendance records report

### PDF Receipt Generation

- Generate PDF payment receipts
- Receipt includes:
    - Student code
    - Student name
    - Batch
    - Subject
    - Payment month
    - Payment year
    - Expected amount
    - Paid amount
    - Balance
    - Payment status
    - Payment method
    - Payment date
    - Remarks

---

## Tech Stack

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Thymeleaf
- MySQL
- Bootstrap
- Chart.js
- OpenPDF
- Lombok
- Maven

---

## Project Structure

```text
src/main/java/lk/janith/tuitionmanagement
│
├── config
│   └── SecurityConfig.java
│
├── controller
│   ├── AttendanceController.java
│   ├── BatchController.java
│   ├── DashboardController.java
│   ├── EnrollmentController.java
│   ├── PaymentController.java
│   ├── ReportController.java
│   └── StudentController.java
│
├── dto
│   └── PaymentDueDto.java
│
├── entity
│   ├── Attendance.java
│   ├── Batch.java
│   ├── Enrollment.java
│   ├── Payment.java
│   └── Student.java
│
├── enums
│   ├── AttendanceStatus.java
│   ├── BatchStatus.java
│   ├── EducationLevel.java
│   ├── EnrollmentStatus.java
│   ├── Grade.java
│   ├── PaymentMethod.java
│   ├── PaymentStatus.java
│   ├── StreamType.java
│   └── StudentStatus.java
│
├── repository
│   ├── AttendanceRepository.java
│   ├── BatchRepository.java
│   ├── EnrollmentRepository.java
│   ├── PaymentRepository.java
│   └── StudentRepository.java
│
├── service
│   ├── AttendanceService.java
│   ├── BatchService.java
│   ├── DashboardService.java
│   ├── EnrollmentService.java
│   ├── PaymentService.java
│   ├── PdfReceiptService.java
│   ├── ReportService.java
│   └── StudentService.java
│
└── TuitionManagementApplication.java
```

---

## Database Tables

Main tables:

```text
students
batches
enrollments
attendance_records
payments
```

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/tuition-management-system.git
cd tuition-management-system
```

### 2. Create MySQL Database

Open MySQL and run:

```sql
CREATE DATABASE tuition_management_db;
```

### 3. Configure Database Connection

Open:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
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
```

### 4. Add Environment Variable

Set your MySQL password as an environment variable.

In IntelliJ:

```text
Run
→ Edit Configurations
→ Environment variables
→ DB_PASSWORD=your_mysql_password
```

Example:

```text
DB_PASSWORD=1234
```

Do not commit your real database password to GitHub.

### 5. Run the Project

Using IntelliJ:

```text
Run TuitionManagementApplication
```

Or using Maven:

```bash
mvn spring-boot:run
```

Open in browser:

```text
http://localhost:8080/dashboard
```

---

## Main URLs

```text
Dashboard:
http://localhost:8080/dashboard

Students:
http://localhost:8080/students

Batches:
http://localhost:8080/batches

Enrollments:
http://localhost:8080/enrollments

Attendance:
http://localhost:8080/attendance

Attendance Records:
http://localhost:8080/attendance/records

Payments:
http://localhost:8080/payments

Payment Dues:
http://localhost:8080/payments/dues

Reports:
http://localhost:8080/reports
```

---

## Demo Data

The project can be tested with sample data such as:

- 500 Sri Lankan-style student records
- Bulk enrollments
- 1000 payment records
- Attendance records for previous days

Demo data can be inserted using SQL scripts through MySQL Workbench.

Recommended insert order:

```text
1. Batches
2. Students
3. Enrollments
4. Payments
5. Attendance Records
```

---

## Important Notes

### Do not hard-delete enrollments with payments

If an enrollment already has payment or attendance records, it should not be deleted directly.

Use:

```text
Deactivate Enrollment
```

instead of deleting it.

This protects payment and attendance history.

### Safe update mode in MySQL Workbench

If MySQL Workbench blocks delete queries, run:

```sql
SET SQL_SAFE_UPDATES = 0;
```

After cleanup, you can enable it again:

```sql
SET SQL_SAFE_UPDATES = 1;
```

---

## Common Errors and Fixes

### Docker Compose Error

If Spring Boot tries to start Docker and gives an error, add this to `application.properties`:

```properties
spring.docker.compose.enabled=false
```

### MySQL Connection Refused

Make sure MySQL is running.

Check MySQL service:

```text
Services → MySQL80 → Start
```

Or use CMD:

```cmd
net start MySQL80
```

### Wrong Pageable Import

Do not use:

```java
import java.awt.print.Pageable;
```

Use:

```java
import org.springframework.data.domain.Pageable;
```

### Thymeleaf Template Not Found

Make sure HTML files are inside:

```text
src/main/resources/templates
```

Example:

```text
src/main/resources/templates/reports/index.html
```

---

## Current Project Status

Completed modules:

- Student management
- Batch management
- Enrollment management
- Attendance management
- Payment management
- Payment dues report
- Dashboard
- Dashboard charts
- Reports module
- Search and filters
- Pagination
- PDF payment receipt generation
- Demo data support

---

## Future Improvements

Planned improvements:

- Admin login with Spring Security
- Role-based access control
- Teacher login
- Parent/student portal
- CSV export
- Excel export
- Advanced PDF reports
- SMS or WhatsApp payment reminders
- QR code attendance
- Online payment integration
- Cloud deployment
- Database backup system

---

## Author

Developed by **Janith Dasanayaka**

GitHub: `@janithcd`

---

## License

This project is for educational and portfolio purposes.
