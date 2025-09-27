# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

**Librows** is a Spring Boot library management system that enables students to browse, reserve, and borrow books while providing administrators with comprehensive book and user management capabilities. The system features role-based authentication, real-time notifications, and a complete reservation workflow.

## Development Commands

### Build & Run
```powershell
# Build the project
.\gradlew build

# Run the application (development mode with auto-reload)
.\gradlew bootRun

# Run tests
.\gradlew test

# Clean build artifacts
.\gradlew clean
```

### Database Setup
```powershell
# Import the database schema (MySQL/MariaDB required)
# Execute sql/librows.sql in your MySQL database
mysql -u root -p librows < sql/librows.sql
```

### Testing Individual Components
```powershell
# Run specific test class
.\gradlew test --tests "com.example.FinalProject.FinalProjectApplicationTests"

# Run tests with detailed output
.\gradlew test --info
```

### Packaging & Deployment
```powershell
# Create executable JAR
.\gradlew bootJar

# Create WAR file for deployment
.\gradlew bootWar
```

## Architecture Overview

### Core Technology Stack
- **Spring Boot 4.0.0-M2** (Java 21)
- **Spring Security** for authentication and authorization
- **Spring Data JPA** with Hibernate for ORM
- **Thymeleaf** for server-side templating
- **MySQL** database
- **Gradle** for dependency management and build automation

### Package Structure & Responsibilities

#### Controllers (`com.example.FinalProject.controller`)
- **BookController**: Book management, search/filter, reservation workflow, admin operations
- **StudentController**: User registration, profile management, authentication handling  
- **LoginController**: Custom authentication logic
- **MainController**: General navigation and utility endpoints

#### Models (`com.example.FinalProject.model`)
Core entities with JPA relationships:
- **Book**: Central entity with status (available/not_available/borrowed), linked to borrowers and reservers
- **Student**: User entity with many-to-many relationships to borrowed and reserved books
- **Reservation**: Request entity tracking reservation workflow (pending/accepted/rejected)
- **admin**: Administrator entity with status tracking
- **Course**: Academic course reference for students
- **Notification**: System notifications for students

#### Security Architecture (`com.example.FinalProject.config`)
- **SecurityConfig**: Multi-provider authentication supporting both student and admin logins
- **StudentDetailsService/AdminDetailsService**: Custom UserDetailsService implementations
- Role-based authorization with `@PreAuthorize` annotations
- BCrypt password encoding

#### Data Access (`com.example.FinalProject.repository`)
JPA repositories extending `JpaRepository` with custom query methods:
- Complex queries in BookRepository for search/filtering
- Status-based filtering in ReservationRepository
- Email-based lookups for authentication

### Key Design Patterns & Data Flow

#### Reservation Workflow
1. **Student Request**: Student reserves available book → status changes to `not_available`
2. **Admin Review**: Admin sees pending reservations, can accept/reject
3. **Acceptance**: Book moves from reserved to borrowed, status becomes `borrowed`
4. **Notification**: System creates notifications for status changes

#### Many-to-Many Relationship Management
- Books track both `borrowers` and `reservers` lists
- Students maintain `borrowedBooks` and `reservedBooks` collections
- Join tables: `student_borrowed_books`, `student_reserved_books`, `book_reservers`

#### Authentication Strategy
- Dual authentication providers (student/admin)
- Custom UserDetails implementations wrapping domain entities
- Session-based authentication with role-specific redirects

### Development Guidelines

#### Working with Entities
- Always update both sides of bidirectional relationships
- Use `@Transactional` for complex entity state changes
- Call `repository.flush()` when immediate persistence is required

#### Security Context
- Extract authenticated user via `Authentication.getPrincipal()`
- Cast to `StudentDetails` or `AdminDetails` for domain access
- Use `@PreAuthorize("hasRole('ADMIN')")` for admin-only endpoints

#### Database Considerations
- Book status enum: `available` → `not_available` → `borrowed`
- Reservation status enum: `pending` → `accepted`/`rejected`
- MySQL database with provided schema in `sql/librows.sql`

### Static Resources & Templates
- **Thymeleaf templates**: `src/main/resources/templates/`
- **CSS/JS assets**: `src/main/resources/static/`
- **Profile dropdown navigation**: Implemented across admin templates
- **Role-based view rendering**: Using Thymeleaf security dialect

### Testing Strategy
- Main application test: `FinalProjectApplicationTests`
- Focus on integration testing given the entity relationship complexity
- Test reservation workflow and authentication edge cases

## Important Notes

- The project uses Java 21 with Gradle Kotlin DSL
- Database schema must be imported from `sql/librows.sql`
- Application runs on default Spring Boot port (8080)
- Admin and student interfaces are completely separated
- Real-time features implemented via session management and notifications table
