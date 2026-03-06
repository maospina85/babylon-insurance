import { useState, useMemo, useCallback } from 'react';

/**
 * Manages active modules and selected tiers.
 * @param {Array} modules - modules from the API (via useProduct)
 * @returns {{
 *   activeModules: Object<string, string|null>,
 *   toggleModule: (moduleId: string) => void,
 *   selectTier: (moduleId: string, tierId: string) => void,
 *   isActive: (moduleId: string) => boolean,
 *   getSelectedTier: (moduleId: string) => object|null,
 *   breakdown: Array<{ module: object, tier: object }>,
 *   clearAll: () => void,
 * }}
 */
export function useCoverage(modules = []) {
  // { moduleId: tierId | null }
  const [activeModules, setActiveModules] = useState({});

  const toggleModule = useCallback((moduleId) => {
    setActiveModules((prev) => {
      if (moduleId in prev) {
        const next = { ...prev };
        delete next[moduleId];
        return next;
      }
      return { ...prev, [moduleId]: null };
    });
  }, []);

  const selectTier = useCallback((moduleId, tierId) => {
    setActiveModules((prev) => ({ ...prev, [moduleId]: tierId }));
  }, []);

  const isActive = useCallback(
    (moduleId) => moduleId in activeModules,
    [activeModules],
  );

  const getSelectedTier = useCallback(
    (moduleId) => {
      if (!(moduleId in activeModules)) return null;
      const tierId = activeModules[moduleId];
      if (!tierId) return null;
      const mod = modules.find((m) => m.id === moduleId);
      return mod?.tiers.find((t) => t.id === tierId) ?? null;
    },
    [activeModules],
  );

  const breakdown = useMemo(
    () =>
      modules.filter((m) => m.id in activeModules && activeModules[m.id] !== null)
        .map((m) => ({
          module: m,
          tier: m.tiers.find((t) => t.id === activeModules[m.id]),
        }))
        .filter(({ tier }) => tier !== undefined),
    [activeModules, modules],
  );

  const clearAll = useCallback(() => setActiveModules({}), []);

  return { activeModules, toggleModule, selectTier, isActive, getSelectedTier, breakdown, clearAll };
}
