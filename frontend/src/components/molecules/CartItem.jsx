import { useTheme } from '../../context/ThemeContext';
import { copFmt, shortFmt } from '../../constants/catalog';

/**
 * Compact line item in the mini-cart sidebar.
 * @param {{
 *   module: object,
 *   tier: object,
 *   moduleColor: { bg: string, accent: string, text: string, icon: string, label: string },
 * }} props
 */
export function CartItem({ module, tier, moduleColor }) {
  const theme = useTheme();

  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        padding: '10px 14px',
        borderRadius: 14,
        backgroundColor: moduleColor.bg,
      }}
    >
      <span style={{ fontSize: 22, flexShrink: 0 }} aria-hidden="true">
        {moduleColor.icon}
      </span>

      <div style={{ flex: 1, minWidth: 0 }}>
        <div
          style={{
            fontWeight: 700,
            fontSize: 13,
            color: moduleColor.text,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          {moduleColor.label}
        </div>
        <div style={{ fontSize: 11, color: theme.brand.muted }}>
          {tier.label}
          {tier.sumAsegurada != null && ` · ${shortFmt(tier.sumAsegurada)}`}
        </div>
      </div>

      <div style={{ textAlign: 'right', flexShrink: 0 }}>
        <div style={{ fontWeight: 800, fontSize: 14, color: moduleColor.accent }}>
          {copFmt(tier.prima)}
        </div>
        <div style={{ fontSize: 11, color: theme.brand.muted }}>/mes</div>
      </div>
    </div>
  );
}
