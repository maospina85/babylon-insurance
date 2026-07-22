import { useState, useCallback, useMemo } from 'react';
import { useTheme } from './context/ThemeContext';
import { useProduct } from './hooks/useProduct';
import { useCoverage } from './hooks/useCoverage';
import { useBeneficiaries } from './hooks/useBeneficiaries';
import { useAssistances } from './hooks/useAssistances';
import { usePremium } from './hooks/usePremium';
import { createQuote } from './api/quoteApi';
import { CoverageSection } from './components/organisms/CoverageSection';
import { AssistanceSection } from './components/organisms/AssistanceSection';
import { HolderForm } from './components/organisms/HolderForm';
import { CartSummary } from './components/organisms/CartSummary';
import {
  validateHolderName,
  validateHolderEmail,
  validateHolderPhone,
  validateHolderDob,
} from './constants/validations';
import { copFmt } from './constants/catalog';

const INITIAL_HOLDER = { name: '', email: '', phone: '', dob: '' };
const INITIAL_TOUCHED = { name: false, email: false, phone: false, dob: false };

function useHolder() {
  const [holder, setHolder] = useState(INITIAL_HOLDER);
  const [touched, setTouched] = useState(INITIAL_TOUCHED);

  const onChange = useCallback((field, value) => {
    setHolder((prev) => ({ ...prev, [field]: value }));
  }, []);

  const onBlur = useCallback((field) => {
    setTouched((prev) => ({ ...prev, [field]: true }));
  }, []);

  const isValid =
    validateHolderName(holder.name).valid &&
    validateHolderEmail(holder.email).valid &&
    validateHolderPhone(holder.phone).valid &&
    validateHolderDob(holder.dob).valid;

  return { holder, touched, onChange, onBlur, isValid };
}

// ─── App ─────────────────────────────────────────────────────────────────────

export default function App() {
  const theme = useTheme();
  const { modules, loading: catalogLoading, error: catalogError } = useProduct();

  const {
    activeModules,
    toggleModule,
    selectTier,
    breakdown,
  } = useCoverage(modules);

  const {
    getBeneficiaries,
    addBeneficiary,
    removeBeneficiary,
    updateBeneficiary,
    isModuleValid,
  } = useBeneficiaries();

  const { isSelected, toggleAssistance, selectedList: selectedAssistances } = useAssistances(breakdown.length);
  const premium = usePremium(breakdown);
  const { holder, touched, onChange: onHolderChange, onBlur: onHolderBlur, isValid: isHolderValid } = useHolder();

  const [annual, setAnnual] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(null);
  const [quoteResult, setQuoteResult] = useState(null);
  const [discountCode, setDiscountCode] = useState('');

  const canContinue = useMemo(() => {
    if (premium.isEmpty) return false;
    const modulesNeedingBeneficiaries = modules.filter(
      (m) => m.hasBeneficiaries && m.id in activeModules && activeModules[m.id] !== null,
    );
    const allBeneficiariesValid = modulesNeedingBeneficiaries.every((m) =>
      isModuleValid(m.id),
    );
    return allBeneficiariesValid && isHolderValid;
  }, [premium.isEmpty, modules, activeModules, isModuleValid, isHolderValid]);

  const handleContinue = useCallback(async () => {
    if (!canContinue || submitting) return;
    setSubmitting(true);
    setSubmitError(null);
    try {
      const selectedCoverages = breakdown.map(({ module, tier }) => ({
        moduleId: module.id,
        tierId: tier.id,
        prima: tier.prima,
        sumAsegurada: tier.sumAsegurada ?? null,
      }));

      const beneficiaries = breakdown
        .filter(({ module }) => module.hasBeneficiaries)
        .flatMap(({ module }) =>
          getBeneficiaries(module.id).map((b) => ({
            name: b.name,
            relation: b.relation,
            pct: parseInt(b.pct, 10),
            moduleId: module.id,
            coverageType: b.type ?? module.beneficiaryType,
          })),
        );

      const result = await createQuote({
        holderName: holder.name,
        holderEmail: holder.email,
        holderPhone: holder.phone,
        holderDob: holder.dob,
        selectedCoverages,
        beneficiaries,
        assistances: selectedAssistances.map((a) => a.id),
        paymentFrequency: annual ? 'anual' : 'mensual',
        discountCode: discountCode.trim() || undefined,
      });

      setQuoteResult(result);
    } catch (err) {
      setSubmitError(err.message);
    } finally {
      setSubmitting(false);
    }
  }, [canContinue, submitting, breakdown, getBeneficiaries, holder, selectedAssistances, annual, discountCode]);

  // ── Loading / Error states ─────────────────────────────────────────────────

  if (catalogLoading) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: theme.brand.bg, fontFamily: theme.typography.fontFamily }}>
        <p style={{ color: theme.brand.muted, fontSize: 16 }}>Cargando catálogo…</p>
      </div>
    );
  }

  if (catalogError) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: theme.brand.bg, fontFamily: theme.typography.fontFamily }}>
        <p style={{ color: theme.brand.error, fontSize: 16 }}>Error: {catalogError}</p>
      </div>
    );
  }

  // ── Success state ──────────────────────────────────────────────────────────

  if (quoteResult) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: theme.brand.bg, fontFamily: theme.typography.fontFamily }}>
        <div style={{ textAlign: 'center', background: theme.brand.surface, padding: '48px 40px', borderRadius: theme.shape.cardRadius, boxShadow: theme.shadow.elevated, maxWidth: 480 }}>
          <div style={{ fontSize: 56, marginBottom: 16 }}>🎉</div>
          <h1 style={{ fontSize: 24, fontWeight: 900, color: theme.brand.textPrimary, marginBottom: 8 }}>¡Cotización creada!</h1>
          <p style={{ fontSize: 14, color: theme.brand.muted, marginBottom: 20 }}>Tu póliza ha sido registrada exitosamente.</p>
          <div style={{ background: theme.brand.gradientSoft, borderRadius: 16, padding: '16px 24px', marginBottom: 24 }}>
            <div style={{ fontSize: 12, color: theme.brand.muted, marginBottom: 4 }}>Número de póliza</div>
            <div style={{ fontSize: 22, fontWeight: 900, background: theme.brand.gradient, WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text' }}>
              {quoteResult.policyNumber}
            </div>
            <div style={{ fontSize: 12, color: theme.brand.muted, marginTop: 12, marginBottom: 4 }}>Prima {quoteResult.paymentFrequency}</div>
            <div style={{ fontSize: 18, fontWeight: 800, color: theme.brand.textPrimary }}>
              {copFmt(quoteResult.totalMonthlyPrima)}
            </div>
            {quoteResult.appliedDiscountCode && (
              <div style={{ fontSize: 12, color: theme.brand.success, fontWeight: 700, marginTop: 6 }}>
                Código aplicado: {quoteResult.appliedDiscountCode}
              </div>
            )}
          </div>
          <button
            onClick={() => { setQuoteResult(null); }}
            style={{ background: theme.brand.gradient, color: '#fff', border: 'none', borderRadius: theme.shape.buttonRadius, padding: '12px 32px', fontWeight: 700, fontSize: 15, cursor: 'pointer' }}
          >
            Nueva cotización
          </button>
        </div>
      </div>
    );
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        background: theme.brand.bg,
        fontFamily: theme.typography.fontFamily,
      }}
    >
      {/* ── Header ── */}
      <header
        style={{
          background: theme.brand.gradient,
          padding: '0 24px',
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          position: 'sticky',
          top: 0,
          zIndex: 100,
          boxShadow: '0 2px 12px rgba(43,91,245,0.25)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <span style={{ fontSize: 24 }} aria-hidden="true">
            🛡️
          </span>
          <span
            style={{
              fontSize: 20,
              fontWeight: 900,
              color: '#FFFFFF',
              letterSpacing: '-0.02em',
            }}
          >
            {theme.brand.name}
          </span>
          <span
            style={{
              fontSize: 11,
              fontWeight: 600,
              color: 'rgba(255,255,255,0.7)',
              marginLeft: 2,
            }}
          >
            {theme.brand.tagline}
          </span>
        </div>

        {!premium.isEmpty && (
          <div
            aria-live="polite"
            style={{
              fontSize: 13,
              fontWeight: 700,
              color: 'rgba(255,255,255,0.9)',
              background: 'rgba(255,255,255,0.15)',
              padding: '6px 14px',
              borderRadius: '999px',
            }}
          >
            {breakdown.length} cobertura{breakdown.length > 1 ? 's' : ''}
          </div>
        )}
      </header>

      {/* ── Hero ── */}
      <div
        style={{
          background: theme.brand.gradientSoft,
          padding: '48px 24px 40px',
          textAlign: 'center',
          borderBottom: '1px solid #E5E7EB',
        }}
      >
        <h1
          style={{
            fontSize: 'clamp(26px, 4vw, 40px)',
            fontWeight: 900,
            color: theme.brand.textPrimary,
            lineHeight: 1.2,
            letterSpacing: '-0.02em',
            marginBottom: 12,
          }}
        >
          Diseña tu seguro a medida
        </h1>
        <p
          style={{
            fontSize: 16,
            color: theme.brand.muted,
            maxWidth: 520,
            margin: '0 auto',
            lineHeight: 1.6,
          }}
        >
          Elige solo las coberturas que necesitas, sin paquetes fijos. Tu protección, a tu precio.
        </p>
      </div>

      {/* ── Main layout ── */}
      <main
        style={{
          maxWidth: 1200,
          margin: '0 auto',
          padding: '40px 24px 80px',
          display: 'grid',
          gridTemplateColumns: 'minmax(0, 1fr) 360px',
          gap: 40,
          alignItems: 'start',
        }}
      >
        {/* Left column */}
        <div>
          <CoverageSection
            modules={modules}
            activeModules={activeModules}
            onToggleModule={toggleModule}
            onSelectTier={selectTier}
            getBeneficiaries={getBeneficiaries}
            onBeneficiaryChange={updateBeneficiary}
            onAddBeneficiary={addBeneficiary}
            onRemoveBeneficiary={removeBeneficiary}
          />

          <AssistanceSection
            isSelected={isSelected}
            onToggle={toggleAssistance}
            maxSelectable={breakdown.length}
            selectedCount={selectedAssistances.length}
          />

          <HolderForm
            holder={holder}
            onChange={onHolderChange}
            touched={touched}
            onBlur={onHolderBlur}
          />

          {submitError && (
            <p role="alert" style={{ color: theme.brand.error, fontSize: 13, fontWeight: 600, marginTop: 16 }}>
              {submitError}
            </p>
          )}
        </div>

        {/* Right column — sticky cart */}
        <CartSummary
          breakdown={breakdown}
          premium={premium}
          selectedAssistances={selectedAssistances}
          canContinue={canContinue}
          onContinue={handleContinue}
          annual={annual}
          onAnnualChange={setAnnual}
          submitting={submitting}
          discountCode={discountCode}
          onDiscountCodeChange={setDiscountCode}
        />
      </main>

      {/* ── Footer ── */}
      <footer
        style={{
          borderTop: '1px solid #E5E7EB',
          padding: '20px 24px',
          textAlign: 'center',
          fontSize: 12,
          color: theme.brand.muted,
        }}
      >
        © {new Date().getFullYear()} {theme.brand.name}. Todos los derechos reservados. · Vigilado por la Superintendencia Financiera de Colombia.
      </footer>
    </div>
  );
}
