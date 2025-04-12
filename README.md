AI Property Viewing System

An AI-powered backend service to streamline property viewing management between landlords and tenants. This system integrates OpenAI GPT for natural conversation-based appointment scheduling, backed by a robust Spring Boot and PostgreSQL stack with secure JWT authentication.

Features Implemented
✅ Core Architecture
Built with Spring Boot (Java 17)

PostgreSQL database for relational data persistence

JPA/Hibernate for ORM

JWT-based authentication with role-based access control

OpenAI GPT integration for AI chat-based interaction

✅ Functional Components
User Management
Landlords and tenants registration, login, and authentication

Property Management
Landlords can create and manage property listings

Appointment Scheduling
Tenants can initiate property viewings via AI-powered chat

Chat Sessions
GPT-assisted natural language interface for tenants to schedule appointments

✅ Security Features
JWT Authentication for secure access to protected endpoints

BCrypt Password Encryption

Role-Based Access Control (RBAC) for tenant and landlord operations

AI Chat Flow
POST /api/auth/register
POST /api/auth/login
 Start Chat Session
First, you need to start a conversation. The endpoint is:
POST /api/v1/ai-appointments/start
Headers: Authorization: Bearer <jwt_token>
This will return a conversationId that you'll need for subsequent requests.
Then, you can process an appointment request:
POST /api/v1/ai-appointments/process
Content-Type: application/json

{
    "conversationId": "your-conversation-id",
    "userMessage": "I want to schedule a viewing for property 123 on Monday at 2pm"
}

To validate the extracted details:
POST /api/v1/ai-appointments/validate/{appointmentId}

To confirm the appointment:
POST /api/v1/ai-appointments/confirm/{appointmentId}?confirmationCode=your-code
