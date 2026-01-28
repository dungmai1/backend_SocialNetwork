# Social Network Backend API

## 🎯 Project Overview

A RESTful Backend API for a social networking application, supporting features such as posting, commenting, liking, following, real-time notifications, and multi-method authentication.

**Problems Solved:**

- Built a full-featured social platform with complete CRUD for posts, comments, and likes
- Implemented secure authentication/authorization with JWT + OAuth2
- Optimized performance using caching strategy and cursor-based pagination
- Developed real-time notification system via WebSocket

---

## 🛠️ Tech Stack

| Category           | Technologies                         |
| ------------------ | ------------------------------------ |
| **Framework**      | Spring Boot 3.5, Spring Security 6.5 |
| **Database**       | PostgreSQL, Redis (Cache)            |
| **Authentication** | JWT (RSA256), OAuth2 (Google)        |
| **Real-time**      | WebSocket (STOMP)                    |
| **Cloud Storage**  | Cloudinary (Image Upload)            |
| **API Docs**       | Swagger/OpenAPI 3.0                  |
| **Others**         | Lombok, ModelMapper, Spring AOP      |

---

## ✅ What I Built

### Core Features

- **Post Management**: CRUD posts with multi-image upload
- **Comment System**: Nested comments (replies), like comments
- **Like System**: Like/unlike posts & comments with caching
- **Follow System**: Follow/unfollow users, follower/following lists
- **Notification**: Real-time notifications (like, comment, follow, mention)
- **User Management**: Profile CRUD, search, ban/unban (Admin)

### Security & Auth

- JWT Authentication with **RSA-256 signing**
- OAuth2 login with Google
- Role-based access control (USER/ADMIN)
- HttpOnly Cookie for token storage (XSS prevention)
- Refresh token rotation

---

## 💡 Technical Highlights

### 1. JWT with RSA Key Pair

```
- Used RSA-256 instead of HMAC for enhanced security
- Access Token (15 min) + Refresh Token (7 days)
- Token validation with issuer check + expiration check
```

### 2. Caching Strategy (Redis)

```
- Cached like count, user list per post/comment
- @Cacheable for read operations
- @CacheEvict for write operations (maintain consistency)
```

### 3. Cursor-based Pagination

```
- Replaced offset pagination (slow with large datasets)
- Used postTime as cursor → O(1) lookup
- Consistent results when new posts are added
```

### 4. AOP Logging

```
- Automatic logging for all controller/service calls
- Execution time measurement for each request
- Centralized error logging
```

### 5. Clean Architecture

```
- Controller → Service Interface → Service Implementation
- Entity → DTO mapping (ModelMapper)
- Global Exception Handler
- Response wrapper (ApiResponse)
```

---

## 🧩 Challenges & Solutions

| Challenge                    | Solution                                                                        |
| ---------------------------- | ------------------------------------------------------------------------------- |
| **Duplicate notifications**  | Check existing notification before creating new one, update timestamp if exists |
| **N+1 Query Problem**        | Used `JOIN FETCH` in JPQL queries                                               |
| **Cache invalidation**       | `@Caching` annotation to evict multiple caches simultaneously                   |
| **Nested comment deletion**  | Cascade delete replies before deleting parent comment                           |
| **OAuth2 + JWT integration** | Custom SuccessHandler to generate JWT after OAuth2 login                        |

---

## 📁 Project Structure

```
src/main/java/SocialNetwork/
├── auth/              # Authentication (Login, Register, JWT)
├── config/            # Security, WebSocket, AOP configs
├── controllers/       # REST endpoints
├── domain/
│   ├── entities/      # JPA Entities
│   └── models/        # DTOs, Request/Response objects
├── exception/         # Global exception handling
├── repositories/      # JPA Repositories
├── services/          # Service interfaces
└── servicesImpl/      # Service implementations
```

---

## 🚀 Quick Start

```bash
# Prerequisites: Java 17+, PostgreSQL, Redis

# 1. Clone & configure
git clone <repo-url>
# Edit application.properties (DB credentials, Cloudinary keys)

# 2. Run
./mvnw spring-boot:run

# 3. API Docs
http://localhost:8080/swagger-ui.html
```

---

## 📈 Future Improvements

- [ ] Message system (real-time chat)
- [ ] Story feature (24h posts)
- [ ] Full-text search with Elasticsearch
- [ ] Rate limiting & request throttling
- [ ] Unit tests & Integration tests
- [ ] Docker containerization
- [ ] CI/CD pipeline

---

## 📫 Contact

**Author**: Dung Mai  
Feel free to reach out for any questions about this project!
