// setup.js — Inicialización completa de MongoDB Atlas para Babylon Insurance
// Uso: mongosh "URI_DE_ATLAS" --file setup.js
//
// Ejecutar una sola vez contra el cluster de Atlas para dejar la base de datos
// lista: colecciones con validación de esquema + todos los índices.
// El script es idempotente respecto a los índices, pero recrea las colecciones
// si no existen. Si ya existen, el bloque try/catch omite el error.

use("babylon");

// ─── 1. Colección: quotes ─────────────────────────────────────────────────────

try {
  db.createCollection("quotes", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: [
          "quoteId",
          "policyNumber",
          "status",
          "totalMonthlyPrima",
          "selectedCoverages",
          "paymentFrequency",
          "createdAt"
        ],
        properties: {
          quoteId: {
            bsonType: "string",
            description: "UUID v4 requerido"
          },
          policyNumber: {
            bsonType: "string",
            pattern: "^BLF-[A-Z0-9]+$",
            description: "Formato BLF-XXXXXXX requerido"
          },
          status: {
            bsonType: "string",
            enum: ["QUOTED", "ISSUED", "CANCELLED"],
            description: "Estado válido requerido"
          },
          totalMonthlyPrima: {
            bsonType: "decimal",
            minimum: 0,
            description: "Prima mensual no puede ser negativa"
          },
          paymentFrequency: {
            bsonType: "string",
            enum: ["mensual", "anual"]
          },
          selectedCoverages: {
            bsonType: "array",
            minItems: 1,
            maxItems: 4,
            items: {
              bsonType: "object",
              required: ["moduleId", "tierId", "prima"],
              properties: {
                moduleId: {
                  bsonType: "string",
                  enum: ["death", "disability", "accidents", "assistance"]
                },
                tierId: {
                  bsonType: "string",
                  pattern: "^t[1-4]$"
                },
                prima: {
                  bsonType: "decimal",
                  minimum: 0
                }
              }
            }
          },
          beneficiaries: {
            bsonType: "array",
            items: {
              bsonType: "object",
              required: ["name", "relation", "pct", "moduleId"],
              properties: {
                pct: {
                  bsonType: "int",
                  minimum: 1,
                  maximum: 100
                },
                relation: {
                  bsonType: "string",
                  enum: ["Cónyuge", "Hijo/a", "Padre/Madre", "Hermano/a", "Otro"]
                }
              }
            }
          },
          holderNameEncrypted: {
            bsonType: "string",
            description: "Siempre cifrado — nunca en claro"
          },
          holderEmailEncrypted: {
            bsonType: "string",
            description: "Siempre cifrado — nunca en claro"
          }
        }
      }
    },
    validationLevel: "moderate",
    validationAction: "warn"
  });
  print("Colección 'quotes' creada.");
} catch (e) {
  print("Colección 'quotes' ya existe, omitiendo creación: " + e.message);
}

// ─── 2. Colección: products ───────────────────────────────────────────────────

try {
  db.createCollection("products", {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["productCode", "version", "status", "modules"],
        properties: {
          productCode: { bsonType: "string" },
          version:     { bsonType: "string" },
          status: {
            bsonType: "string",
            enum: ["ACTIVE", "INACTIVE"]
          },
          modules: {
            bsonType: "array",
            minItems: 1,
            items: {
              bsonType: "object",
              required: ["moduleId", "tiers"],
              properties: {
                moduleId: {
                  bsonType: "string",
                  enum: ["death", "disability", "accidents", "assistance"]
                },
                tiers: {
                  bsonType: "array",
                  minItems: 1,
                  maxItems: 4
                }
              }
            }
          }
        }
      }
    },
    validationLevel: "moderate",
    validationAction: "warn"
  });
  print("Colección 'products' creada.");
} catch (e) {
  print("Colección 'products' ya existe, omitiendo creación: " + e.message);
}

// ─── 3. Índices: quotes ───────────────────────────────────────────────────────

db.quotes.createIndex(
  { policyNumber: 1 },
  { unique: true, name: "idx_policyNumber_unique" }
);
db.quotes.createIndex({ correlationId: 1 },   { name: "idx_correlationId" });
db.quotes.createIndex({ createdAt: -1 },       { name: "idx_createdAt_desc" });
db.quotes.createIndex({ status: 1, createdAt: -1 }, { name: "idx_status_createdAt" });

// ─── 4. Índices: products ─────────────────────────────────────────────────────

db.products.createIndex({ productCode: 1, status: 1 }, { name: "idx_productCode_status" });
db.products.createIndex({ version: -1 },               { name: "idx_version_desc" });

// ─── 5. Verificación final ────────────────────────────────────────────────────

print("\n=== Setup completado ===");
print("Colecciones: " + db.getCollectionNames());
print("Quotes indexes:   " + db.quotes.getIndexes().length);
print("Products indexes: " + db.products.getIndexes().length);
