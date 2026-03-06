/**
 * Pure validation functions. All return { valid: boolean, message: string }.
 */

/**
 * @param {string} name
 * @returns {{ valid: boolean, message: string }}
 */
export function validateBeneficiaryName(name) {
  if (!name || name.trim() === '')
    return { valid: false, message: 'El nombre es requerido' };
  if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,100}$/.test(name.trim()))
    return { valid: false, message: 'Solo letras y espacios, entre 2 y 100 caracteres' };
  return { valid: true, message: '' };
}

/**
 * @param {string | number} pct - New value being set
 * @param {number} currentTotal - Sum of all beneficiaries' percentages
 * @param {number} ownPct - Current percentage of this beneficiary
 * @returns {{ valid: boolean, message: string }}
 */
export function validateBeneficiaryPct(pct, currentTotal, ownPct) {
  const n = Number(pct);
  if (pct === '' || pct == null) return { valid: false, message: 'El porcentaje es requerido' };
  if (isNaN(n) || !Number.isInteger(n)) return { valid: false, message: 'Debe ser un número entero' };
  if (n < 1 || n > 100) return { valid: false, message: 'Debe ser entre 1 y 100' };
  const projectedTotal = currentTotal - ownPct + n;
  if (projectedTotal > 100)
    return { valid: false, message: `Excede el 100% en ${projectedTotal - 100}%` };
  return { valid: true, message: '' };
}

/**
 * @param {{ pct: string | number }[]} beneficiaries
 * @returns {{ valid: boolean, message: string }}
 */
export function validateBeneficiariesTotal(beneficiaries) {
  if (!beneficiaries || beneficiaries.length === 0)
    return { valid: false, message: 'Agrega al menos un beneficiario' };
  const total = beneficiaries.reduce((s, b) => s + (Number(b.pct) || 0), 0);
  if (total !== 100)
    return { valid: false, message: `La suma debe ser exactamente 100% (actual: ${total}%)` };
  return { valid: true, message: '' };
}

/**
 * @param {string} name
 * @returns {{ valid: boolean, message: string }}
 */
export function validateHolderName(name) {
  return validateBeneficiaryName(name);
}

/**
 * @param {string} email
 * @returns {{ valid: boolean, message: string }}
 */
export function validateHolderEmail(email) {
  if (!email || email.trim() === '')
    return { valid: false, message: 'El correo es requerido' };
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim()))
    return { valid: false, message: 'Ingresa un correo válido' };
  return { valid: true, message: '' };
}

/**
 * @param {string} phone
 * @returns {{ valid: boolean, message: string }}
 */
export function validateHolderPhone(phone) {
  if (!phone || phone.trim() === '')
    return { valid: false, message: 'El teléfono es requerido' };
  if (!/^[+\s\-\d]{7,20}$/.test(phone.trim()))
    return { valid: false, message: 'Mínimo 7 dígitos. Permite +, espacios y guiones' };
  return { valid: true, message: '' };
}

/**
 * @param {string} dob - ISO date string (YYYY-MM-DD)
 * @returns {{ valid: boolean, message: string }}
 */
export function validateHolderDob(dob) {
  if (!dob) return { valid: false, message: 'La fecha de nacimiento es requerida' };
  const date = new Date(dob);
  if (isNaN(date.getTime())) return { valid: false, message: 'Fecha inválida' };
  const today = new Date();
  const age =
    today.getFullYear() -
    date.getFullYear() -
    (today < new Date(today.getFullYear(), date.getMonth(), date.getDate()) ? 1 : 0);
  if (age < 18) return { valid: false, message: 'Debes ser mayor de 18 años' };
  if (age >= 70) return { valid: false, message: 'Debes ser menor de 70 años' };
  return { valid: true, message: '' };
}
