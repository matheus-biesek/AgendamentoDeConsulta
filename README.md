# 🏥 Management System for an Ophthalmology Clinic

This repository contains the development of a **modular web system** for managing an ophthalmology clinic, focusing on scalability, security, and organization based on architectural best practices such as **DDD (Domain-Driven Design)**, **Clean Architecture**, and **microservices**.

> 🚧 **Project under development**. This documentation outlines the proposed structure and system goals.

---

## 🧱 Application Architecture

### 🌐 **Frontend – User Interface**

- Built with **vanilla HTML, CSS, and JavaScript**, without SPA frameworks.
- Reusable components and layered organization:
  - `DOM`, `Controller`, `Service`, `Component`.
- Layout structured into `navbar`, `main`, and `footer`, managed by modular CSS.
- Inspired by Clean Architecture, adapted to non-SPA frontend applications.
- Security:
  - **XSS protection**: no user input is rendered as HTML.
  - **CSRF protection**: uses `SameSite=Strict`, `HttpOnly`, `Secure` cookies and includes **CSRF Token**.
- Private pages use **Nginx reverse proxy** to validate sessions with the backend.
- Page structure:
  - Login/authentication
  - Role-based access: healthcare professionals, administrative staff, operational users, and patients.
- The frontend is **dockerized** and statically served via **Nginx**.

---

### 🧠 **Backend – Core of the Application**

- Based on **Hexagonal Architecture (Ports & Adapters)**.
- Divided into three main services:
  1. **Authentication and Authorization**
  2. **Professional Management**
  3. **Scheduling and Appointments**
- Each service runs independently with **asynchronous, event-driven communication via RabbitMQ**.
- Uses a **Service Layer** to centralize business rules.
- Exposes RESTful APIs with **JWT authentication**.
- CORS configured to allow only requests from the authorized frontend.
- Each service is **dockerized**, running on **Wildfly** with customized PostgreSQL datasource settings.

---

## 🧬 Database – Domain-Based Organization

The database is structured into **independent schemas**, representing different business domains, allowing future migration to separate databases (multi-database).

### 🛡️ `auth_service`
- Manages users, passwords, roles (`ADMIN`, `SECRETARY`, `DOCTOR`, etc.).
- Audit tables track sensitive actions.

### 🧑‍⚕️ `worker_service`
- Stores healthcare and administrative staff data.
- Related to the authentication domain by ID only, without cross-schema foreign keys.
- Medical specializations and professional records are managed separately.

### 🗓️ `schedule_service`
- Manages rooms, appointments, consultations, and patient links.
- Full auditing of scheduling and consultation entities.

---

## 🏗️ Architectural Vision and Scalability

- Context separation based on **DDD and Bounded Contexts**.
- Service communication via **DTOs and APIs**, avoiding tight database coupling.
- Use of **audit tables** for traceability.
- Prepared for future evolution into a full microservices architecture.

---

## 🐳 Backend Deployment with Wildfly + Docker

### 📁 Directory Structure:
```
docker-image/
├── Dockerfile
├── configure-wildfly.cli
├── module.xml
├── postgresql-42.7.2.jar
```

### ⚙️ Key Files:

#### `configure-wildfly.cli`
- Removes H2 and adds the PostgreSQL driver.
- Creates `PostgresDS` datasource based on the database container.

#### `module.xml`
- Defines dependencies for the PostgreSQL JDBC driver.

#### `Dockerfile`
- Based on the official Wildfly image.
- Adds an admin user.
- Copies the driver, XML, and executes the `.cli` script.

---

## 🔐 Security and Best Practices

- Context isolation by schema and service.
- Well-defined backend layers for separation of concerns.
- Secure, asynchronous communication using RabbitMQ.
- Secure API with JWT and strict CORS control.
- No user input is rendered as HTML in the frontend.

---

## 📌 Project Goal

To build a professional, scalable, and secure system for managing an ophthalmology clinic, serving as a foundation for future solutions based on **microservices**, **high availability**, and a **modular experience** for both patients and healthcare professionals.
