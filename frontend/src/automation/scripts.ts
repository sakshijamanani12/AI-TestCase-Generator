import type { CodeLanguage, TestCase } from '../types';

const escapeScript = (value: string) => value.replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/\r?\n/g, ' ');

function playwrightActions(step: string): string[] {
  const normalized = step.trim().toLowerCase();
  const safeStep = escapeScript(step);

  if (/\b(open|navigate to|go to)\b.*\b(login|sign[- ]?in)\b/.test(normalized)) {
    return ["await page.goto('/login');"];
  }
  if (/\benter\b.*\bvalid\b.*\bcredentials\b/.test(normalized)) {
    return [
      "await page.getByLabel('Email').fill('user@example.com');",
      "await page.getByLabel('Password').fill('Password123!');",
    ];
  }
  if (/\b(email|e-mail)\b/.test(normalized) && /\benter\b/.test(normalized)) {
    const value = /without an? @|invalid email|invalid format/.test(normalized) ? 'invalid-email' : 'user@example.com';
    return [`await page.getByLabel('Email').fill('${value}');`];
  }
  if (/sql injection/.test(normalized)) {
    return ["await page.getByLabel('Email').fill(\"' OR '1'='1\");"];
  }
  if (/\b(password|passcode)\b/.test(normalized) && /\b(enter|use)\b/.test(normalized)) {
    const value = /129|over the maximum|too long/.test(normalized) ? 'P'.repeat(129) : 'Password123!';
    return [`await page.getByLabel('Password').fill('${value}');`];
  }
  if (/remember me/.test(normalized) && /\b(enable|check|select|tick)\b/.test(normalized)) {
    return ["await page.getByLabel('Remember me').check();"];
  }
  if (/\b(submit|sign in|log in|login)\b/.test(normalized) && /\b(form|button|request|credentials)?\b/.test(normalized)) {
    return ["await page.getByRole('button', { name: /sign in|login|submit/i }).click();"];
  }
  if (/\b(reopen|reload|refresh)\b.*\b(browser|page|screen)\b/.test(normalized)) {
    return ['await page.reload();'];
  }
  if (/\btab\b/.test(normalized)) {
    return ["await page.keyboard.press('Tab');"];
  }
  if (/\bpress\s+enter\b/.test(normalized)) {
    return ["await page.keyboard.press('Enter');"];
  }

  return [`// TODO: Implement step: ${safeStep}`];
}

function fallbackScript(item: TestCase, language: CodeLanguage): string {
  const title = escapeScript(item.scenario);
  const steps = item.steps.map(escapeScript);
  const expected = escapeScript(item.expectedResult);

  if (language === 'gherkin') {
    return [
      `Feature: ${title}`,
      '',
      `  Scenario: ${title}`,
      `    Given ${escapeScript(item.given)}`,
      `    When ${steps[0] ?? 'the user follows the documented flow'}`,
      ...steps.slice(1).map((step) => `    And ${step}`),
      `    Then ${expected}`,
    ].join('\n');
  }

  if (language === 'cypress') {
    return [
      `describe('${title}', () => {`,
      `  it('${title}', () => {`,
      ...steps.map((step, index) => `    // Step ${index + 1}: ${step}`),
      '',
      `    // Expected: ${expected}`,
      '  });',
      '});',
    ].join('\n');
  }

  return [
    `test('${title}', async ({ page }) => {`,
    ...item.steps.flatMap((step) => playwrightActions(step).map((action) => `  ${action}`)),
    '',
    `  // Expected: ${expected}`,
    '});',
  ].join('\n');
}

export function scriptForCase(item: TestCase, language: CodeLanguage): string {
  const provided = item.automation?.[language];
  return provided?.trim() ? provided.replace(/\\n/g, '\n') : fallbackScript(item, language);
}

export function suiteForCases(items: TestCase[], language: CodeLanguage): string {
  if (language === 'cypress' && !items.some((item) => item.automation?.cypress?.trim())) {
    return [
      "describe('Generated coverage suite', () => {",
      ...items.flatMap((item) => {
        const title = escapeScript(item.scenario);
        const steps = item.steps.map(escapeScript);
        return [
          `  it('${title}', () => {`,
          ...steps.map((step, index) => `    // Step ${index + 1}: ${step}`),
          `    // Expected: ${escapeScript(item.expectedResult)}`,
          '  });',
          '',
        ];
      }),
      '});',
    ].join('\n');
  }
  return items.map((item) => scriptForCase(item, language)).join('\n\n');
}
