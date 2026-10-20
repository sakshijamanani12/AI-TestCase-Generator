import type { TestCase } from '../types';

const make = (id: string, type: TestCase['type'], scenario: string, steps: string[], expectedResult: string, priority: TestCase['priority']): TestCase => ({
  id, type, scenario, steps, expectedResult, priority,
  given: 'the visitor is on the secure login screen',
  when: steps.slice(1, -1).join(' and ').toLowerCase(),
  then: expectedResult,
});

export const fixtureCases: TestCase[] = [
  make('TC001', 'Positive', 'Sign in with valid credentials', ['Open the login screen', 'Enter a registered email', 'Enter the correct password', 'Submit the form'], 'The workspace opens and the session is created.', 'Blocker'),
  make('TC002', 'Positive', 'Persist a session with Remember me', ['Open the login screen', 'Enter valid credentials', 'Enable Remember me', 'Submit the form', 'Reopen the browser'], 'The user remains authenticated after reopening the browser.', 'Critical'),
  make('TC003', 'Negative', 'Reject an invalid email format', ['Open the login screen', 'Enter an email without an @ symbol', 'Enter a valid password', 'Submit the form'], 'An inline validation message explains the email format.', 'Critical'),
  make('TC004', 'Negative', 'Prevent SQL injection from reaching auth', ['Open the login screen', "Enter a SQL injection payload", 'Enter any password', 'Submit the form'], 'The request is rejected without exposing a database error.', 'Blocker'),
  make('TC005', 'Boundary', 'Accept the maximum password length', ['Open the login screen', 'Enter valid credentials', 'Use a 128-character password', 'Submit the form'], 'The credential is accepted at the documented limit.', 'Major'),
  make('TC006', 'Boundary', 'Reject a password over the maximum length', ['Open the login screen', 'Enter a valid email', 'Use a 129-character password', 'Submit the form'], 'A clear length validation message is displayed.', 'Critical'),
  make('TC007', 'Edge', 'Recover gracefully after a timed-out request', ['Open the login screen', 'Enter valid credentials', 'Simulate a network timeout', 'Submit the form'], 'The user sees a retry action and no duplicate session is created.', 'Major'),
  make('TC008', 'Edge', 'Keep the form usable with keyboard navigation', ['Open the login screen', 'Tab through each field', 'Enter valid credentials', 'Press Enter'], 'Focus order is logical and the form submits once.', 'Major'),
];

export const defaultDraft = {
  feature: 'Secure user authentication',
  userStory: 'As a registered user, I want to sign in securely so that I can access my workspace.',
  expectedBehavior: 'Valid credentials open the workspace. Invalid input is rejected with actionable feedback.',
};
