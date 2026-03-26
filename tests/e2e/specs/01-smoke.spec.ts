import { test, expect } from '@playwright/test';
import { loadApp } from '../helpers/setup';
import { MODULES, ASSISTANCES } from '../fixtures/test-data';

test.describe('Suite 1 — Smoke & Render', () => {

  test('1.1 — La app carga y muestra las 3 secciones principales', async ({ page }) => {
    await loadApp(page);

    await expect(page.getByRole('heading', { name: '1. Elige tus coberturas' })).toBeVisible();
    await expect(page.getByRole('heading', { name: '2. Asistencias incluidas' })).toBeVisible();
    await expect(page.getByRole('heading', { name: '3. Datos del titular' })).toBeVisible();
  });

  test('1.2 — El cart empieza vacío y el botón CTA está deshabilitado', async ({ page }) => {
    await loadApp(page);

    await expect(page.getByText('Activa un módulo y elige un plan para ver el resumen de precio.')).toBeVisible();
    const cta = page.getByRole('button', { name: 'Elige una cobertura' });
    await expect(cta).toBeVisible();
    await expect(cta).toBeDisabled();
  });

  test('1.3 — El catálogo muestra los 3 módulos y las 6 asistencias', async ({ page }) => {
    await loadApp(page);

    for (const label of Object.values(MODULES)) {
      await expect(page.getByText(label)).toBeVisible();
    }
    for (const label of Object.values(ASSISTANCES)) {
      await expect(page.getByText(label)).toBeVisible();
    }
  });

});
