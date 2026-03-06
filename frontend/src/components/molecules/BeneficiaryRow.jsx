import { useTheme } from '../../context/ThemeContext';
import { RELATIONS } from '../../constants/catalog';
import { validateBeneficiaryName } from '../../constants/validations';

/**
 * Single beneficiary row: name, relation, percentage, remove button.
 * @param {{
 *   beneficiary: { id: string, name: string, relation: string, pct: string|number },
 *   onChange: (field: string, value: any) => void,
 *   onRemove: () => void,
 *   remainingPct: number,
 *   isOnlyOne: boolean,
 *   index: number,
 *   moduleId: string,
 * }} props
 */
export function BeneficiaryRow({ beneficiary, onChange, onRemove, remainingPct, isOnlyOne, index, moduleId }) {
  const theme = useTheme();

  const nameId = `ben-name-${moduleId}-${beneficiary.id}`;
  const relId  = `ben-rel-${moduleId}-${beneficiary.id}`;
  const pctId  = `ben-pct-${moduleId}-${beneficiary.id}`;

  const nameResult = validateBeneficiaryName(beneficiary.name);
  const showNameError = beneficiary.name !== '' && !nameResult.valid;

  const pctNum = Number(beneficiary.pct);
  const pctError =
    beneficiary.pct !== '' &&
    (isNaN(pctNum) || !Number.isInteger(pctNum) || pctNum < 1 || pctNum > 100)
      ? 'Entero entre 1 y 100'
      : null;

  const inputStyle = (hasError) => ({
    width: '100%',
    padding: '8px 10px',
    borderRadius: theme.shape.inputRadius,
    border: `1.5px solid ${hasError ? theme.brand.error : '#D1D5DB'}`,
    fontSize: 13,
    fontFamily: theme.typography.fontFamily,
    color: theme.brand.textPrimary,
    backgroundColor: '#FFFFFF',
    outline: 'none',
    boxSizing: 'border-box',
    transition: 'border-color 0.15s ease',
  });

  const labelStyle = {
    fontSize: 11,
    fontWeight: 600,
    color: theme.brand.textSecondary,
    display: 'block',
    marginBottom: 3,
  };

  const errorStyle = {
    fontSize: 11,
    color: theme.brand.error,
    marginTop: 2,
    display: 'block',
    minHeight: 14,
  };

  return (
    <div
      style={{
        backgroundColor: '#FFFFFF',
        borderRadius: 12,
        padding: 12,
        display: 'grid',
        gridTemplateColumns: '1fr 130px 64px 36px',
        gap: 8,
        alignItems: 'start',
      }}
    >
      {/* Name */}
      <div>
        <label htmlFor={nameId} style={labelStyle}>
          Nombre
        </label>
        <input
          id={nameId}
          type="text"
          value={beneficiary.name}
          onChange={(e) => onChange('name', e.target.value)}
          placeholder="Nombre completo"
          style={inputStyle(showNameError)}
          aria-invalid={showNameError}
          aria-describedby={showNameError ? `${nameId}-err` : undefined}
          autoComplete="off"
        />
        <span id={`${nameId}-err`} style={errorStyle} role="alert">
          {showNameError ? nameResult.message : ''}
        </span>
      </div>

      {/* Relation */}
      <div>
        <label htmlFor={relId} style={labelStyle}>
          Parentesco
        </label>
        <select
          id={relId}
          value={beneficiary.relation}
          onChange={(e) => onChange('relation', e.target.value)}
          style={{ ...inputStyle(false), cursor: 'pointer' }}
        >
          {RELATIONS.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <span style={errorStyle} />
      </div>

      {/* Percentage */}
      <div>
        <label htmlFor={pctId} style={labelStyle}>
          %
        </label>
        <input
          id={pctId}
          type="number"
          min={1}
          max={remainingPct + (Number(beneficiary.pct) || 0)}
          step={1}
          value={beneficiary.pct}
          onChange={(e) => onChange('pct', e.target.value)}
          style={inputStyle(!!pctError)}
          aria-invalid={!!pctError}
          aria-describedby={pctError ? `${pctId}-err` : undefined}
        />
        <span id={`${pctId}-err`} style={errorStyle} role="alert">
          {pctError ?? ''}
        </span>
      </div>

      {/* Remove */}
      <div style={{ paddingTop: 18 }}>
        <button
          type="button"
          aria-label={`Quitar beneficiario ${beneficiary.name || index + 1}`}
          disabled={isOnlyOne}
          onClick={onRemove}
          style={{
            width: 36,
            height: 36,
            borderRadius: '50%',
            border: 'none',
            backgroundColor: isOnlyOne ? '#F3F4F6' : '#FEE2E2',
            color: isOnlyOne ? '#9CA3AF' : theme.brand.error,
            cursor: isOnlyOne ? 'not-allowed' : 'pointer',
            fontSize: 18,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexShrink: 0,
            transition: 'background-color 0.15s ease',
            outline: 'none',
          }}
          onFocus={(e) => {
            if (!isOnlyOne) e.currentTarget.style.outline = `2px solid ${theme.brand.error}44`;
          }}
          onBlur={(e) => {
            e.currentTarget.style.outline = 'none';
          }}
        >
          ×
        </button>
      </div>
    </div>
  );
}
