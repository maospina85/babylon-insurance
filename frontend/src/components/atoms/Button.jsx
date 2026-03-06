import { useTheme } from '../../context/ThemeContext';

const SIZES = {
  sm: { padding: '8px 16px', fontSize: 13, minHeight: 36 },
  md: { padding: '12px 24px', fontSize: 15, minHeight: 44 },
  lg: { padding: '16px 32px', fontSize: 16, minHeight: 52 },
};

/**
 * Base button with variants and loading state.
 * @param {{
 *   children: React.ReactNode,
 *   variant?: 'primary'|'secondary'|'ghost'|'danger',
 *   size?: 'sm'|'md'|'lg',
 *   onClick?: () => void,
 *   disabled?: boolean,
 *   loading?: boolean,
 *   type?: 'button'|'submit'|'reset',
 *   ariaLabel?: string,
 *   fullWidth?: boolean,
 * }} props
 */
export function Button({
  children,
  variant = 'primary',
  size = 'md',
  onClick,
  disabled = false,
  loading = false,
  type = 'button',
  ariaLabel,
  fullWidth = false,
}) {
  const theme = useTheme();
  const sz = SIZES[size] ?? SIZES.md;
  const isDisabled = disabled || loading;

  const variantStyles = {
    primary: {
      background: theme.brand.gradient,
      color: '#FFFFFF',
      border: 'none',
      boxShadow: isDisabled ? 'none' : theme.shadow.button,
    },
    secondary: {
      background: 'transparent',
      color: theme.brand.primary,
      border: `2px solid ${theme.brand.primary}`,
      boxShadow: 'none',
    },
    ghost: {
      background: 'transparent',
      color: theme.brand.muted,
      border: '2px solid #E5E7EB',
      boxShadow: 'none',
    },
    danger: {
      background: theme.brand.error,
      color: '#FFFFFF',
      border: 'none',
      boxShadow: 'none',
    },
  };

  const v = variantStyles[variant] ?? variantStyles.primary;

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={isDisabled}
      aria-label={ariaLabel}
      aria-busy={loading}
      style={{
        ...sz,
        ...v,
        width: fullWidth ? '100%' : 'auto',
        borderRadius: theme.shape.buttonRadius,
        fontFamily: theme.typography.fontFamily,
        fontWeight: 700,
        cursor: isDisabled ? 'not-allowed' : 'pointer',
        opacity: isDisabled ? 0.6 : 1,
        transition: 'transform 0.15s ease, box-shadow 0.15s ease, opacity 0.15s ease',
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 8,
        outline: 'none',
        userSelect: 'none',
        letterSpacing: '0.01em',
      }}
      onMouseEnter={(e) => {
        if (!isDisabled) e.currentTarget.style.transform = 'translateY(-1px)';
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)';
      }}
      onFocus={(e) => {
        e.currentTarget.style.outline = `3px solid ${theme.brand.primary}44`;
        e.currentTarget.style.outlineOffset = '2px';
      }}
      onBlur={(e) => {
        e.currentTarget.style.outline = 'none';
      }}
    >
      {loading && (
        <span
          aria-hidden="true"
          style={{
            width: 16,
            height: 16,
            borderRadius: '50%',
            border: '2px solid transparent',
            borderTopColor: variant === 'primary' || variant === 'danger' ? '#FFFFFF' : theme.brand.primary,
            animation: 'spin 0.8s linear infinite',
            display: 'inline-block',
            flexShrink: 0,
          }}
        />
      )}
      {children}
    </button>
  );
}
