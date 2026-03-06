// init-indexes.js — Crear índices en MongoDB Atlas
// Uso: mongosh "URI_DE_ATLAS" --file init-indexes.js
//
// Este script es idempotente: createIndex() ignora índices ya existentes
// con el mismo nombre y definición.

use("babylon");

// ─── Colección: quotes ────────────────────────────────────────────────────────

db.quotes.createIndex(
  { policyNumber: 1 },
  { unique: true, name: "idx_policyNumber_unique" }
);

db.quotes.createIndex(
  { correlationId: 1 },
  { name: "idx_correlationId" }
);

db.quotes.createIndex(
  { createdAt: -1 },
  { name: "idx_createdAt_desc" }
);

db.quotes.createIndex(
  { status: 1, createdAt: -1 },
  { name: "idx_status_createdAt" }
);

// ─── Colección: products ──────────────────────────────────────────────────────

db.products.createIndex(
  { productCode: 1, status: 1 },
  { name: "idx_productCode_status" }
);

db.products.createIndex(
  { version: -1 },
  { name: "idx_version_desc" }
);

// ─── Verificación ─────────────────────────────────────────────────────────────

print("Índices quotes:");
printjson(db.quotes.getIndexes());
print("Índices products:");
printjson(db.products.getIndexes());
