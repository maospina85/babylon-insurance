/**
 * Formats a number as Colombian Peso currency, no decimals.
 * @param {number} n
 * @returns {string}
 */
export function copFmt(n) {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(n);
}

/**
 * Short human-readable format: $10M, $2.5M, $500K. null → "Incluido".
 * @param {number | null} n
 * @returns {string}
 */
export function shortFmt(n) {
  if (n == null) return 'Incluido';
  if (n >= 1_000_000) {
    const v = n / 1_000_000;
    return `$${Number.isInteger(v) ? v : v.toFixed(1)}M`;
  }
  if (n >= 1_000) return `$${(n / 1_000).toFixed(0)}K`;
  return `$${n}`;
}

/** Asistencias modulares sin costo */
export const ASSISTANCES = [
  {
    id: 'medico_virtual',
    label: 'Médico Virtual 24/7',
    icon: '🩺',
    color: '#2B5BF5',
    desc: 'Consultas médicas ilimitadas por videollamada.',
  },
  {
    id: 'asistencia_hogar',
    label: 'Asistencia en el Hogar',
    icon: '🏠',
    color: '#7B2FF7',
    desc: 'Plomería, electricidad y cerrajería de emergencia.',
  },
  {
    id: 'juridico',
    label: 'Asesoría Jurídica',
    icon: '⚖️',
    color: '#059669',
    desc: 'Consultas legales telefónicas ilimitadas.',
  },
  {
    id: 'psicologico',
    label: 'Apoyo Psicológico',
    icon: '🧠',
    color: '#E91E8C',
    desc: 'Sesiones de orientación psicológica virtual.',
  },
  {
    id: 'nutricion',
    label: 'Nutrición y Bienestar',
    icon: '🥗',
    color: '#EA580C',
    desc: 'Asesoría nutricional personalizada.',
  },
  {
    id: 'orientacion_fin',
    label: 'Orientación Financiera',
    icon: '💰',
    color: '#0891B2',
    desc: 'Asesoría en finanzas personales y deudas.',
  },
];

/** Parentescos válidos para beneficiarios */
export const RELATIONS = ['Cónyuge', 'Hijo/a', 'Padre/Madre', 'Hermano/a', 'Otro'];
