import { useTheme } from '../../context/ThemeContext';
import { ModuleCard } from '../molecules/ModuleCard';

/**
 * Renders the full list of insurable modules.
 * @param {{
 *   modules: Array,
 *   activeModules: Object<string, string|null>,
 *   onToggleModule: (moduleId: string) => void,
 *   onSelectTier: (moduleId: string, tierId: string) => void,
 *   getBeneficiaries: (moduleId: string) => Array,
 *   onBeneficiaryChange: (moduleId: string, beneficiaryId: string, field: string, value: any) => void,
 *   onAddBeneficiary: (moduleId: string, beneficiaryType: string) => void,
 *   onRemoveBeneficiary: (moduleId: string, beneficiaryId: string) => void,
 * }} props
 */
export function CoverageSection({
  modules,
  activeModules,
  onToggleModule,
  onSelectTier,
  getBeneficiaries,
  onBeneficiaryChange,
  onAddBeneficiary,
  onRemoveBeneficiary,
}) {
  const theme = useTheme();
  const activeCount = Object.keys(activeModules).length;

  return (
    <section aria-labelledby="coverage-heading">
      <div style={{ marginBottom: 24 }}>
        <h2
          id="coverage-heading"
          style={{
            fontSize: 22,
            fontWeight: 900,
            color: theme.brand.textPrimary,
            marginBottom: 6,
          }}
        >
          1. Elige tus coberturas
        </h2>
        <p style={{ fontSize: 14, color: theme.brand.muted, lineHeight: 1.6 }}>
          Activa los módulos que necesitas y selecciona el nivel de protección de cada uno.
          {activeCount > 0 && (
            <span
              style={{
                marginLeft: 8,
                fontWeight: 700,
                color: theme.brand.primary,
              }}
            >
              {activeCount} activo{activeCount > 1 ? 's' : ''}
            </span>
          )}
        </p>
      </div>

      <div role="list" aria-label="Módulos de seguro disponibles">
        {modules.map((module) => (
          <div key={module.id} role="listitem">
            <ModuleCard
              module={module}
              selectedTierId={activeModules[module.id] ?? null}
              isActive={module.id in activeModules}
              onToggle={() => onToggleModule(module.id)}
              onTierSelect={onSelectTier}
              beneficiaries={getBeneficiaries(module.id)}
              onBeneficiaryChange={onBeneficiaryChange}
              onAddBeneficiary={onAddBeneficiary}
              onRemoveBeneficiary={onRemoveBeneficiary}
            />
          </div>
        ))}
      </div>
    </section>
  );
}
