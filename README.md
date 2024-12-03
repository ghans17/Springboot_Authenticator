# Spring Boot Project with Custom Security Implementation

This project is a Spring Boot and Angular-based application that implements custom security mechanisms, including JWT access tokens, OTP validation, password setup, and email/SMS-based OTP authentication. The security features are developed without using the Spring Security dependency, relying on custom logic and code.

## Features
- **JWT Access Tokens**: Secure authentication with JWT for managing user sessions.
- **OTP Validation**: One-time password (OTP) sent via SMS and email for verification.
- **Password Setup URLs**: Links for users to set their passwords are sent via email.
- **Custom Email Queue**: Emails are sent through a custom queue using a scheduler.
- **OTP Settings Storage**: A database table to store OTP-related settings and configurations.
- **Lockout Mechanism**: Users are locked out after multiple incorrect OTP attempts, providing protection against brute force attacks.

## Technologies Used
- **Backend**: Spring Boot
- **Frontend**: Angular
- **Database**: MySQL
- **Scheduler**: Custom scheduler for handling OTP and email operations
- **JWT**: For secure token-based authentication
- **SMS & Email Service**: Integration for OTP delivery via both SMS and email


