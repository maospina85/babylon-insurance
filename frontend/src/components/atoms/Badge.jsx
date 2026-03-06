import { useTheme } from '../../context/ThemeContext';

const VARIANT_COLORS = {
  success: { bg: '#D1FAE5', color: '#065F46' },
  warning: { bg: '#FEF3C7', color: '#92400E' },
  error:   { bg: '#FEE2E2', color: '#991B1B' },
  info:    { bg: '#EFF6FF', color: '#1E40AF' },
  free:    { bg: '#ECFDF5', color: '#065F46' },
  active:  { bg: '#EEF2FF', color: '#3730A3' },
};

/**
 * Status, price or category label.
 * @param {{
 *   children: React.ReactNode,
 *   variant?: 'success'|'warning'|'error'|'info'|'free'|'active',
 *   size?: 'sm'|'md',
 * }} props
 */
export function Badge({ children, variant = 'info', size = 'md' }) {
  // useTheme is called to stay consistent with the pattern, theme may be used in future variants
  useTheme();
  const colors = VARIANT_COLORS[variant] ?? VARIANT_COLORS.info;

  return (
    <span
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        padding: size === 'sm' ? '2px 8px' : '4px 12px',
        borderRadius: 999,
        backgroundColor: colors.bg,
        color: colors.color,
        fontSize: size === 'sm' ? 11 : 13,
        fontWeight: 700,
        letterSpacing: '0.02em',
        whiteSpace: 'nowrap',
        userSelect: 'none',
      }}
    >
      {children}
    </span>
  );
}
