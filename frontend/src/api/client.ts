import { fixtureCases } from '../fixtures/generatedTestCases';
import type { GenerationPayload, GenerationResult, TestCase } from '../types';

export async function generateTestCases(payload: GenerationPayload): Promise<GenerationResult> {
  const baseUrl = import.meta.env.VITE_API_BASE_URL as string | undefined;
  if (!baseUrl) {
    await new Promise((resolve) => setTimeout(resolve, 900));
    return { testCases: fixtureCases, generatedAt: new Date().toISOString(), source: 'fixture' };
  }

  const response = await fetch(`${baseUrl.replace(/\/$/, '')}/api/generate`, {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload),
  });
  const body = await response.json().catch(() => null);
  if (!response.ok) throw new Error(body?.error ?? 'Generation service unavailable.');
  return { testCases: flattenCases(body?.testCases), generatedAt: new Date().toISOString(), source: 'api' };
}

function flattenCases(group: Record<string, Array<Record<string, unknown>>> | undefined): TestCase[] {
  const categories = ['positive', 'negative', 'boundary', 'edge'] as const;
  return categories.flatMap((category) => (group?.[category] ?? []).map((item, index) => {
    const steps = Array.isArray(item.steps) ? item.steps.map(String) : [];
    const expectedResult = String(item.expectedResult ?? 'The expected outcome is observed.');
    const automation = readAutomation(item);
    return {
      id: String(item.id ?? `TC${String(index + 1).padStart(3, '0')}`),
      scenario: String(item.scenario ?? 'Generated scenario'),
      steps,
      expectedResult,
      type: titleCase(String(item.type ?? category)),
      priority: category === 'negative' ? 'Critical' : category === 'positive' ? 'Blocker' : 'Major',
      given: 'the product is ready for the documented flow',
      when: steps.join(' and '),
      then: expectedResult,
      ...(automation ? { automation } : {}),
    };
  }));
}

function readAutomation(item: Record<string, unknown>): TestCase['automation'] | undefined {
  const source = item.automation && typeof item.automation === 'object' ? item.automation as Record<string, unknown> : item;
  const automation: NonNullable<TestCase['automation']> = {};
  for (const language of ['playwright', 'cypress', 'gherkin'] as const) {
    const value = source[language];
    if (typeof value === 'string' && value.trim()) automation[language] = value;
  }
  return Object.keys(automation).length ? automation : undefined;
}

function titleCase(value: string): TestCase['type'] {
  const normalized = value.toLowerCase();
  if (normalized === 'negative') return 'Negative';
  if (normalized === 'boundary') return 'Boundary';
  if (normalized === 'edge') return 'Edge';
  return 'Positive';
}
