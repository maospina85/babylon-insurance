import { useTheme } from '../../context/ThemeContext';
import { Badge } from '../atoms/Badge';

/**
 * Selectable assistance service card.
 * @param {{
 *   assistance: { id: string, label: string, icon: string, color: string, desc: string },
 *   selected: boolean,
 *   onToggle: () => void,
 * }} props
 */
export function AssistanceCard({ assistance, selected, disabled, onToggle }) {
  useTheme(); // required pattern — theme context must be active

  return (
    <button
      type="button"
      role="checkbox"
      aria-pressed={selected}
      aria-disabled={disabled}
      aria-label={`${selected ? 'Quitar' : 'Agregar'} asistencia ${assistance.label}`}
      onClick={disabled ? undefined : onToggle}
      style={{
        padding: 16,
        borderRadius: 20,
        border: `2px solid ${selected ? assistance.color : '#E5E7EB'}`,
        backgroundColor: selected ? assistance.color : disabled ? '#F9FAFB' : '#FFFFFF',
        color: selected ? '#FFFFFF' : disabled ? '#9CA3AF' : '#111827',
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.5 : 1,
        transition:
          'background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease, transform 0.15s ease',
        transform: selected ? 'scale(1.02)' : 'scale(1)',
        outline: 'none',
        textAlign: 'left',
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
        gap: 8,
        minHeight: 140,
      }}
      onFocus={(e) => {
        e.currentTarget.style.outline = `3px solid ${assistance.color}55`;
        e.currentTarget.style.outlineOffset = '2px';
      }}
      onBlur={(e) => {
        e.currentTarget.style.outline = 'none';
      }}
    >
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 8 }}>
        <span style={{ fontSize: 26 }} aria-hidden="true">
          {assistance.icon}
        </span>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          {selected && (
            <span
              aria-hidden="true"
              style={{
                width: 20,
                height: 20,
                borderRadius: '50%',
                backgroundColor: 'rgba(255,255,255,0.3)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: 13,
                fontWeight: 700,
                flexShrink: 0,
              }}
            >
              ✓
            </span>
          )}
          <Badge variant="free" size="sm">
            GRATIS
          </Badge>
        </div>
      </div>

      <div style={{ fontWeight: 700, fontSize: 14, lineHeight: 1.3 }}>{assistance.label}</div>

      <div
        style={{
          fontSize: 12,
          opacity: selected ? 0.88 : 0.62,
          lineHeight: 1.5,
          flex: 1,
        }}
      >
        {assistance.desc}
      </div>
    </button>
  );
}
