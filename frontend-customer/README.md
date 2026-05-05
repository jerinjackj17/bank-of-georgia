# Frontend — Bank of Georgia

React 19 web client for the Bank of Georgia banking system.

## Tech Stack

- React 19, Vite 8
- Tailwind CSS 4
- react-router-dom 7
- axios 1.9

## Project Structure

```
frontend/
├── .env                  # Local environment variables (gitignored)
├── .env.example          # Template — copy this to .env
├── services/
│   ├── api.js            # Axios instance (reads VITE_API_URL)
│   └── authApi.js        # Auth API functions
├── routes/
│   └── AppRoutes.jsx     # Route definitions
└── src/
    ├── main.jsx          # App entry point
    ├── App.jsx           # Root component
    └── pages/
        ├── LoginPage.jsx # Password and phone OTP login
        └── OtpPage.jsx   # OTP entry and verification
```

## Setup

```bash
cp .env.example .env
npm install
npm run dev
```

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `VITE_API_URL` | Backend API base URL | `http://localhost:8080/api` |

> Vite bakes env vars into the bundle at build time. If you change `.env`, run `npm run build` again.

## Pages

| Route | Page | Description |
|---|---|---|
| `/` | LoginPage | Sign in with username/email + password, or request a phone OTP |
| `/login/otp` | OtpPage | Enter the 6-digit OTP sent via SMS |
