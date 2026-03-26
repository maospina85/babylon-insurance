import { test, expect } from '@playwright/test';
import { loadApp } from '../helpers/setup';
import { VALID_HOLDER, INVALID_HOLDER } from '../fixtures/test-data';

test.describe('Suite 5 — Formulario del Titular', () => {

  test('5.1 — Sin tocar los campos, no se muestran errores', async ({ page }) => {
    await loadApp(page);

    // Error spans exist but should be empty (no text)
    await expect(page.locator('#holder-name-err')).toBeEmpty();
    await expect(page.locator('#holder-email-err')).toBeEmpty();
    await expect(page.locator('#holder-phone-err')).toBeEmpty();
    await expect(page.locator('#holder-dob-err')).toBeEmpty();
  });

  test('5.2 — Nombre válido con tildes muestra borde verde (no error)', async ({ page }) => {
    await loadApp(page);

    await page.locator('#holder-name').fill(VALID_HOLDER.name);
    await page.locator('#holder-name').blur();

    await expect(page.locator('#holder-name-err')).toBeEmpty();
  });

  test('5.3 — Nombre con números muestra error de validación', async ({ page }) => {
    await loadApp(page);

    await page.locator('#holder-name').fill(INVALID_HOLDER.nameWithNumbers);
    await page.locator('#holder-name').blur();

    await expect(page.locator('#holder-name-err')).toContainText('Solo letras y espacios');
  });

  test('5.4 — Email inválido muestra error; completar lo limpia', async ({ page }) => {
    await loadApp(page);

    await page.locator('#holder-email').fill(INVALID_HOLDER.emailNoAt);
    await page.locator('#holder-email').blur();
    await expect(page.locator('#holder-email-err')).toContainText('Ingresa un correo válido');

    await page.locator('#holder-email').fill(VALID_HOLDER.email);
    await page.locator('#holder-email').blur();
    await expect(page.locator('#holder-email-err')).toBeEmpty();
  });

  test('5.5 — Teléfono muy corto muestra error; teléfono válido no', async ({ page }) => {
    await loadApp(page);

    await page.locator('#holder-phone').fill(INVALID_HOLDER.phoneShort);
    await page.locator('#holder-phone').blur();
    await expect(page.locator('#holder-phone-err')).toContainText('Mínimo 7 dígitos');

    await page.locator('#holder-phone').fill(VALID_HOLDER.phone);
    await page.locator('#holder-phone').blur();
    await expect(page.locator('#holder-phone-err')).toBeEmpty();
  });

  test('5.6 — Fecha de nacimiento menor de 18 años muestra error', async ({ page }) => {
    await loadApp(page);

    await page.locator('#holder-dob').fill(INVALID_HOLDER.dobUnder18);
    await page.locator('#holder-dob').blur();

    await expect(page.locator('#holder-dob-err')).toContainText('mayor de 18 años');
  });

  test('5.7 — Fecha de nacimiento de 70 años o más muestra error', async ({ page }) => {
    await loadApp(page);

    await page.locator('#holder-dob').fill(INVALID_HOLDER.dobOver70);
    await page.locator('#holder-dob').blur();

    await expect(page.locator('#holder-dob-err')).toContainText('menor de 70 años');
  });

});
