# BookFlex — Generic Bookable Resource System (Microservices)

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.2-green?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.3-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Architecture](https://img.shields.io/badge/Architecture-Microservices-purple)

A production-grade, enterprise microservices system built with Java 21, Spring Boot 3.3.x, and PostgreSQL 16.
Designed to handle any type of "bookable resource" (hotel rooms, medical appointments, sports fields) dynamically without modifying core domain logic (**Open/Closed Principle**).

---

## 📋 Table of Contents
1. [Requirements & SDLC](#1-requirements--sdlc)
2. [Architecture Overview](#2-architecture-overview)
3. [Domain Model & Abstraction](#3-domain-model--abstraction)
4. [SOLID Principles Mapping](#4-solid-principles-mapping)
5. [Design Patterns Catalog](#5-design-patterns-catalog)
6. [Collections Framework & Exception Handling](#6-collections-framework--exception-handling)
7. [Concurrency & Double-Booking Prevention](#7-concurrency--double-booking-prevention)
8. [Cross-Service Transactions (Saga Pattern)](#8-cross-service-transactions-saga-pattern)
9. [Deployment & Quick Start](#9-deployment--quick-start)
10. [Testing & OCP Proof](#10-testing--ocp-proof)

---

## 1. Requirements & SDLC

### Functional Requirements
* **User Management**: Authentication & Authorization (JWT + Role-Based Access Control: `CUSTOMER`, `PROVIDER`, `ADMIN`).
* **Resource Management**: Dynamic creation and availability tracking for diverse resource types (`ROOM`, `APPOINTMENT`, `SPORTS_FIELD`).
* **Booking Lifecycle**: Reservation, payment confirmation, cancellation with time-based refund policy calculation, and completion.
* **Notifications**: Event-driven email/SMS/log notifications on booking state changes via RabbitMQ.
* **Verified Reviews & Ratings**: Verified post-booking ratings (1-5 stars) and average rating calculation per resource (`review-service`).

### Non-Functional Requirements
* **Extensibility**: Add new resource types without modifying existing booking services (Open/Closed Principle).
* **Double-Booking Guarantee**: Concurrent requests for the same resource time slot must be strictly serialized using database-level pessimistic locking.
* **Fault Tolerance & Consistency**: Event-driven Saga pattern with compensating transactions when payment fails.

---

## 2. Architecture Overview

### System Architecture Diagram
```
                          ┌───────────────────────────┐
                          │   Client / Postman / UI   │
                          └─────────────┬─────────────┘
                                        │
                                        ▼
                          ┌───────────────────────────┐
                          │   API Gateway (:8088)     │
                          │ (JWT Filter + Routing)    │
                          └─────────────┬─────────────┘
                                        │
             ┌──────────────────────────┼──────────────────────────┬──────────────────────────┐
             ▼                          ▼                          ▼                          ▼
    ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
    │  User Service   │        │ Resource Service│        │ Booking Service │        │ Review Service  │
    │     (:8081)     │        │     (:8082)     │        │     (:8083)     │        │     (:8086)     │
    └────────┬────────┘        └────────┬────────┘        └────────┬────────┘        └────────┬────────┘
             │                          │                          │                          │
             └──────────────────────────┼──────────────────────────┼──────────────────────────┘
                                        ▼                          ▼
                            ┌───────────────────────┐   ┌───────────────────────┐
                            │ Eureka Service        │   │ pgAdmin 4 Dashboard   │
                            │ Discovery (:8761)     │   │      (:5050)          │
                            └───────────────────────┘   └───────────────────────┘
                                        │
             ┌──────────────────────────┴──────────────────────────┐
             ▼                                                     ▼
    ┌─────────────────┐                                   ┌─────────────────┐
    │ Payment Service │ ◄══════════ RabbitMQ ═══════════► │ Notification Svc│
    │     (:8084)     │            Event Bus              │     (:8085)     │
    └─────────────────┘                                   └─────────────────┘
```

### Microservice Registry

| Service | Port | Database (PostgreSQL 16) | Description |
|---|---|---|---|
| `discovery-service` | 8761 | None | Netflix Eureka Service Registry |
| `api-gateway` | 8088 | None | Spring Cloud Gateway, JWT authentication filter, dynamic routing |
| `user-service` | 8081 | `bookflex_users` | Authentication (JWT), user management, RBAC (`CUSTOMER`, `PROVIDER`, `ADMIN`) |
| `resource-service` | 8082 | `bookflex_resources` | Core abstraction `BookableResource`, Factory pattern, resource CRUD |
| `booking-service` | 8083 | `bookflex_bookings` | Core booking engine, State & Strategy patterns, Pessimistic Lock, Saga orchestrator |
| `payment-service` | 8084 | `bookflex_payments` | Mock payment gateway, Dependency Inversion (`PaymentGateway` interface) |
| `notification-service` | 8085 | `bookflex_notifications` | Event listener for RabbitMQ, Interface Segregation (`NotificationSender`) |
| `review-service` | 8086 | `bookflex_reviews` | Verified ratings (1-5 stars), single review per booking rule, rating aggregations |
| `pgadmin` | 5050 | PostgreSQL Dashboard | Web administration interface for all PostgreSQL 16 databases |

---

## 3. Domain Model & Abstraction

The core abstraction is the `BookableResource` interface located in `resource-service`:

```java
public interface BookableResource {
    String getResourceId();
    ResourceType getType();
    Duration getMinBookingDuration();
    boolean isAvailableAt(TimeSlot slot);
    String getDisplayName();
}
```

### Implementations:
1. `Room implements BookableResource` — Minimum duration: 24 hours (1 night).
2. `AppointmentSlot implements BookableResource` — Minimum duration: 30 minutes.
3. `SportsFieldSlot implements BookableResource` — Minimum duration: 1 hour (OCP proof).

The `booking-service` **never** imports or references concrete resource types. It interacts solely via `BookableResource` abstractions and REST/Feign DTO representations.

---

## 4. SOLID Principles Mapping

| Principle | Implementation Location | Technical Explanation |
|---|---|---|
| **S (Single Responsibility)** | Across all microservices | Each layer is strictly isolated: `Controller` handles HTTP, `Service` handles business domain, `Repository` handles database queries, `Mapper` handles DTO conversion, `GlobalExceptionHandler` handles errors. |
| **O (Open/Closed)** | `resource-service` & `booking-service` | Added `SportsFieldSlot` without modifying `BookingServiceImpl` or any other microservice. `CancellationPolicy` allows new refund strategies without changing cancellation logic. |
| **L (Liskov Substitution)** | `com.bookflex.resource.domain.*` | `Room`, `AppointmentSlot`, and `SportsFieldSlot` can be substituted anywhere `BookableResource` is expected without breaking invariants. |
| **I (Interface Segregation)** | `NotificationSender` & `PaymentGateway` | Fine-grained interfaces. `NotificationSender` has only `send()`, completely decoupled from payment or booking logic. |
| **D (Dependency Inversion)** | `PaymentService`, `BookingServiceImpl`, `ReviewService` | High-level services depend on abstractions (`PaymentGateway`, `CancellationPolicy`, `ResourceClient`, `BookingClient`), not concrete implementations. Dependencies are injected via Spring Constructor Injection. |

---

## 5. Design Patterns Catalog

### 1. State Pattern
* **Location**: `booking-service/src/main/java/com/bookflex/booking/state/`
* **Why used**: Replaced ugly `switch(status)` statements with polymorphic state classes (`PendingState`, `ConfirmedState`, `CancelledState`, `CompletedState`). State transitions enforce valid business lifecycle rules.

### 2. Strategy Pattern
* **Location**: `booking-service/src/main/java/com/bookflex/booking/policy/`
* **Why used**: Encapsulates cancellation refund logic into interchangeable strategies (`RefundablePolicy`, `NonRefundablePolicy`, `PartialRefundPolicy`). Resolved dynamically at runtime by `CancellationPolicyResolver`.

### 3. Factory Pattern
* **Location**: `resource-service/src/main/java/com/bookflex/resource/factory/ResourceFactory.java`
* **Why used**: Instantiates concrete `BookableResource` domain objects (`Room`, `AppointmentSlot`, `SportsFieldSlot`) from flat persisted `ResourceEntity` database rows.

### 4. Observer Pattern
* **Location**: `booking-service` (Publisher) & `notification-service` (Listener)
* **Why used**: Decouples booking state changes from notification sending via RabbitMQ topic exchanges (`booking.exchange`). When a booking is confirmed/cancelled, event listeners asynchronously consume events.

### 5. Builder Pattern
* **Location**: Domain entities and DTOs (via Lombok `@Builder`) & `BookingEntity`, `ReviewEntity` creation.
* **Why used**: Simplifies construction of complex immutable objects with validated parameters.

### 6. Template Method
* **Location**: `BookingServiceImpl.createBooking`
* **Why used**: Defines the fixed skeleton of a booking transaction (Validate -> Lock -> Reserve -> Create -> Pay -> Confirm/Compensate).

### 7. Singleton Pattern
* **Location**: Spring IoC Container
* **Why used**: All Spring beans (`@Service`, `@Component`, `@Repository`) are singleton-scoped by default, ensuring efficient memory usage without thread safety violations.

---

## 6. Collections Framework & Exception Handling

### Collections Framework Highlights
* **`Map<LocalDate, List<BookingResponse>>`**: Groups resource bookings by calendar date using Java Stream `Collectors.groupingBy()`.
* **`Set<TimeSlot>`**: Guarantees uniqueness of reserved time slots without duplicates.
* **Custom `Comparator`**: Sorts bookings chronologically: `Comparator.comparing(BookingEntity::getStartTime)`.

### Exception Handling
Centralized exception handling with `@RestControllerAdvice` in each service translating domain exceptions to a standardized `ApiErrorResponse` JSON:
* `ResourceAlreadyBookedException` -> `409 CONFLICT`
* `BookingNotFoundException` -> `404 NOT FOUND`
* `ReviewAlreadyExistsException` -> `409 CONFLICT`
* `InvalidBookingException` -> `400 BAD REQUEST`
* `PaymentFailedException` -> `402 PAYMENT REQUIRED`

---

## 7. Concurrency & Double-Booking Prevention

To prevent two simultaneous users from booking the exact same time slot, `booking-service` uses database **Pessimistic Locking**:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("""
    SELECT b FROM BookingEntity b
    WHERE b.resourceId = :resourceId
      AND b.status NOT IN ('CANCELLED')
      AND b.startTime < :endTime AND b.endTime > :startTime
""")
Optional<BookingEntity> findConflictingBookingForUpdate(...);
```

---

## 8. Cross-Service Transactions (Saga Pattern)

Since each microservice owns its database, distributed transactions are coordinated via an **Orchestrated Saga**:

```
Client ──► Booking Service ──► Resource Service (Reserve Slot)
                 │
                 ├──► Payment Service (Process Payment)
                 │         │
                 │         ├─► Success ──► Booking State = CONFIRMED ──► Publish Event
                 │         │
                 │         └─► Failure ──► Compensating Transaction:
                 │                           1. Booking State = CANCELLED
                 │                           2. Resource Service (Release Slot)
```

---

## 9. Deployment & Quick Start

### Prerequisites
* Java 21 & Maven 3.9+
* Docker & Docker Compose

### Building & Running with Docker Compose
```bash
# 1. Build all microservice JARs
mvn clean package -DskipTests

# 2. Start all services, PostgreSQL 16 databases, pgAdmin 4, and RabbitMQ
docker compose up -d --build
```

### Accessing Dashboards
* **API Gateway**: `http://localhost:8088`
* **Eureka Service Registry**: `http://localhost:8761`
* **pgAdmin 4 Dashboard**: `http://localhost:5050` (Email: `admin@bookflex.com`, Password: `admin`)
* **RabbitMQ Management**: `http://localhost:15672` (User: `guest`, Password: `guest`)

---

## 10. Postman Collection & Testing

An updated, complete Postman collection is included under [`postman/BookFlex.postman_collection.json`](file:///home/hassan/DEV/SpringBoot/BookFlex%20Microservices/postman/BookFlex.postman_collection.json).
It includes 5 organized folders with automated test scripts that dynamically save `jwtToken`, `resourceId`, and `bookingId`:

1. `1. User & Auth Service` (Register Customer, Register Provider, Login, Get Current Profile)
2. `2. Resource Service` (Create Room, Create Appointment Slot, Create Sports Field Slot, List Resources)
3. `3. Booking Service` (Create Booking Saga, Get Details, My Bookings, Cancel Booking)
4. `4. Payment & Notification Services` (Get Payments By Booking, User Notifications)
5. `5. Review & Rating Service` (Submit Verified Review, List Resource Reviews, Get Rating Summary)
