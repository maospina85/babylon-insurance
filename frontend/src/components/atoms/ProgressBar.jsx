import { useTheme } from '../../context/ThemeContext';

/**
 * Visual progress bar. Fixed dimensions to prevent CLS.
 * @param {{
 *   value: number,
 *   max?: number,
 *   showLabel?: boolean,
 *   colorOverride?: string,
 * }} props
 */
export function ProgressBar({ value, max = 100, showLabel = true, colorOverride }) {
  const theme = useTheme();
  const pct = Math.min(Math.max((value / max) * 100, 0), 100);
  const color =
    colorOverride ??
    (value === max
      ? theme.brand.success
      : value > max
      ? theme.brand.error
      : theme.brand.warning);

  return (
    <div
      style={{ display: 'flex', alignItems: 'center', gap: 8, width: '100%', height: 20 }}
      role="progressbar"
      aria-valuenow={value}
      aria-valuemin={0}
      aria-valuemax={max}
      aria-label={`${value} de ${max}`}
    >
      <div
        style={{
          flex: 1,
          height: 8,
          borderRadius: 999,
          backgroundColor: '#E5E7EB',
          overflow: 'hidden',
          flexShrink: 0,
        }}
      >
        <div
          style={{
            height: '100%',
            width: `${pct}%`,
            backgroundColor: color,
            borderRadius: 999,
            transition: 'width 0.3s ease',
          }}
        />
      </div>
      {showLabel && (
        <span
          style={{
            fontSize: 12,
            fontWeight: 700,
            color,
            minWidth: 36,
            textAlign: 'right',
            flexShrink: 0,
          }}
        >
          {value}%
        </span>
      )}
    </div>
  );
}
