export type Category = 'Positive' | 'Negative' | 'Boundary' | 'Edge';
export type Priority = 'Blocker' | 'Critical' | 'Major';
export type CodeLanguage = 'playwright' | 'cypress' | 'gherkin';

export interface TestCase {
  id: string;
  scenario: string;
  steps: string[];
  expectedResult: string;
  type: Category;
  priority: Priority;
  given: string;
  when: string;
  then: string;
  automation?: Partial<Record<CodeLanguage, string>>;
}

export interface StoryDraft {
  feature: string;
  userStory: string;
  expectedBehavior: string;
}

export interface GenerationPayload extends StoryDraft {
  options: string[];
}

export interface GenerationResult {
  testCases: TestCase[];
  generatedAt: string;
  source: 'fixture' | 'api';
}
