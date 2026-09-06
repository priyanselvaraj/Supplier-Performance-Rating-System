# Contributing to Supplier Performance Rating System (SPRS)

Thank you for your interest in contributing to the **Supplier Performance Rating System**! We welcome contributions from developers of all experience levels.

---

## 🛠️ Code of Conduct
- Be respectful, constructive, and collaborative.
- Adhere to the established code style and architectural separation of concerns.

---

## 🚀 Getting Started

### 1. Fork and Clone the Repository
```bash
git clone https://github.com/your-username/Supplier-Performance-Rating-System.git
cd Supplier-Performance-Rating-System
```

### 2. Create a Feature Branch
```bash
git checkout -b feature/your-feature-name
```

---

## 💻 Development Workflow

### Backend (Spring Boot 3.3.3 / Java 17)
1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Build and verify tests:
   ```bash
   mvn clean test
   ```
3. Run the backend locally:
   ```bash
   mvn spring-boot:run
   ```

### Frontend (React 18 / TypeScript / Vite)
1. Navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run tests and start development server:
   ```bash
   npm test
   npm run dev
   ```

---

## 🧪 Testing Guidelines
- All new business logic must include corresponding unit tests in `backend/src/test/java/com/supplier/sprsystem/service/`.
- REST controllers should include integration tests in `backend/src/test/java/com/supplier/sprsystem/integration/`.
- Ensure all 157+ backend tests and 8+ frontend tests pass before opening a pull request.

---

## 📝 Commit & Pull Request Guidelines
- Follow conventional commit conventions:
  - `feat: add supplier email alert notification`
  - `fix: resolve pagination index offset in report service`
  - `docs: update deployment environment instructions`
- Ensure `.env` and sensitive credentials are never committed.
