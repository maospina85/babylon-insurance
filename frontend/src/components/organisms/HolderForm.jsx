import { useTheme } from '../../context/ThemeContext';
import {
  validateHolderName,
  validateHolderEmail,
  validateHolderPhone,
  validateHolderDob,
} from '../../constants/validations';

const FIELDS = [
  {
    id: 'name',
    label: 'Nombre completo',
    type: 'text',
    placeholder: 'Ej: María García López',
    autoComplete: 'name',
    validate: validateHolderName,
  },
  {
    id: 'email',
    label: 'Correo electrónico',
    type: 'email',
    placeholder: 'correo@ejemplo.com',
    autoComplete: 'email',
    validate: validateHolderEmail,
  },
  {
    id: 'phone',
    label: 'Teléfono',
    type: 'tel',
    placeholder: '+57 300 000 0000',
    autoComplete: 'tel',
    validate: validateHolderPhone,
  },
  {
    id: 'dob',
    label: 'Fecha de nacimiento',
    type: 'date',
    placeholder: '',
    autoComplete: 'bday',
    validate: validateHolderDob,
  },
];

/**
 * Controlled form for policy holder personal data.
 * @param {{
 *   holder: { name: string, email: string, phone: string, dob: string },
 *   onChange: (field: string, value: string) => void,
 *   touched: { name: boolean, email: boolean, phone: boolean, dob: boolean },
 *   onBlur: (field: string) => void,
 * }} props
 */
export function HolderForm({ holder, onChange, touched, onBlur }) {
  const theme = useTheme();

  const inputBase = {
    width: '100%',
    padding: '12px 14px',
    borderRadius: theme.shape.inputRadius,
    fontSize: 14,
    fontFamily: theme.typography.fontFamily,
    color: theme.brand.textPrimary,
    backgroundColor: '#FFFFFF',
    boxSizing: 'border-box',
    outline: 'none',
    transition: 'border-color 0.15s ease',
  };

  const labelStyle = {
    display: 'block',
    fontSize: 13,
    fontWeight: 700,
    color: theme.brand.textSecondary,
    marginBottom: 6,
  };

  const errorStyle = {
    display: 'block',
    fontSize: 12,
    color: theme.brand.error,
    marginTop: 4,
    minHeight: 18,
    fontWeight: 600,
  };

  return (
    <section aria-labelledby="holder-heading" style={{ marginTop: 40 }}>
      <div style={{ marginBottom: 24 }}>
        <h2
          id="holder-heading"
          style={{
            fontSize: 22,
            fontWeight: 900,
            color: theme.brand.textPrimary,
            marginBottom: 6,
          }}
        >
          3. Datos del titular
        </h2>
        <p style={{ fontSize: 14, color: theme.brand.muted, lineHeight: 1.6 }}>
          Información personal del asegurado principal. Debe ser mayor de 18 y menor de 70 años.
        </p>
      </div>

      <div
        style={{
          backgroundColor: theme.brand.surface,
          borderRadius: theme.shape.cardRadius,
          padding: 28,
          boxShadow: theme.shadow.card,
        }}
      >
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
            gap: '20px 24px',
          }}
        >
          {FIELDS.map((field) => {
            const raw = holder[field.id] ?? '';
            const result = field.validate(raw);
            const isDirty = touched[field.id];
            const hasError = isDirty && raw !== '' && !result.valid;
            const borderColor = hasError
              ? theme.brand.error
              : isDirty && raw !== '' && result.valid
              ? theme.brand.success
              : '#D1D5DB';

            return (
              <div key={field.id}>
                <label htmlFor={`holder-${field.id}`} style={labelStyle}>
                  {field.label}
                  <span aria-hidden="true" style={{ color: theme.brand.error, marginLeft: 2 }}>
                    *
                  </span>
                </label>
                <input
                  id={`holder-${field.id}`}
                  type={field.type}
                  value={raw}
                  placeholder={field.placeholder}
                  autoComplete={field.autoComplete}
                  aria-required="true"
                  aria-invalid={hasError}
                  aria-describedby={hasError ? `holder-${field.id}-err` : undefined}
                  onChange={(e) => onChange(field.id, e.target.value)}
                  onBlur={() => onBlur(field.id)}
                  onFocus={(e) => {
                    e.currentTarget.style.borderColor = theme.brand.primary;
                  }}
                  onBlurCapture={(e) => {
                    e.currentTarget.style.borderColor = hasError
                      ? theme.brand.error
                      : '#D1D5DB';
                  }}
                  style={{
                    ...inputBase,
                    border: `1.5px solid ${borderColor}`,
                  }}
                />
                <span
                  id={`holder-${field.id}-err`}
                  role={hasError ? 'alert' : undefined}
                  style={errorStyle}
                >
                  {hasError ? result.message : ''}
                </span>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
