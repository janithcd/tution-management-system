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

---

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