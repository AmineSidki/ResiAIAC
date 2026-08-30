/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
      colors: {
        // institutional blue — primary actions, active nav
        primary: {
          50: '#eef4ff',
          100: '#d9e6ff',
          300: '#8fb4ff',
          500: '#3b6fe0',
          600: '#2c56b8',
          700: '#1f3f8a',
          900: '#142a5c',
        },
        // amber — warnings, "en attente" states, secondary CTA
        accent: {
          500: '#e08a3b',
          600: '#c26f24',
        },
        // validé / active / libre
        success: {
          500: '#2f9e5c',
        },
        // invalide / fermé sans traitement / delete actions
        danger: {
          500: '#d94848',
        },
        neutral: {
          50: '#f7f8fa',
          100: '#eef0f3',
          300: '#c7ccd4',
          500: '#7c8494',
          700: '#3f4552',
          900: '#1c1f26',
        },
        surface: {
          DEFAULT: '#ffffff',
          dark: '#12141a',
        },
      },
    },
  },
  plugins: [],
}

