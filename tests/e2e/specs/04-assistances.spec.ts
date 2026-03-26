import { test, expect } from '@playwright/test';
import { loadApp, activateModule } from '../helpers/setup';
import { MODULES, ASSISTANCES } from '../fixtures/test-data';

test.describe('Suite 4 — Asistencias', () => {

  test('4.1 — Sin cobertura activa, todas las asistencias están deshabilitadas', async ({ page }) => {
    await loadApp(page);

    for (const label of Object.values(ASSISTANCES)) {
      const card = page.getByRole('checkbox', { name: new RegExp(label) });
      await expect(card).toHaveAttribute('aria-disabled', 'true');
    }
  });

  test('4.2 — Con 2 módulos activos solo se pueden seleccionar 2 asistencias', async ({ page }) => {
    await loadApp(page);

    await activateModule(page, MODULES.accidents);
    await activateModule(page, MODULES.disability);

    // Select 2 assistances
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.juridico) }).click();
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.psicologico) }).click();

    // A third one should be disabled
    const thirdCard = page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.nutricion) });
    await expect(thirdCard).toHaveAttribute('aria-disabled', 'true');
  });

  test('4.3 — Quitar cobertura hace auto-trim de asistencias seleccionadas', async ({ page }) => {
    await loadApp(page);

    await activateModule(page, MODULES.accidents);
    await activateModule(page, MODULES.disability);

    // Select 2 assistances
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.juridico) }).click();
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.psicologico) }).click();

    // Deactivate one module
    const article = page.locator(`article[aria-label="Módulo ${MODULES.disability}"]`);
    await article.getByRole('button', { name: /Desactivar/ }).click();

    // Cart should now show only 1 assistance
    await expect(page.getByRole('complementary')).toContainText('Asistencias incluidas (1)');
  });

  test('4.4 — La asistencia seleccionada aparece como chip en el CartSummary', async ({ page }) => {
    await loadApp(page);

    await activateModule(page, MODULES.accidents);
    await page.getByRole('checkbox', { name: new RegExp(ASSISTANCES.medico_virtual) }).click();

    await expect(page.getByRole('complementary')).toContainText(ASSISTANCES.medico_virtual);
  });

});
