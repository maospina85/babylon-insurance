import { test, expect } from '@playwright/test';
import { loadApp, activateModule, fillBeneficiary, fillHolder, clickCTA, getPolicyNumber } from '../helpers/setup';
import { MODULES, ASSISTANCES } from '../fixtures/test-data';

test.describe('Suite 7 — Happy Path & Submission', () => {

  test('7.1 — Flujo completo: cotización creada con número de póliza BLF-', async ({ page }) => {
    await loadApp(page);

    // 1. Activate 'Accidentes' (no beneficiaries needed)
    await activateModule(page, MODULES.accidents);

    // 2. Select an assistance
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.medico_virtual) }).click();

    // 3. Fill holder form
    await fillHolder(page);

    // 4. Verify CTA is enabled
    await expect(page.getByRole('button', { name: 'Continuar →' })).toBeEnabled();

    // 5. Submit
    await clickCTA(page);

    // 6. Verify success screen
    const policyNumber = await getPolicyNumber(page);
    expect(policyNumber).toMatch(/BLF-[A-Z0-9]+/);
  });

  test('7.1b — Flujo con beneficiarios: módulo Vida + beneficiario 100%', async ({ page }) => {
    await loadApp(page);

    // 1. Activate 'Vida / Fallecimiento' (requires beneficiaries)
    await activateModule(page, MODULES.death);

    // 2. Fill beneficiary at 100%
    await fillBeneficiary(page, 'death');

    // 3. Select an assistance
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.juridico) }).click();

    // 4. Fill holder form
    await fillHolder(page);

    // 5. Submit
    await clickCTA(page);

    // 6. Verify success screen
    const policyNumber = await getPolicyNumber(page);
    expect(policyNumber).toMatch(/BLF-[A-Z0-9]+/);
  });

  test('7.2 — Sin completar el formulario, el CTA permanece deshabilitado', async ({ page }) => {
    await loadApp(page);

    // Activate module but don't fill anything else
    await activateModule(page, MODULES.accidents);

    // CTA should NOT say "Continuar →" (holder form is incomplete)
    await expect(page.getByRole('button', { name: 'Continuar →' })).not.toBeVisible();
    await expect(page.getByRole('button', { name: /Elige|Continuar/ }).first()).toBeDisabled();
  });

  test('7.3 — Pantalla de éxito tiene botón "Nueva cotización" que vuelve al inicio', async ({ page }) => {
    await loadApp(page);

    await activateModule(page, MODULES.accidents);
    await fillHolder(page);
    await clickCTA(page);

    await expect(page.getByText('¡Cotización creada!')).toBeVisible({ timeout: 15_000 });

    await page.getByRole('button', { name: 'Nueva cotización' }).click();

    // Should return to main form
    await expect(page.getByRole('heading', { name: '1. Elige tus coberturas' })).toBeVisible();
  });

});
