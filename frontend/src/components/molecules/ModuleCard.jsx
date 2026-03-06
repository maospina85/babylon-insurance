import { useMemo } from 'react';
import { useTheme } from '../../context/ThemeContext';
import { Toggle } from '../atoms/Toggle';
import { TierCard } from '../atoms/TierCard';
import { ProgressBar } from '../atoms/ProgressBar';
import { Button } from '../atoms/Button';
import { BeneficiaryRow } from './BeneficiaryRow';

const MAX_BENEFICIARIES = 10;

// ─── Internal: BeneficiaryManager ──────────────────────────────────────────

function BeneficiaryManager({
  moduleId,
  beneficiaryType,
  beneficiaries,
  onAdd,
  onChange,
  onRemove,
  totalPct,
  moduleColor,
}) {
  const theme = useTheme();
  const canAdd = totalPct < 100 && beneficiaries.length < MAX_BENEFICIARIES;

  return (
    <div
      style={{
        borderRadius: 16,
        backgroundColor: moduleColor.bg,
        padding: 16,
        marginTop: 8,
      }}
    >
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: 8,
        }}
      >
        <span style={{ fontWeight: 700, fontSize: 14, color: moduleColor.text }}>
          Beneficiarios — {beneficiaryType}
        </span>
        <span style={{ fontSize: 12, fontWeight: 700, color: moduleColor.accent }}>
          {totalPct}% / 100%
        </span>
      </div>

      <ProgressBar value={totalPct} max={100} showLabel={false} colorOverride={moduleColor.accent} />

      {beneficiaries.length === 0 && (
        <p
          style={{
            textAlign: 'center',
            color: theme.brand.muted,
            fontSize: 13,
            marginTop: 12,
            marginBottom: 4,
          }}
        >
          Agrega al menos un beneficiario para continuar.
        </p>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: 12 }}>
        {beneficiaries.map((b, idx) => (
          <BeneficiaryRow
            key={b.id}
            beneficiary={b}
            onChange={(field, value) => onChange(moduleId, b.id, field, value)}
            onRemove={() => onRemove(moduleId, b.id)}
            remainingPct={100 - totalPct + (Number(b.pct) || 0)}
            isOnlyOne={beneficiaries.length === 1}
            index={idx}
            moduleId={moduleId}
          />
        ))}
      </div>

      <div style={{ marginTop: 12 }}>
        <Button
          variant="secondary"
          size="sm"
          onClick={() => onAdd(moduleId, beneficiaryType)}
          disabled={!canAdd}
          ariaLabel="Agregar beneficiario"
        >
          + Agregar beneficiario
        </Button>
      </div>

      {beneficiaries.length > 0 && totalPct !== 100 && (
        <p
          role="alert"
          style={{ fontSize: 12, color: theme.brand.error, marginTop: 8, fontWeight: 600 }}
        >
          {totalPct < 100
            ? `Falta distribuir ${100 - totalPct}% — la suma debe ser exactamente 100%`
            : `La suma excede 100% en ${totalPct - 100}%`}
        </p>
      )}
    </div>
  );
}

// ─── Main: ModuleCard ───────────────────────────────────────────────────────

/**
 * Insurance module card with toggle, expandable tiers and optional beneficiary manager.
 * @param {{
 *   module: object,
 *   selectedTierId: string|null,
 *   isActive: boolean,
 *   onToggle: () => void,
 *   onTierSelect: (moduleId: string, tierId: string) => void,
 *   beneficiaries: Array,
 *   onBeneficiaryChange: (moduleId: string, beneficiaryId: string, field: string, value: any) => void,
 *   onAddBeneficiary: (moduleId: string, beneficiaryType: string) => void,
 *   onRemoveBeneficiary: (moduleId: string, beneficiaryId: string) => void,
 * }} props
 */
export function ModuleCard({
  module,
  selectedTierId,
  isActive,
  onToggle,
  onTierSelect,
  beneficiaries,
  onBeneficiaryChange,
  onAddBeneficiary,
  onRemoveBeneficiary,
}) {
  const theme = useTheme();
  const mc = theme.modules[module.id] ?? theme.modules.death;

  const selectedTier = useMemo(
    () => module.tiers.find((t) => t.id === selectedTierId) ?? null,
    [module.tiers, selectedTierId],
  );

  const totalBenPct = useMemo(
    () => beneficiaries.reduce((s, b) => s + (Number(b.pct) || 0), 0),
    [beneficiaries],
  );

  return (
    <article
      style={{
        borderRadius: theme.shape.moduleRadius,
        border: `2px solid ${isActive ? mc.accent : '#E5E7EB'}`,
        backgroundColor: theme.brand.surface,
        boxShadow: isActive ? theme.shadow.active(mc.ring) : theme.shadow.card,
        overflow: 'hidden',
        transition: 'border-color 0.25s ease, box-shadow 0.25s ease',
        marginBottom: 16,
      }}
      aria-label={`Módulo ${mc.label}`}
    >
      {/* ── Header ── */}
      <div
        role="button"
        tabIndex={0}
        aria-expanded={isActive}
        onClick={onToggle}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            onToggle();
          }
        }}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: 14,
          padding: '20px 24px',
          backgroundColor: isActive ? mc.bg : '#FAFAFA',
          cursor: 'pointer',
          userSelect: 'none',
          transition: 'background-color 0.2s ease',
        }}
      >
        <span style={{ fontSize: 30, flexShrink: 0 }} aria-hidden="true">
          {mc.icon}
        </span>

        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontWeight: 700, fontSize: 16, color: isActive ? mc.text : theme.brand.textPrimary }}>
            {mc.label}
          </div>
          {selectedTier && (
            <div style={{ fontSize: 13, color: mc.accent, marginTop: 2, fontWeight: 600 }}>
              {selectedTier.label}
              {selectedTier.sumAsegurada != null && ` · ${import('../../constants/catalog').then ? '' : ''}`}
            </div>
          )}
          {isActive && beneficiaries.length > 0 && (
            <div style={{ fontSize: 12, color: mc.accent, marginTop: 2 }}>
              {beneficiaries.length} beneficiario{beneficiaries.length > 1 ? 's' : ''}{' '}
              ({totalBenPct}%)
            </div>
          )}
        </div>

        {/* Toggle — stopPropagation so clicking it doesn't also fire the div's onClick */}
        <div onClick={(e) => e.stopPropagation()}>
          <Toggle
            checked={isActive}
            onChange={onToggle}
            label={`${isActive ? 'Desactivar' : 'Activar'} módulo ${mc.label}`}
            id={`toggle-${module.id}`}
          />
        </div>
      </div>

      {/* ── Body (CLS-safe expand: max-height + opacity) ── */}
      <div
        style={{
          overflow: 'hidden',
          maxHeight: isActive ? '3000px' : '0px',
          opacity: isActive ? 1 : 0,
          transition: theme.animation.expand,
        }}
      >
        <div style={{ padding: '0 24px 24px' }}>
          <p style={{ color: theme.brand.muted, fontSize: 14, marginTop: 16, marginBottom: 20 }}>
            {module.description}
          </p>

          {/* Coverage checklist (only for modules with named coverages e.g. death) */}
          {module.coverages?.length > 0 && (
            <div style={{ marginBottom: 20 }}>
              <div style={{ fontSize: 13, fontWeight: 700, color: theme.brand.textSecondary, marginBottom: 10 }}>
                Coberturas por plan:
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {module.coverages.map((cov) => {
                  const included = selectedTier?.coverages?.includes(cov.id) ?? false;
                  return (
                    <div
                      key={cov.id}
                      style={{
                        display: 'flex',
                        alignItems: 'flex-start',
                        gap: 10,
                        opacity: included ? 1 : 0.35,
                        transition: 'opacity 0.25s ease',
                      }}
                    >
                      <span
                        aria-hidden="true"
                        style={{
                          flexShrink: 0,
                          width: 22,
                          height: 22,
                          borderRadius: '50%',
                          backgroundColor: included ? mc.accent : '#D1D5DB',
                          color: '#FFFFFF',
                          fontSize: 12,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          fontWeight: 700,
                          marginTop: 1,
                          transition: 'background-color 0.25s ease',
                        }}
                      >
                        {included ? '✓' : '×'}
                      </span>
                      <div>
                        <div style={{ fontSize: 13, fontWeight: 600, color: theme.brand.textSecondary }}>
                          {cov.label}
                        </div>
                        <div style={{ fontSize: 12, color: theme.brand.muted, lineHeight: 1.4 }}>
                          {cov.description}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Tier grid */}
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(2, 1fr)',
              gap: 12,
              marginBottom: 16,
            }}
          >
            {module.tiers.map((tier) => (
              <TierCard
                key={tier.id}
                tier={tier}
                selected={selectedTierId === tier.id}
                moduleColor={mc}
                onSelect={() => onTierSelect(module.id, tier.id)}
                allCoverages={module.coverages}
              />
            ))}
          </div>

          {/* Beneficiary Manager */}
          {module.hasBeneficiaries && selectedTier && (
            <BeneficiaryManager
              moduleId={module.id}
              beneficiaryType={module.beneficiaryType}
              beneficiaries={beneficiaries}
              onAdd={onAddBeneficiary}
              onChange={onBeneficiaryChange}
              onRemove={onRemoveBeneficiary}
              totalPct={totalBenPct}
              moduleColor={mc}
            />
          )}
        </div>
      </div>
    </article>
  );
}
