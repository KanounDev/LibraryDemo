# After cloning the repository

cd LibraryDemo

## 1. Backend (Spring Boot)

cd library-backend

# Option A: Use the included Maven wrapper (recommended - no global Maven install needed)
.\mvnw clean install

# Option B: If you have Maven installed globally
mvn clean install

# Run the backend
.\mvnw spring-boot:run
# or
mvn spring-boot:run

# The backend should now be running on http://localhost:8080 (or 8081 if you changed the port)
# Keep this terminal open

# Note: Make sure MongoDB is running locally!
#     - If not installed: download & install MongoDB Community Server
#     - Then open a new terminal and run: mongod
#     (or start it as a service)
## 2. Frontend (Angular)

# Open a **new** terminal / PowerShell window
# (do NOT close the backend terminal)

cd ..\library-frontend

# Install dependencies
npm install

# Run the frontend in development mode
ng serve

# Open your browser at: http://localhost:4200
# You should see the login page

```text
# Library Management System Demo

A full-stack demo application with **Spring Boot** (backend) + **Angular** (frontend) + **MongoDB**, implementing secure user registration and login with role-based access (MEMBER, LIBRARIAN, ADMIN) and branch validation.

## Project Structure
LibraryDemo/
├── library-backend/       # Spring Boot + Spring Security + JWT + MongoDB
│   ├── src/
│   ├── pom.xml
│   └── application.properties
└── library-frontend/      # Angular 17+ (standalone components)
    ├── src/
    ├── angular.json
    └── package.json

## How the Frontend Interacts with the Backend (Demo Flow)

This section explains the **end-to-end authentication flow** — what happens when a user registers or logs in.

### 1. Registration Flow

1. **User opens the app** → http://localhost:4200  
   → Redirects to `/login` (or shows home with Login/Register buttons)

2. **User clicks "Register"** → navigates to `/register`

3. **Frontend collects data**  
   - `username`, `password`, `role` (MEMBER / LIBRARIAN / ADMIN), `branch` (only shown for LIBRARIAN/ADMIN)  
   - All fields use `[(ngModel)]` two-way binding

4. **User submits the form**  
   - `register()` method in `register.ts` is called  
   - Prepares payload object (JSON)  
   - Calls `authService.register(payload)` → HTTP POST request

5. **Request sent to backend**  
   URL: `POST http://localhost:8080/api/auth/register`  
   Body (JSON example):

   ```json
   {
     "username": "johnlibrarian",
     "password": "pass123",
     "role": "LIBRARIAN",
     "branch": "Downtown"
   }
   ```

6. **Backend processing** (AuthController → AuthService)
   - Validates input (e.g. branch required for LIBRARIAN/ADMIN)
   - Hashes password with BCrypt
   - Saves user to MongoDB
   - Generates JWT token (with username, role, branch in payload)
   - Returns { "token": "eyJhbGciOiJIUzI1NiIs..." }

7. **Frontend receives response**
   - `authService.saveToken(response.token)` → stores JWT in localStorage
   - Redirects to /home (or dashboard)

8. **Result**
   User is now registered and automatically logged in.

### 2. Login Flow

1. **User fills login form**
   - username, password, role (to show/hide branch field)
   - branch only if role ≠ MEMBER

2. **User clicks "Login"**
   - `login()` method prepares payload (only sends branch if needed)
   - Calls `authService.login(payload)` → POST request

3. **Request to backend**
   URL: `POST http://localhost:8080/api/auth/login`
   Body example:
   ```json
   {
     "username": "johnlibrarian",
     "password": "pass123",
     "branch": "Downtown"
   }
   ```

4. **Backend validation**
   - Authenticates username/password via AuthenticationManager
   - Loads user from MongoDB
   - For LIBRARIAN/ADMIN: checks branch matches exactly (case-insensitive)
   - If citizen (MEMBER): rejects if branch was sent
   - Generates JWT token
   - Returns token in response

5. **Frontend success**
   - Saves token to localStorage
   - Redirects to /home

6. **Protected routes**
   - Future requests include `Authorization: Bearer <token>` header (handled by HttpInterceptor or manual)
   - JwtAuthenticationFilter validates token → sets SecurityContext
   - Role/branch info available via `authService.getRole()` / `getBranch()`

### Summary – Key Interaction Points

| Step | Frontend Action | HTTP Request | Backend Action | Response to Frontend |
|------|-----------------|--------------|----------------|---------------------|
| Register | form submit → authService.register(user) | POST /api/auth/register | Validate → hash → save → generate JWT | { token: "..." } |
| Login | form submit → authService.login(payload) | POST /api/auth/login | Authenticate → check branch → generate JWT | { token: "..." } |
| After success | saveToken() → router.navigate(['/home']) | — | — | Redirect to dashboard |
| Later requests | Add Bearer token in headers | GET/POST protected endpoints | JwtAuthenticationFilter validates token | Normal response or 401 |

### Visual Flow (Text Diagram)

```
Browser (Angular)                  Backend (Spring Boot)
     │                                     │
User fills form ──► submit ──► POST /register or /login
     │                                     │
     └─────────────► HttpClient ───────────┘
                                           │
                                       Validate
                                     Hash / Auth
                                    Save / Check
                                     Generate JWT
                                           │
                               ◄─────────────┘
                                           │
                                 { "token": "eyJ..." }
                                           │
                                 save to localStorage
                                           │
                                     redirect /home
```

This flow demonstrates secure JWT-based authentication, role-based validation, and branch-specific access control — the core features of the project.

```
