import { createContext, useContext } from 'react';

export const ThemeContext = createContext(null);

/**
 * Hook to access the current client theme.
 * Must be used inside a ThemeContext.Provider.
 * @returns {import('../styles/theme').Theme}
 */
export function useTheme() {
  const theme = useContext(ThemeContext);
  if (!theme) throw new Error('useTheme must be used within a ThemeContext.Provider');
  return theme;
}
