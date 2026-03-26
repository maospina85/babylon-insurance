/**
 * Test data for Babylon Insurance E2E tests.
 * Update this file to change test values across all specs.
 */

export const MODULES = {
  death:      'Vida / Fallecimiento',
  disability: 'Invalidez Total',
  accidents:  'Accidentes',
} as const;

export const ASSISTANCES = {
  medico_virtual:   'Médico Virtual 24/7',
  asistencia_hogar: 'Asistencia en el Hogar',
  juridico:         'Asesoría Jurídica',
  psicologico:      'Apoyo Psicológico',
  nutricion:        'Nutrición y Bienestar',
  orientacion_fin:  'Orientación Financiera',
} as const;

export const VALID_HOLDER = {
  name:  'María García López',
  email: 'maria.garcia@test.com',
  phone: '+57 300 000 0000',
  // 35 years old (always valid: > 18, < 70)
  dob:   (() => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 35);
    return d.toISOString().split('T')[0]; // YYYY-MM-DD
  })(),
};

export const INVALID_HOLDER = {
  nameTooShort:    'A',
  nameWithNumbers: 'Juan123',
  emailNoAt:       'notanemail',
  emailNoDomain:   'test@',
  phoneShort:      '+57 123',
  dobUnder18: (() => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 17);
    return d.toISOString().split('T')[0];
  })(),
  dobOver70: (() => {
    const d = new Date();
    d.setFullYear(d.getFullYear() - 70);
    return d.toISOString().split('T')[0];
  })(),
};

export const VALID_BENEFICIARY = {
  name:     'José Pérez',
  relation: 'Cónyuge',
  pct:      '100',
};

export const BENEFICIARY_TWO = {
  name:     'Ana López',
  relation: 'Hijo/a',
  pct:      '50',
};
