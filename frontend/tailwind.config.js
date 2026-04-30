/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      
      colors: {
        navy: {
          50:  "#eef1f8",
          100: "#d4dced",
          200: "#a9b9db",
          300: "#7e96c9",
          400: "#5373b7",
          500: "#2850a5",
          600: "#1a3a80",
          700: "#112a60",   
          800: "#0b1d45",   
          900: "#060e24",   
        },
        gold: {
          50:  "#fffbeb",
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
        card: {
          
          base:    "#f8f9fc",
          surface: "#ffffff",
          muted:   "#f1f4fa",
          border:  "#e2e8f0",
        },
      },

      // ── Typography 
      fontFamily: {
        display: ["'Playfair Display'", "serif"],   
        body:    ["'DM Sans'", "sans-serif"],        
        mono:    ["'JetBrains Mono'", "monospace"],  
      },

      // ── Spacing & sizing
      borderRadius: {
        card: "1rem",     
        input: "0.5rem",  
      },

      // ── Box shadows for cards 
      boxShadow: {
        card:  "0 4px 24px rgba(6, 14, 36, 0.18)",
        float: "0 8px 40px rgba(6, 14, 36, 0.28)",
      },
    },
  },
  plugins: [],
};