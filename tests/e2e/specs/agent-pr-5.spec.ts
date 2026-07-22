import { test, expect } from '@playwright/test';
import { loadApp } from '../helpers/setup';

test.describe('Fallback smoke test (generacion invalida, ver log del agente)', () => {
  test('La app carga y muestra las 3 secciones principales', async ({ page }) => {
    await loadApp(page);
    await expect(page.getByRole('heading', { name: '1. Elige tus coberturas' })).toBeVisible();
    await expect(page.getByRole('heading', { name: '2. Asistencias incluidas' })).toBeVisible();
    await expect(page.getByRole('heading', { name: '3. Datos del titular' })).toBeVisible();
  });
});
