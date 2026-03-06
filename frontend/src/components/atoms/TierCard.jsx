import { useTheme } from '../../context/ThemeContext';
import { copFmt, shortFmt } from '../../constants/catalog';

/**
 * Selectable tier card showing coverage level, sum insured and monthly premium.
 * @param {{
 *   tier: object,
 *   selected: boolean,
 *   moduleColor: { bg: string, accent: string, text: string, ring: string },
 *   onSelect: () => void,
 * }} props
 */
export function TierCard({ tier, selected, moduleColor, onSelect, allCoverages = [] }) {
  const theme = useTheme();
  const coverages = tier.coverages ?? [];
  const visible = coverages.slice(0, 3);
  const extra = coverages.length - visible.length;

  return (
    <button
      role="radio"
      aria-pressed={selected}
      aria-label={`Plan ${tier.label} ${copFmt(tier.prima)} por mes`}
      onClick={onSelect}
      type="button"
      style={{
        padding: '14px 12px',
        borderRadius: theme.shape.tierRadius,
        border: `2px solid ${selected ? moduleColor.accent : '#E5E7EB'}`,
        backgroundColor: selected ? moduleColor.accent : '#FFFFFF',
        color: selected ? '#FFFFFF' : theme.brand.textPrimary,
        cursor: 'pointer',
        transition: theme.animation.card,
        transform: selected ? 'scale(1.02)' : 'scale(1)',
        boxShadow: selected ? theme.shadow.active(moduleColor.ring) : theme.shadow.card,
        textAlign: 'left',
        outline: 'none',
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
        gap: 6,
        minHeight: 120,
      }}
      onFocus={(e) => {
        e.currentTarget.style.outline = `2px solid ${moduleColor.ring}`;
        e.currentTarget.style.outlineOffset = '2px';
      }}
      onBlur={(e) => {
        e.currentTarget.style.outline = 'none';
      }}
    >
      <div style={{ fontWeight: 700, fontSize: 14 }}>{tier.label}</div>

      {tier.sumAsegurada != null && (
        <div style={{ fontSize: 13, opacity: selected ? 0.85 : 0.55, fontWeight: 600 }}>
          {shortFmt(tier.sumAsegurada)}
        </div>
      )}

      {visible.length > 0 && (
        <ul style={{ margin: 0, padding: 0, listStyle: 'none', flex: 1 }}>
          {visible.map((covId) => {
            const cov = allCoverages.find((c) => c.id === covId);
            return cov ? (
              <li
                key={covId}
                style={{
                  fontSize: 11,
                  opacity: selected ? 0.9 : 0.6,
                  marginBottom: 2,
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: 4,
                  lineHeight: 1.3,
                }}
              >
                <span style={{ flexShrink: 0 }}>✓</span>
                <span>{cov.label}</span>
              </li>
            ) : null;
          })}
          {extra > 0 && (
            <li style={{ fontSize: 11, opacity: 0.65, fontStyle: 'italic' }}>
              +{extra} más…
            </li>
          )}
        </ul>
      )}

      <div style={{ fontWeight: 800, fontSize: 16, marginTop: 'auto' }}>
        {copFmt(tier.prima)}
        <span style={{ fontWeight: 400, fontSize: 11, marginLeft: 2 }}>/mes</span>
      </div>
    </button>
  );
}
