import { useState, useCallback } from 'react';
import { validateBeneficiaryName } from '../constants/validations';

function uuid() {
  return typeof crypto !== 'undefined' && crypto.randomUUID
    ? crypto.randomUUID()
    : Math.random().toString(36).slice(2) + Date.now().toString(36);
}

/**
 * Manages beneficiaries per module.
 * @returns {{
 *   getBeneficiaries: (moduleId: string) => Array,
 *   addBeneficiary: (moduleId: string, beneficiaryType: string) => void,
 *   removeBeneficiary: (moduleId: string, beneficiaryId: string) => void,
 *   updateBeneficiary: (moduleId: string, beneficiaryId: string, field: string, value: any) => void,
 *   isModuleValid: (moduleId: string) => boolean,
 *   remainingPct: (moduleId: string) => number,
 *   totalPct: (moduleId: string) => number,
 *   clearModule: (moduleId: string) => void,
 * }}
 */
export function useBeneficiaries() {
  const [beneficiaries, setBeneficiaries] = useState({});

  const getBeneficiaries = useCallback(
    (moduleId) => beneficiaries[moduleId] ?? [],
    [beneficiaries],
  );

  const addBeneficiary = useCallback((moduleId, beneficiaryType) => {
    setBeneficiaries((prev) => ({
      ...prev,
      [moduleId]: [
        ...(prev[moduleId] ?? []),
        { id: uuid(), name: '', relation: 'Cónyuge', pct: '', type: beneficiaryType },
      ],
    }));
  }, []);

  const removeBeneficiary = useCallback((moduleId, beneficiaryId) => {
    setBeneficiaries((prev) => ({
      ...prev,
      [moduleId]: (prev[moduleId] ?? []).filter((b) => b.id !== beneficiaryId),
    }));
  }, []);

  const updateBeneficiary = useCallback((moduleId, beneficiaryId, field, value) => {
    setBeneficiaries((prev) => ({
      ...prev,
      [moduleId]: (prev[moduleId] ?? []).map((b) =>
        b.id === beneficiaryId ? { ...b, [field]: value } : b,
      ),
    }));
  }, []);

  const totalPct = useCallback(
    (moduleId) =>
      (beneficiaries[moduleId] ?? []).reduce((s, b) => s + (Number(b.pct) || 0), 0),
    [beneficiaries],
  );

  const remainingPct = useCallback(
    (moduleId) => 100 - totalPct(moduleId),
    [totalPct],
  );

  const isModuleValid = useCallback(
    (moduleId) => {
      const bens = beneficiaries[moduleId] ?? [];
      if (bens.length === 0) return false;
      const total = bens.reduce((s, b) => s + (Number(b.pct) || 0), 0);
      if (total !== 100) return false;
      return bens.every((b) => {
        const n = Number(b.pct);
        return (
          validateBeneficiaryName(b.name).valid &&
          Number.isInteger(n) &&
          n >= 1 &&
          n <= 100
        );
      });
    },
    [beneficiaries],
  );

  const clearModule = useCallback((moduleId) => {
    setBeneficiaries((prev) => {
      const next = { ...prev };
      delete next[moduleId];
      return next;
    });
  }, []);

  return {
    getBeneficiaries,
    addBeneficiary,
    removeBeneficiary,
    updateBeneficiary,
    isModuleValid,
    remainingPct,
    totalPct,
    clearModule,
  };
}
