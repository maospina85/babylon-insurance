import { useTheme } from '../../context/ThemeContext';

/**
 * Accessible toggle switch.
 * @param {{ checked: boolean, onChange: () => void, disabled?: boolean, label: string, id: string }} props
 */
export function Toggle({ checked, onChange, disabled = false, label, id }) {
  const theme = useTheme();

  return (
    <button
      id={id}
      role="switch"
      aria-checked={checked}
      aria-label={label}
      disabled={disabled}
      type="button"
      onClick={onChange}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        width: 44,
        height: 24,
        borderRadius: 999,
        backgroundColor: checked ? theme.brand.primary : '#D1D5DB',
        padding: 2,
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.5 : 1,
        transition: 'background-color 0.3s cubic-bezier(0.4,0,0.2,1)',
        border: 'none',
        outline: 'none',
        flexShrink: 0,
      }}
      onFocus={(e) => {
        e.currentTarget.style.boxShadow = `0 0 0 3px ${theme.brand.primary}44`;
      }}
      onBlur={(e) => {
        e.currentTarget.style.boxShadow = 'none';
      }}
    >
      <span
        style={{
          width: 20,
          height: 20,
          borderRadius: '50%',
          backgroundColor: '#FFFFFF',
          boxShadow: '0 1px 3px rgba(0,0,0,0.25)',
          transform: checked ? 'translateX(20px)' : 'translateX(0px)',
          transition: theme.animation.toggle,
          display: 'block',
          flexShrink: 0,
        }}
        aria-hidden="true"
      />
    </button>
  );
}
