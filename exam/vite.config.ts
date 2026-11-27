import { copyFileSync, existsSync } from 'node:fs'
import { resolve } from 'node:path'
import react from '@vitejs/plugin-react'
import { defineConfig, type Plugin } from 'vite'

function ensureQuestionsJson(): Plugin {
  return {
    name: 'ensure-questions-json',
    configResolved(config) {
      const dataDir = resolve(config.root, 'src/data')
      const target = resolve(dataDir, 'questions.json')
      const example = resolve(dataDir, 'questions.example.json')
      if (!existsSync(target) && existsSync(example)) {
        copyFileSync(example, target)
        console.log('[ensure-questions-json] created src/data/questions.json from example')
      }
    },
  }
}

export default defineConfig({
  plugins: [react(), ensureQuestionsJson()],
  server: {
    port: 5173,
    // Optioneel: Spring sandbox in backend/ meedraaien
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
