import { Page, expect } from '@playwright/test';
import { VALID_HOLDER, VALID_BENEFICIARY } from '../fixtures/test-data';

/** Navigate to app and wait for catalog to load */
export async function loadApp(page: Page) {
  await page.goto('/');
  // Wait until loading spinner is gone and at least one module card is visible
  await expect(page.getByText('Cargando catálogo…')).not.toBeVisible({ timeout: 15_000 });
  await expect(page.getByText('1. Elige tus coberturas')).toBeVisible();
}

/** Click the module header to expand/toggle it */
export async function toggleModule(page: Page, moduleLabel: string) {
  await page.getByRole('button', { name: `Activar módulo ${moduleLabel}` }).click();
}

/** Select a specific tier inside an expanded module by its tier label (e.g. "Básico") */
export async function selectTier(page: Page, tierLabel: string) {
  await page.getByRole('radio', { name: new RegExp(tierLabel) }).first().click();
}

/**
 * Activate a module and select its first available tier.
 * Returns after the module is active and a tier is selected.
 */
export async function activateModule(page: Page, moduleLabel: string) {
  // Click the module header row (role=button with aria-expanded)
  const moduleArticle = page.locator(`article[aria-label="Módulo ${moduleLabel}"]`);
  await moduleArticle.getByRole('button', { name: new RegExp(`Activar|Desactivar`) }).click();
  // Select first tier
  await moduleArticle.getByRole('radio').first().click();
}

/** Fill a single beneficiary row (first one by default) with valid data */
export async function fillBeneficiary(
  page: Page,
  moduleId: string,
  opts: { name?: string; relation?: string; pct?: string } = {}
) {
  const name = opts.name ?? VALID_BENEFICIARY.name;
  const relation = opts.relation ?? VALID_BENEFICIARY.relation;
  const pct = opts.pct ?? VALID_BENEFICIARY.pct;

  // Find the first beneficiary name field for this module (id starts with ben-name-{moduleId})
  const nameInput = page.locator(`[id^="ben-name-${moduleId}-"]`).first();
  await nameInput.fill(name);
  await nameInput.blur();

  const relSelect = page.locator(`[id^="ben-rel-${moduleId}-"]`).first();
  await relSelect.selectOption(relation);

  const pctInput = page.locator(`[id^="ben-pct-${moduleId}-"]`).first();
  await pctInput.fill(pct);
  await pctInput.blur();
}

/** Fill the holder form with valid data */
export async function fillHolder(page: Page, data = VALID_HOLDER) {
  await page.locator('#holder-name').fill(data.name);
  await page.locator('#holder-name').blur();

  await page.locator('#holder-email').fill(data.email);
  await page.locator('#holder-email').blur();

  await page.locator('#holder-phone').fill(data.phone);
  await page.locator('#holder-phone').blur();

  await page.locator('#holder-dob').fill(data.dob);
  await page.locator('#holder-dob').blur();
}

/** Click the main CTA button in the cart */
export async function clickCTA(page: Page) {
  await page.getByRole('button', { name: 'Continuar →' }).click();
}

/** Wait for and return the policy number from the success screen */
export async function getPolicyNumber(page: Page): Promise<string> {
  await expect(page.getByText('¡Cotización creada!')).toBeVisible({ timeout: 15_000 });
  const policyEl = page.locator('text=/BLF-[A-Z0-9]+/');
  await expect(policyEl).toBeVisible();
  return (await policyEl.textContent()) ?? '';
}
