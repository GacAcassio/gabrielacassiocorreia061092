/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          50:  "#f3f7f2",
          100: "#e3eee0",
          200: "#c7ddc1",
          300: "#a3c59b",
          400: "#7faa72",
          500: "#5f8f55",
          600: "#4a7342",
          700: "#3b5b35",
          800: "#2f472b",
          900: "#253822",
        },
      },
    },
  },
  plugins: [],
};
