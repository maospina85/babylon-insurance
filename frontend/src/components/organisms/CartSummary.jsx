// annual state lifted to App — no local useState needed
import { useTheme } from '../../context/ThemeContext';
import { CartItem } from '../molecules/CartItem';
import { Button } from '../atoms/Button';
import { copFmt } from '../../constants/catalog';

/**
 * Sticky sidebar showing selected modules, pricing, and CTA.
 * @param {{
 *   breakdown: Array<{ module: object, tier: object }>,
 *   premium: object,
 *   selectedAssistances: Array<object>,
 *   canContinue: boolean,
 *   onContinue: () => void,
 *   annual: boolean,
 *   onAnnualChange: (value: boolean) => void,
 *   submitting: boolean,
 *   discountCode: string,
 *   onDiscountCodeChange: (value: string) => void,
 * }} props
 */
export function CartSummary({ breakdown, premium, selectedAssistances, canContinue, onContinue, annual, onAnnualChange, submitting, discountCode, onDiscountCodeChange }) {
  const theme = useTheme();

  const displayPrice = annual ? premium.totalAnnualDiscounted : premium.totalMonthly;
  const periodLabel = annual ? '/año' : '/mes';

  return (
    <aside
      aria-label="Resumen de tu seguro"
      style={{
        position: 'sticky',
        top: 24,
        backgroundColor: theme.brand.surface,
        borderRadius: theme.shape.cardRadius,
        padding: 24,
        boxShadow: theme.shadow.elevated,
        display: 'flex',
        flexDirection: 'column',
        gap: 20,
      }}
    >
      {/* Header */}
      <div>
        <h2
          style={{
            fontSize: 18,
            fontWeight: 900,
            color: theme.brand.textPrimary,
            marginBottom: 4,
          }}
        >
          Tu seguro
        </h2>
        <p style={{ fontSize: 13, color: theme.brand.muted }}>
          {premium.isEmpty
            ? 'Selecciona al menos una cobertura'
            : `${breakdown.length} cobertura${breakdown.length > 1 ? 's' : ''} activa${breakdown.length > 1 ? 's' : ''}`}
        </p>
      </div>

      {/* Empty state */}
      {premium.isEmpty && (
        <div
          style={{
            textAlign: 'center',
            padding: '28px 16px',
            borderRadius: 20,
            background: theme.brand.gradientSoft,
          }}
        >
          <div style={{ fontSize: 38, marginBottom: 10 }}>🛡️</div>
          <p style={{ fontSize: 13, color: theme.brand.muted, lineHeight: 1.6 }}>
            Activa un módulo y elige un plan para ver el resumen de precio.
          </p>
        </div>
      )}

      {/* Module breakdown */}
      {!premium.isEmpty && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          {breakdown.map(({ module, tier }) => {
            const mc = theme.modules[module.id] ?? theme.modules.death;
            return (
              <CartItem key={module.id} module={module} tier={tier} moduleColor={mc} />
            );
          })}
        </div>
      )}

      {/* Assistances chips */}
      {selectedAssistances.length > 0 && (
        <div>
          <div
            style={{
              fontSize: 12,
              fontWeight: 700,
              color: theme.brand.textSecondary,
              marginBottom: 8,
            }}
          >
            Asistencias incluidas ({selectedAssistances.length})
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
            {selectedAssistances.map((a) => (
              <span
                key={a.id}
                title={a.label}
                style={{
                  fontSize: 11,
                  padding: '3px 10px',
                  borderRadius: theme.shape.chipRadius,
                  backgroundColor: `${a.color}18`,
                  color: a.color,
                  fontWeight: 700,
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 4,
                }}
              >
                {a.icon} {a.label}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Billing period toggle */}
      {!premium.isEmpty && (
        <div
          role="group"
          aria-label="Periodicidad de pago"
          style={{
            display: 'flex',
            borderRadius: theme.shape.buttonRadius,
            overflow: 'hidden',
            border: '1.5px solid #E5E7EB',
          }}
        >
          {[
            { label: 'Mensual', value: false },
            { label: 'Anual −10%', value: true },
          ].map(({ label, value }) => {
            const active = annual === value;
            return (
              <button
                key={String(value)}
                type="button"
                aria-pressed={active}
                onClick={() => onAnnualChange(value)}
                style={{
                  flex: 1,
                  padding: '9px 0',
                  fontSize: 12,
                  fontWeight: 700,
                  border: 'none',
                  cursor: 'pointer',
                  backgroundColor: active ? theme.brand.primary : 'transparent',
                  color: active ? '#FFFFFF' : theme.brand.muted,
                  fontFamily: theme.typography.fontFamily,
                  transition: 'background-color 0.2s ease, color 0.2s ease',
                  outline: 'none',
                }}
                onFocus={(e) => {
                  e.currentTarget.style.outline = `2px solid ${theme.brand.primary}55`;
                }}
                onBlur={(e) => {
                  e.currentTarget.style.outline = 'none';
                }}
              >
                {label}
              </button>
            );
          })}
        </div>
      )}

      {/* Price display */}
      {!premium.isEmpty && (
        <div style={{ textAlign: 'center' }}>
          <div
            style={{
              fontSize: 38,
              fontWeight: 900,
              background: theme.brand.gradient,
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              lineHeight: 1.1,
              letterSpacing: '-0.02em',
            }}
          >
            {copFmt(displayPrice)}
          </div>
          <div style={{ fontSize: 13, color: theme.brand.muted, marginTop: 4 }}>
            {periodLabel}
            {annual && premium.annualSavings > 0 && (
              <span style={{ marginLeft: 8, color: theme.brand.success, fontWeight: 700 }}>
                · ahorras {copFmt(premium.annualSavings)}
              </span>
            )}
          </div>
          {premium.totalSumAsegurada > 0 && (
            <div style={{ fontSize: 12, color: theme.brand.muted, marginTop: 8 }}>
              Suma asegurada:{' '}
              <strong style={{ color: theme.brand.textSecondary }}>
                {copFmt(premium.totalSumAsegurada)}
              </strong>
            </div>
          )}
        </div>
      )}

      {/* Discount code */}
      {!premium.isEmpty && (
        <div>
          <label
            htmlFor="discount-code"
            style={{
              fontSize: 12,
              fontWeight: 700,
              color: theme.brand.textSecondary,
              marginBottom: 6,
              display: 'block',
            }}
          >
            Código de descuento
          </label>
          <input
            id="discount-code"
            type="text"
            value={discountCode}
            onChange={(e) => onDiscountCodeChange(e.target.value)}
            placeholder="Opcional"
            style={{
              width: '100%',
              padding: '10px 12px',
              borderRadius: theme.shape.buttonRadius,
              border: '1.5px solid #E5E7EB',
              fontSize: 13,
              fontFamily: theme.typography.fontFamily,
              boxSizing: 'border-box',
              color: theme.brand.textPrimary,
            }}
          />
        </div>
      )}

      {/* CTA */}
      <Button
        variant="primary"
        size="lg"
        onClick={onContinue}
        disabled={!canContinue}
        ariaLabel={premium.isEmpty ? 'Elige una cobertura para continuar' : 'Continuar con la cotización'}
        fullWidth
      >
        {submitting ? 'Enviando…' : premium.isEmpty ? 'Elige una cobertura' : 'Continuar →'}
      </Button>

      {/* Inline warning when blocked */}
      {!canContinue && !premium.isEmpty && (
        <p
          role="alert"
          style={{
            fontSize: 12,
            color: theme.brand.warning,
            textAlign: 'center',
            fontWeight: 600,
            lineHeight: 1.5,
          }}
        >
          Completa los datos de beneficiarios (100%) antes de continuar.
        </p>
      )}

      {/* Trust badges */}
      <div
        aria-hidden="true"
        style={{
          display: 'flex',
          justifyContent: 'center',
          gap: 16,
          paddingTop: 4,
          borderTop: '1px solid #F3F4F6',
        }}
      >
        {[
          { icon: '🔒', text: 'Seguro SSL' },
          { icon: '✓', text: 'Sin compromiso' },
        ].map(({ icon, text }) => (
          <div
            key={text}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: 4,
              fontSize: 11,
              color: theme.brand.muted,
            }}
          >
            <span>{icon}</span>
            <span>{text}</span>
          </div>
        ))}
      </div>
    </aside>
  );
}
