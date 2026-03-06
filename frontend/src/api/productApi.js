const API_BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

/**
 * Fetches the active product catalogue and normalizes the API shape
 * to match the frontend module/tier structure.
 * @returns {Promise<{ modules: Array }>}
 */
export async function fetchCatalog() {
  const res = await fetch(`${API_BASE}/api/products/catalog`);
  if (!res.ok) throw new Error(`Error cargando catálogo: ${res.status}`);
  const data = await res.json();
  return { ...data, modules: data.modules.map(normalizeModule) };
}

function normalizeModule(m) {
  return {
    id: m.moduleId,
    description: m.description,
    hasBeneficiaries: m.hasBeneficiaries,
    beneficiaryType: m.beneficiaryType,
    tiers: m.tiers.map((t) => ({
      id: t.tierId,
      label: t.label,
      sumAsegurada: t.sumAsegurada,
      prima: t.prima,
      coverages: t.includedCoverageIds ?? [],
    })),
    coverages: m.coverages ?? [],
  };
}
