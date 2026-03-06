// generate-key.js — Genera una clave AES-256 válida para BABYLON_ENCRYPTION_KEY
// Uso: node generate-key.js

const crypto = require('crypto');
const key = crypto.randomBytes(32).toString('base64');

console.log('BABYLON_ENCRYPTION_KEY=' + key);
console.log('');
console.log('Guarda este valor en:');
console.log('  - Local:  backend/.env  (nunca commitear — está en .gitignore)');
console.log('  - GCP:    Secret Manager → secret "BABYLON_ENCRYPTION_KEY"');
console.log('  - GitHub: Settings → Secrets → Actions → BABYLON_ENCRYPTION_KEY');
