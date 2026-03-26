import { test, expect } from '@playwright/test';
import { loadApp, activateModule } from '../helpers/setup';
import { MODULES } from '../fixtures/test-data';

test.describe('Suite 2 — Selección de Cobertura', () => {

  test('2.1 — Click en toggle expande el módulo y muestra los tiers', async ({ page }) => {
    await loadApp(page);

    const moduleLabel = MODULES.death;
    const article = page.locator(`article[aria-label="Módulo ${moduleLabel}"]`);

    // Before click: module should be collapsed (aria-expanded=false)
    await expect(article.getByRole('button', { name: /Activar/ })).toBeVisible();

    // Click toggle to expand
    await article.getByRole('button', { name: /Activar/ }).click();

    // Tier cards should now be visible
    await expect(article.getByRole('radio').first()).toBeVisible();
  });

  test('2.2 — Seleccionar un tier lo marca como activo y aparece en el header del módulo', async ({ page }) => {
    await loadApp(page);

    const moduleLabel = MODULES.death;
    const article = page.locator(`article[aria-label="Módulo ${moduleLabel}"]`);

    await article.getByRole('button', { name: /Activar/ }).click();

    const firstTier = article.getByRole('radio').first();
    await firstTier.click();

    // Tier should be pressed
    await expect(firstTier).toHaveAttribute('aria-pressed', 'true');
  });

  test('2.3 — Seleccionar un tier actualiza el CartSummary con el precio', async ({ page }) => {
    await loadApp(page);

    await activateModule(page, MODULES.death);

    // Cart should no longer show empty state
    await expect(page.getByText('Activa un módulo y elige un plan para ver el resumen de precio.')).not.toBeVisible();
    // Should show at least one CartItem with the module name
    await expect(page.getByRole('complementary')).toContainText(MODULES.death);
  });

  test('2.4 — Cambiar de tier actualiza el precio en el cart', async ({ page }) => {
    await loadApp(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.death}"]`);
    await article.getByRole('button', { name: /Activar/ }).click();

    const tiers = article.getByRole('radio');
    await tiers.first().click();
    const priceFirst = await page.getByRole('complementary').textContent();

    // Select last tier (highest tier, different price)
    await tiers.last().click();
    const priceLast = await page.getByRole('complementary').textContent();

    expect(priceFirst).not.toEqual(priceLast);
  });

  test('2.5 — Desactivar módulo lo elimina del cart', async ({ page }) => {
    await loadApp(page);

    const moduleLabel = MODULES.accidents;
    const article = page.locator(`article[aria-label="Módulo ${moduleLabel}"]`);

    // Activate
    await article.getByRole('button', { name: /Activar/ }).click();
    await article.getByRole('radio').first().click();
    await expect(page.getByRole('complementary')).toContainText(moduleLabel);

    // Deactivate
    await article.getByRole('button', { name: /Desactivar/ }).click();
    await expect(page.getByText('Activa un módulo y elige un plan para ver el resumen de precio.')).toBeVisible();
  });

  test('2.6 — Activar los 3 módulos muestra 3 items en el cart', async ({ page }) => {
    await loadApp(page);

    for (const label of Object.values(MODULES)) {
      await activateModule(page, label);
    }

    const cart = page.getByRole('complementary');
    await expect(cart).toContainText('3 coberturas activas');
  });

});
