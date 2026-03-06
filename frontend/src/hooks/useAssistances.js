import { useState, useMemo, useCallback, useEffect } from 'react';
import { ASSISTANCES } from '../constants/catalog';

/**
 * Manages selected assistance services.
 * @returns {{
 *   selected: Set<string>,
 *   toggleAssistance: (id: string) => void,
 *   isSelected: (id: string) => boolean,
 *   selectedList: Array<object>,
 *   clearAll: () => void,
 * }}
 */
export function useAssistances(maxSelectable = 0) {
  const [selected, setSelected] = useState(new Set());

  const toggleAssistance = useCallback((id) => {
    setSelected((prev) => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else if (next.size < maxSelectable) {
        next.add(id);
      }
      return next;
    });
  }, [maxSelectable]);

  // When coverages are removed, trim selected assistances to the new limit
  useEffect(() => {
    setSelected((prev) => {
      if (prev.size <= maxSelectable) return prev;
      const trimmed = [...prev].slice(0, maxSelectable);
      return new Set(trimmed);
    });
  }, [maxSelectable]);

  const isSelected = useCallback((id) => selected.has(id), [selected]);

  const selectedList = useMemo(
    () => ASSISTANCES.filter((a) => selected.has(a.id)),
    [selected],
  );

  const clearAll = useCallback(() => setSelected(new Set()), []);

  return { selected, toggleAssistance, isSelected, selectedList, clearAll };
}
