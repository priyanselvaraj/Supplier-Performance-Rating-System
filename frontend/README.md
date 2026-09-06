# Supplier Performance Rating System (SPRS) — Frontend

Modern, high-performance Single Page Application (SPA) built for the **Supplier Performance Rating System**.

---

## 🛠 Technology Stack

- **Framework**: React 18 + Vite
- **Language**: TypeScript / Modern JavaScript (ES Modules)
- **Styling**: Tailwind CSS 3.4 + PostCSS
- **Routing**: React Router DOM v6
- **HTTP Client**: Axios with JWT Bearer Interceptors
- **Charts & Visualizations**: Chart.js 4 + React-Chartjs-2
- **Icons**: Lucide React

---

## 🚀 Getting Started

### 1. Prerequisites
- Node.js (v18.0.0 or later)
- npm (v9.0.0 or later)
- Spring Boot Backend running on `http://localhost:8080`

### 2. Environment Configuration
Create a `.env` file in the `frontend` root (or copy `.env.example`):
```bash
cp .env.example .env
```
Ensure the API base URL points to the running backend:
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_API_URL=http://localhost:8080/api/v1
```

### 3. Install Dependencies
```bash
cd frontend
npm install
```

### 4. Start Development Server
```bash
npm run dev
```
The application will launch at `http://localhost:5173`.

### 5. Production Build
```bash
npm run build
```
Build output is saved to `frontend/dist/`.

---

## 📂 Project Architecture

```
frontend/src/
├── api/ / services/
│   ├── api.js                   # Centralized Axios client & JWT interceptors
│   ├── auth.service.js          # Authentication & Profile endpoints
│   ├── category.service.js      # Supplier Categories CRUD
│   ├── criteria.service.js      # Evaluation Criteria & Weights
│   ├── dashboard.service.js     # Executive KPI & Analytics
│   ├── evaluation.service.js    # Multi-criteria scoring & workflow
│   ├── rating.service.js        # Automated rating engine & history
│   ├── report.service.js        # PDF / Excel / CSV exports & reports
│   ├── supplier.service.js      # Supplier Management CRUD & Search
│   └── user.service.js          # User Administration
│
├── components/
│   ├── common/                  # Reusable UI (Button, Card, Modal, Table, Badge)
│   └── layout/                  # DashboardLayout, Navbar, Sidebar, ProtectedRoute
│
├── context/
│   └── AuthContext.jsx          # Centralized JWT & Role state management
│
├── pages/
│   ├── auth/                    # Login & Register
│   ├── dashboard/               # Executive Dashboard
│   ├── suppliers/               # Supplier List & Details
│   ├── categories/              # Category List & Modal
│   ├── criteria/                # Criteria List & Weights
│   ├── evaluations/             # Evaluation List, Multi-criteria Form & Details
│   ├── ratings/                 # Performance Ratings & Supplier Timeline
│   ├── analytics/               # Visualizations & Statistical Trends
│   ├── reports/                 # Multi-format Reports & File Exports
│   ├── users/                   # Admin User Management
│   └── profile/                 # Account Profile & Password Reset
│
├── types/
│   └── index.ts                 # TypeScript type definitions for Backend DTOs
│
├── App.jsx
└── main.jsx
```

---

## 🔒 Authentication & Role Security

- **Tokens**: JWT token is securely stored in `localStorage` and injected via the Axios Bearer request interceptor.
- **Expiration**: 401 Unauthorized responses trigger automatic cleanup and safe login redirection.
- **Role-Based Guards**: `ProtectedRoute` protects admin-only views (`/criteria`, `/users`, `/reports/overall`).
- **Backend Validation**: Real security is strictly enforced on the Spring Boot backend via `@PreAuthorize`.
