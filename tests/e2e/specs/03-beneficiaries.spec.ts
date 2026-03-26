import { test, expect } from '@playwright/test';
import { loadApp, activateModule, fillBeneficiary } from '../helpers/setup';
import { MODULES, VALID_BENEFICIARY, BENEFICIARY_TWO } from '../fixtures/test-data';

// Helper: activate 'Vida / Fallecimiento' module and add the first beneficiary row
// (app starts with 0 rows; tests in this suite assume 1 row already exists)
async function activateDeath(page: Parameters<typeof loadApp>[0]) {
  await loadApp(page);
  await activateModule(page, MODULES.death);
  const article = page.locator(`article[aria-label="Módulo ${MODULES.death}"]`);
  await article.getByRole('button', { name: /Agregar beneficiario/ }).click();
  await expect(page.locator('input[id^="ben-name-death-"]').first()).toBeVisible({ timeout: 5_000 });
}

test.describe('Suite 3 — Beneficiarios', () => {

  test('3.1 — BeneficiaryManager aparece solo cuando hay tier seleccionado', async ({ page }) => {
    await loadApp(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.death}"]`);

    // Before activation: no beneficiary section
    await expect(article.getByText(/Beneficiarios/)).not.toBeVisible();

    // Expand without tier → still no beneficiary section
    await article.getByRole('button', { name: /Activar/ }).click();
    await expect(article.getByText(/Beneficiarios/)).not.toBeVisible();

    // Select a tier → beneficiary section appears
    await article.getByRole('radio').first().click();
    await expect(article.getByText(/Beneficiarios/)).toBeVisible();
  });

  test('3.2 — Agregar beneficiario agrega una nueva fila', async ({ page }) => {
    await activateDeath(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.death}"]`);
    const addBtn = article.getByRole('button', { name: /Agregar beneficiario/ });

    await addBtn.click();

    // Should now have 2 name inputs for this module
    const nameInputs = article.locator('input[id^="ben-name-death-"]');
    await expect(nameInputs).toHaveCount(2);
  });

  test('3.3 — Llenar beneficiario válido al 100% pone la barra en verde', async ({ page }) => {
    await activateDeath(page);

    await fillBeneficiary(page, 'death', VALID_BENEFICIARY);

    const progressBar = page.locator('[role="progressbar"]').first();
    await expect(progressBar).toHaveAttribute('aria-valuenow', '100');
  });

  test('3.4 — Nombre inválido muestra error de validación', async ({ page }) => {
    await activateDeath(page);

    const nameInput = page.locator('input[id^="ben-name-death-"]').first();
    await nameInput.fill('123');
    await nameInput.blur();

    await expect(page.getByText('Solo letras y espacios, entre 2 y 100 caracteres')).toBeVisible();
  });

  test('3.5 — Porcentaje inválido (>100) muestra error', async ({ page }) => {
    await activateDeath(page);

    const pctInput = page.locator('input[id^="ben-pct-death-"]').first();
    await pctInput.fill('150');
    await pctInput.blur();

    await expect(page.getByText('Entero entre 1 y 100')).toBeVisible();
  });

  test('3.6 — Dos beneficiarios 50/50 validan correctamente', async ({ page }) => {
    await activateDeath(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.death}"]`);
    await article.getByRole('button', { name: /Agregar beneficiario/ }).click();

    await fillBeneficiary(page, 'death', { ...VALID_BENEFICIARY, pct: '50' });

    // Fill second beneficiary
    const nameInputs = article.locator('input[id^="ben-name-death-"]');
    const relInputs  = article.locator('select[id^="ben-rel-death-"]');
    const pctInputs  = article.locator('input[id^="ben-pct-death-"]');

    await nameInputs.nth(1).fill(BENEFICIARY_TWO.name);
    await nameInputs.nth(1).blur();
    await relInputs.nth(1).selectOption(BENEFICIARY_TWO.relation);
    await pctInputs.nth(1).fill('50');
    await pctInputs.nth(1).blur();

    const progressBar = page.locator('[role="progressbar"]').first();
    await expect(progressBar).toHaveAttribute('aria-valuenow', '100');

    // BeneficiaryManager's sum-error paragraph should not be visible (totalPct=100)
    await expect(article.locator('p[role="alert"]')).not.toBeVisible();
  });

  test('3.7 — Botón eliminar: deshabilitado con 1 beneficiario, habilitado con 2', async ({ page }) => {
    await activateDeath(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.death}"]`);

    // With 1 beneficiary: remove button should be disabled
    const removeBtn = article.getByRole('button', { name: /Quitar beneficiario/ });
    await expect(removeBtn).toBeDisabled();

    // Add a second
    await article.getByRole('button', { name: /Agregar beneficiario/ }).click();

    // Both remove buttons should now be enabled
    const removeBtns = article.getByRole('button', { name: /Quitar beneficiario/ });
    await expect(removeBtns.first()).toBeEnabled();
    await expect(removeBtns.last()).toBeEnabled();

    // Click remove: should go back to 1 row
    await removeBtns.first().click();
    const nameInputs = article.locator('input[id^="ben-name-death-"]');
    await expect(nameInputs).toHaveCount(1);
  });

  test('3.8 — Con suma < 100% el CTA queda deshabilitado y muestra alerta', async ({ page }) => {
    await activateDeath(page);

    // Fill with only 50% (not 100%)
    await fillBeneficiary(page, 'death', { ...VALID_BENEFICIARY, pct: '50' });

    await expect(page.getByText('Completa los datos de beneficiarios (100%) antes de continuar.')).toBeVisible();
    // CTA aria-label is 'Continuar con la cotización' — disabled when canContinue=false
    await expect(page.getByRole('button', { name: 'Continuar con la cotización' })).toBeDisabled();
  });

});
