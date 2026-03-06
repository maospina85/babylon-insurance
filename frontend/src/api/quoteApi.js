const API_BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

/**
 * Submits a new insurance quote to the backend.
 * @param {object} payload - CreateQuoteRequest shape
 * @returns {Promise<object>} - QuoteResponse
 */
export async function createQuote(payload) {
  const res = await fetch(`${API_BASE}/api/quotes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message ?? `Error al crear cotización: ${res.status}`);
  }
  return res.json();
}
