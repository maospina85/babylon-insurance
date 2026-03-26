import { Page, expect } from '@playwright/test';
import { VALID_HOLDER, VALID_BENEFICIARY } from '../fixtures/test-data';

/** Navigate to app and wait for catalog to load */
export async function loadApp(page: Page) {
  await page.goto('/');
  // Wait until loading spinner is gone — Cloud Run min=0 can cold-start in ~25s
  await expect(page.getByText('Cargando catálogo…')).not.toBeVisible({ timeout: 45_000 });
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
  const moduleArticle = page.locator(`article[aria-label="Módulo ${moduleLabel}"]`);
  // Click the switch directly to avoid ambiguity with the outer header button
  await moduleArticle.locator('[role="switch"]').click();
  // Wait until the switch is confirmed checked before selecting tier
  await expect(moduleArticle.locator('[role="switch"]')).toHaveAttribute('aria-checked', 'true');
  // Select first tier and wait until it is marked as pressed
  const firstTier = moduleArticle.getByRole('radio').first();
  await firstTier.click();
  await expect(firstTier).toHaveAttribute('aria-pressed', 'true');
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

  // Use input[] prefix to avoid matching the error <span> elements that share the same id prefix
  const nameInput = page.locator(`input[id^="ben-name-${moduleId}-"]`).first();

  // If no rows exist yet, add the first one
  if (await nameInput.count() === 0) {
    await page.getByRole('button', { name: /Agregar beneficiario/ }).first().click();
    await expect(nameInput).toBeVisible({ timeout: 5_000 });
  }

  await nameInput.fill(name);
  await nameInput.blur();

  const relSelect = page.locator(`select[id^="ben-rel-${moduleId}-"]`).first();
  await relSelect.selectOption(relation);

  const pctInput = page.locator(`input[id^="ben-pct-${moduleId}-"]`).first();
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
  // Button text is 'Continuar →' but aria-label overrides to 'Continuar con la cotización'
  await page.getByRole('button', { name: 'Continuar con la cotización' }).click();
}

/** Wait for and return the policy number from the success screen */
export async function getPolicyNumber(page: Page): Promise<string> {
  await expect(page.getByText('¡Cotización creada!')).toBeVisible({ timeout: 40_000 });
  const policyEl = page.locator('text=/BLF-[A-Z0-9]+/');
  await expect(policyEl).toBeVisible();
  return (await policyEl.textContent()) ?? '';
}
