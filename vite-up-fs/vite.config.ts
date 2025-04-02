import { defineConfig } from 'vite'

// https://vitejs.dev/config/
export default defineConfig({
  clearScreen: false,
  server: {
    watch: {
      ignored: ["**/*.fs"]
    }
  }
})
