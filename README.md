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
