  Hotel Inventory App

A full-stack hotel management application built with Angular (frontend) and Spring Boot (backend).
The system supports room management, user administration, and booking functionality, with enforced business rules and role-based access control.

  Features
Frontend (Angular + Signals + Angular Material)

Pages: Home, Bookings, Admin, Login

Route guards:

bookingGuard – protects bookings for logged-in users

adminGuard – restricts admin area to administrators

Booking form: Automatically assigns the logged-in user

Admin panel: CRUD operations for users and rooms using dialogs

Role-based UI: Admin links and actions visible only to administrators

State management: Angular Signals for form state, success, and error messages

User feedback: Real-time success and error messages from backend

Backend (Spring Boot + JPA/Hibernate)

Entities: User, Room, Booking

DTOs: BookingRequestDTO, BookingResponseDTO

REST APIs: Users, Rooms, Bookings

Authentication: Stateless JWT with LoginRequestDTO / LoginResponseDTO

Role-based access control: Admin and user roles enforced at backend and frontend

Booking validations:

No bookings in the past

Check-out date must be after check-in date

Maximum 1 year in advance

No overlapping bookings for the same room

Delete restrictions: Users or rooms with existing bookings cannot be deleted

Error handling: ResponseStatusException with global exception handler

Database initialization:

Default admin user created via @PostConstruct

Sample data loaded via data.sql

Frontend–Backend Communication

Dates sent as yyyy-MM-dd strings and automatically converted to LocalDate in Spring Boot

Clear HTTP status codes for API responses

Angular Signals display backend responses in the UI

  Getting Started
Backend

Run the Spring Boot application:

mvn spring-boot:run

or run it from your IDE.

Default credentials:

Admin user:
username: admin
password: admin123

Sample users:
password for all sample users: password

Sample users, rooms, and bookings are automatically loaded.

Frontend

Install dependencies:

npm install

Run the Angular app:

ng serve

Access the application:

Home: /home

Bookings: /bookings

Admin: /admin

Login: /login

Admin features are visible only for admin users.

  Tech Stack

Frontend: Angular, Angular Material, TypeScript

Backend: Spring Boot, Spring Data JPA, Hibernate

Database: H2 (development) / MySQL (production)

Build tools: Maven, Angular CLI

  Notes

This project demonstrates:

Full-stack architecture with frontend and backend separation

JWT authentication and role-based access control

RESTful API design with proper HTTP status codes

Business validation logic: booking constraints, delete restrictions

Clean code practices: services, repositories, controllers, global exception handling

Portfolio-worthy features: real-world entity relationships, Angular Signals, and clear frontend–backend integration
