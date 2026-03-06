import { useTheme } from '../../context/ThemeContext';
import { AssistanceCard } from '../molecules/AssistanceCard';
import { ASSISTANCES } from '../../constants/catalog';

/**
 * Grid of selectable assistance services (free add-ons).
 * @param {{
 *   isSelected: (id: string) => boolean,
 *   onToggle: (id: string) => void,
 * }} props
 */
export function AssistanceSection({ isSelected, onToggle, maxSelectable, selectedCount }) {
  const theme = useTheme();
  const limitReached = selectedCount >= maxSelectable;

  return (
    <section aria-labelledby="assistance-heading" style={{ marginTop: 40 }}>
      <div style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
          <h2
            id="assistance-heading"
            style={{ fontSize: 22, fontWeight: 900, color: theme.brand.textPrimary }}
          >
            2. Asistencias incluidas
          </h2>
          <span
            style={{
              fontSize: 11,
              fontWeight: 800,
              padding: '3px 10px',
              borderRadius: '999px',
              backgroundColor: '#D1FAE5',
              color: '#065F46',
              letterSpacing: '0.04em',
              flexShrink: 0,
            }}
          >
            SIN COSTO
          </span>
        </div>
        <p style={{ fontSize: 14, color: theme.brand.muted, lineHeight: 1.6 }}>
          {maxSelectable === 0
            ? 'Selecciona al menos una cobertura para activar asistencias.'
            : `Puedes elegir hasta ${maxSelectable} asistencia${maxSelectable > 1 ? 's' : ''} según tus coberturas activas. Seleccionadas: ${selectedCount} de ${maxSelectable}.`}
        </p>
      </div>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
          gap: 14,
        }}
        role="group"
        aria-label="Servicios de asistencia disponibles"
      >
        {ASSISTANCES.map((assistance) => {
          const sel = isSelected(assistance.id);
          const disabled = maxSelectable === 0 || (!sel && limitReached);
          return (
            <AssistanceCard
              key={assistance.id}
              assistance={assistance}
              selected={sel}
              disabled={disabled}
              onToggle={() => onToggle(assistance.id)}
            />
          );
        })}
      </div>
    </section>
  );
}
