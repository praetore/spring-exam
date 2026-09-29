import { copyFileSync, existsSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const dir = join(dirname(fileURLToPath(import.meta.url)), '..', 'src', 'data')
const target = join(dir, 'questions.json')
const example = join(dir, 'questions.example.json')

if (!existsSync(target)) {
  if (!existsSync(example)) {
    console.error('Missing questions.example.json — cannot bootstrap questions.json')
    process.exit(1)
  }
  copyFileSync(example, target)
  console.log('Created src/data/questions.json from questions.example.json')
}
