# Service Request System (SRS) Specification
## 1. Overview
   The **Service Request System (SRS)** is a platform designed to streamline the submission, tracking, and management of service requests within an organization. It supports two primary user roles—Managers and Requesters—enabling efficient service definition, request processing, and basic search functionality. The system aims to improve operational workflows, ensure secure access, and provide flexibility through customizable service fields.

## 2. Features
   2.1 Service Management
   - Create, update, and delete service offerings
   - Define service categories
   - View all service offerings with optional search by name
   - Enable/disable service availability
###   2.2 Request Management
   - Submit service requests with custom fields and optional attachments
   - View all requests (Manager) or personal request history (Requester)
   - Update request statuses (Manager only)
   - Filter requests by status
###   2.3 Status Management
   - Predefined request statuses: PENDING, IN_PROGRESS, COMPLETED
   - Retrieve list of valid statuses for use in requests
###   2.4 User & Role Management
   - Role-based access control (Managers and Requesters)
   - Secure authentication for all users
###   2.5 Search & Filtering
   - Search service offerings by name (partial match, case-insensitive)
   - Filter requests by status (exact match)
## 3. Technical Specifications
### 3.1 Architecture
   - Backend: Spring Boot (Java)
   - Frontend: Not specified (REST API consumer, e.g., Angular/React)
   - Database: Not specified (Postgres, maria etc.)
   - Cloud Hosting: Compatible with AWS/Azure or on-premise deployment
   - Security: Spring Security with JWT-based authentication, Role-based Access Control (RBAC)
###   3.2 Status Management System
   - Statuses: Implemented as an enum (RequestStatus) with fixed values:
   - PENDING: "Awaiting processing"
   - IN_PROGRESS: "Being worked on"
   - COMPLETED: "Finished"
   - Exposure: Accessible via API endpoint for validation and display 
   - Storage: Persisted as strings in the database (e.g., "PENDING")
###   3.3 Deployment & Scalability
   - Architecture: Monolithic (Spring Boot application), extensible to microservices
   - Containerization: Docker support for deployment
   - Scalability: Horizontal scaling with load balancing (e.g., Kubernetes)
###   3.4 Data Security & Compliance
   - Authentication: JWT tokens for secure user access
   - Authorization: RBAC with roles ROLE_MANAGER and ROLE_REQUESTER
   - Data Protection: Custom fields stored securely in JSONB columns 
   - Audit Logging: Not implemented (optional for tracking changes)
###   3.5 Data Model
####   3.5.1 Core Entities
   - User: System users with authentication and role details
   - ServiceOffering: Defines available services with custom fields
   - Request: Represents a service request with status and custom data
   - Category: Groups service offerings
####   3.5.2 Entity Details
#####   User:
   - id: Unique identifier (Long)
   - username: Login credential (String)
   - password: Hashed password (String)
   - role: Enum (MANAGER, REQUESTER)
#####   ServiceOffering:
   - id: Unique identifier (Long)
   - name: Service name, unique (String)
   - description: Optional description (String)
   - category: Foreign key to Category (Long)
   - fields: JSONB column for custom field definitions (String)
   - isActive: Availability flag (Boolean)
#####   Request:
   - id: Unique identifier (Long)
   - user: Foreign key to User (Long)
   - serviceOffering: Foreign key to ServiceOffering (Long)
   - status: Enum (PENDING, IN_PROGRESS, COMPLETED)
   - submissionDate: Creation timestamp (LocalDateTime)
   - fieldsData: JSONB column for submitted data (String)
   - attachmentUrl: Optional attachment URL (String)
#####   Category:
   - id: Unique identifier (Long)
   - name: Category name (String)
## 4. Conclusion
   The Service Request System (SRS) offers a streamlined solution for managing service requests within an organization. It provides Managers with tools to define and oversee services and requests, while Requesters can easily submit and track their requests. The use of a static RequestStatus enum simplifies status management, and basic search functionality enhances usability. Built on Spring Boot with PostgreSQL, the system ensures security, scalability, and flexibility through JSON-based custom fields.
