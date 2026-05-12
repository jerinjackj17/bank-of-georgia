/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        staff: {
          50: "#f8fafc",
          100: "#f1f5f9",
          200: "#e2e8f0",
          300: "#cbd5e1",
          400: "#94a3b8",
          500: "#64748b",
          600: "#475569",
          700: "#334155",
          800: "#1e293b",
          900: "#0f172a",
          950: "#020617",
        },
        amber: {
          50: "#fffbeb",
          100: "#fef3c7",
          200: "#fde68a",
          300: "#fcd34d",
          400: "#fbbf24",
          500: "#f59e0b",
          600: "#d97706",
          700: "#b45309",
          800: "#92400e",
          900: "#78350f",
        },
        panel: {
          dark: "#111827",
          base: "#f8fafc",
          surface: "#ffffff",
          border: "#e5e7eb",
        },
      },

      fontFamily: {
        display: ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
        body: ["Inter", "ui-sans-serif", "system-ui", "sans-serif"],
        mono: ["JetBrains Mono", "monospace"],
      },

      borderRadius: {
        card: "1rem",
        input: "0.65rem",
      },

      boxShadow: {
        card: "0 10px 30px rgba(15, 23, 42, 0.08)",
        float: "0 20px 50px rgba(15, 23, 42, 0.18)",
      },
    },
  },
  plugins: [],
};