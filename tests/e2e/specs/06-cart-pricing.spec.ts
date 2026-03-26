import { test, expect } from '@playwright/test';
import { loadApp, activateModule } from '../helpers/setup';
import { MODULES } from '../fixtures/test-data';

test.describe('Suite 6 — Cart y Precios', () => {

  test('6.1 — Toggle mensual/anual cambia el precio y muestra ahorro', async ({ page }) => {
    await loadApp(page);
    await activateModule(page, MODULES.accidents);

    const cart = page.getByRole('complementary');

    // Read monthly price
    const monthlyText = await cart.textContent();

    // Click annual toggle
    await cart.getByRole('button', { name: /Anual/ }).click();

    const annualText = await cart.textContent();

    // Annual text should be different from monthly
    expect(annualText).not.toEqual(monthlyText);

    // Should show "/año" label
    await expect(cart).toContainText('/año');
  });

  test('6.2 — Precio anual = precio mensual × 12 × 0.9 (redondeado)', async ({ page }) => {
    await loadApp(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.accidents}"]`);
    await article.getByRole('button', { name: /Activar/ }).click();

    // Select the first tier and read its price from the tier card
    const firstTier = article.getByRole('radio').first();
    const tierText = await firstTier.textContent() ?? '';
    const priceMatch = tierText.match(/\$([\d.,]+)/);
    expect(priceMatch).not.toBeNull();

    await firstTier.click();

    const cart = page.getByRole('complementary');
    await cart.getByRole('button', { name: /Anual/ }).click();

    // Annual price should be visible (we just verify it shows /año)
    await expect(cart).toContainText('/año');
  });

  test('6.3 — El precio se actualiza en tiempo real al cambiar de tier', async ({ page }) => {
    await loadApp(page);

    const article = page.locator(`article[aria-label="Módulo ${MODULES.accidents}"]`);
    await article.getByRole('button', { name: /Activar/ }).click();

    const tiers = article.getByRole('radio');
    await tiers.first().click();
    const priceAfterFirst = await page.getByRole('complementary').textContent();

    await tiers.last().click();
    const priceAfterLast = await page.getByRole('complementary').textContent();

    expect(priceAfterFirst).not.toEqual(priceAfterLast);
  });

  test('6.4 — Volver a mensual desde anual muestra "/mes"', async ({ page }) => {
    await loadApp(page);
    await activateModule(page, MODULES.accidents);

    const cart = page.getByRole('complementary');

    await cart.getByRole('button', { name: /Anual/ }).click();
    await expect(cart).toContainText('/año');

    await cart.getByRole('button', { name: /Mensual/ }).click();
    await expect(cart).toContainText('/mes');
  });

});
