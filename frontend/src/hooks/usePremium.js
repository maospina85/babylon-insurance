import { useMemo } from 'react';

/**
 * Reactive premium calculations from the active module breakdown.
 * @param {Array<{ module: object, tier: object }>} breakdown
 * @returns {{
 *   totalMonthly: number,
 *   totalAnnual: number,
 *   totalAnnualDiscounted: number,
 *   annualSavings: number,
 *   totalSumAsegurada: number,
 *   isEmpty: boolean,
 * }}
 */
export function usePremium(breakdown) {
  return useMemo(() => {
    const totalMonthly = breakdown.reduce((s, { tier }) => s + (tier?.prima ?? 0), 0);
    const totalAnnual = totalMonthly * 12;
    const totalAnnualDiscounted = Math.round(totalAnnual * 0.9);
    const annualSavings = Math.round(totalAnnual * 0.1);
    const totalSumAsegurada = breakdown.reduce(
      (s, { tier }) => s + (tier?.sumAsegurada ?? 0),
      0,
    );
    return {
      totalMonthly,
      totalAnnual,
      totalAnnualDiscounted,
      annualSavings,
      totalSumAsegurada,
      isEmpty: totalMonthly === 0,
    };
  }, [breakdown]);
}
