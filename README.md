# AI Test Case Generator

> **Turn requirements into a review-ready QA suite in minutes.**
>
> Paste a story, import a document, attach a wireframe, or combine them. The application uses AI to generate practical positive, negative, boundary, and edge test cases, then validates the result before you export it.

## Why teams use it

Manual test design is slow, repetitive, and easy to under-cover. AI Test Case Generator gives product, QA, and engineering teams a faster starting point without treating AI output as automatically correct.

- **Faster test design**: turn a requirement into a structured suite in one click.
- **Broader coverage**: generate positive, negative, boundary, and edge scenarios.
- **Multiple input styles**: paste text, upload TXT/PDF requirements, or attach PNG/JPG wireframes and JSON test data.
- **Useful outputs**: inspect cases in the dashboard and export JSON or CSV for automation and test-management tools.
- **Built-in quality gates**: malformed, incomplete, duplicated, or incorrectly categorized cases are rejected before export.
- **Secure architecture**: the OpenRouter key stays in the Java backend and is never sent to the browser.

## What the frontend offers

The React dashboard is the easiest way to use the product. It provides a focused workflow from input to export:

1. **Describe the feature** using editable Feature, User Story, and Expected Behavior fields.
2. **Import requirements** from TXT or PDF, with automatic extraction of recognized sections.
3. **Attach supporting context** such as a PNG/JPG wireframe or JSON file when the story needs UI or data details.
4. **Choose coverage depth** for positive, negative, boundary, and edge scenarios.
5. **Generate test cases** through the Java API and OpenRouter.
6. **Review coverage visually** with totals, category counts, search, filters, and expandable case details.
7. **Inspect automation previews** for Playwright, Cypress, or Gherkin-style output.
8. **Download the suite** as JSON or CSV.

The interface also includes demo mode, so the dashboard can be explored without Java or an API key. Demo mode uses fixture data; real AI generation requires the Java API server.

## Input options at a glance

| Input | Supported use |
|---|---|
| Pasted text | Enter a story directly in the dashboard. |
| TXT document | Import plain-text requirements. |
| PDF document | Extract text from a PDF requirement document. |
| PNG/JPG image | Attach a wireframe or screenshot as supporting context. |
| JSON file | Attach structured test or UI data as supporting context. |
| Java CLI file | Generate from a story file using the command line. |

> **Important:** images and JSON are attached as supporting context in the frontend. The main story still comes from the editable story fields or extracted TXT/PDF text.

## What outputs are generated?

Every successful run produces a structured QA suite with fields such as:

- Unique test-case ID
- Category: Positive, Negative, Boundary, or Edge
- Test type
- Scenario
- Preconditions
- Test steps
- Test data
- Expected result
- Automation-ready preview where applicable

The application generates:

- **JSON**: complete structured output for APIs, automation, and future processing.
- **CSV**: spreadsheet-friendly output for review, sharing, and test-management import.
- **Dashboard results**: searchable, filterable cases with category coverage and expanded details.
- **Automation previews**: generated snippets in Playwright, Cypress, or Gherkin format.

Default files:

```text
output/generated_test_cases.json
output/generated_test_cases.csv
```

## Coverage and automation

The generator is designed to cover the four core test-case types in every suite:

| Type | What it checks |
|---|---|
| Positive | The feature works when valid input and normal actions are used. |
| Negative | Invalid input, rejected actions, errors, and failure messages. |
| Boundary | Minimums, maximums, limits, empty values, and just-inside or just-outside values. |
| Edge | Unusual flows, refreshes, repeated actions, unexpected state, and less common user behavior. |

The dashboard shows the count for each category. The quality validator also checks that Positive, Negative, Boundary, and Edge coverage are all represented before the result is treated as a complete suite.

### Basic Playwright script layout

The frontend generates a basic Playwright test layout for each selected case. It includes:

1. A `test()` block named after the scenario.
2. Playwright actions such as `page.goto()`, `getByLabel().fill()`, `getByRole().click()`, `check()`, `reload()`, and keyboard actions when the step can be recognized.
3. A comment containing the expected result.
4. A `TODO` comment for any step that needs a project-specific locator or implementation.

The same case can also be viewed as Cypress JavaScript or Cucumber Gherkin.

> **Important:** The generated Playwright scripts are not entirely reliable or production-ready. They are intended to provide a basic automation layout and a helpful starting point. Review and adapt the locators, test data, authentication flow, environment setup, and assertions for the application before using them in a real test suite.

## How quality and accuracy are checked

The product does not claim that an AI model is perfect. Instead, it uses several checks to make the result safer and more consistent:

### 1. Input validation

The story is checked for the information needed to generate useful cases. Missing feature, story, or expected-behavior information is reported before generation.

### 2. Response parsing

The AI response must be valid structured JSON. Empty responses, malformed JSON, missing `choices`, and missing `content` are handled as errors instead of being silently exported.

### 3. Required-field validation

Each test case must contain the fields needed to execute or review it, including an ID, scenario, steps, and expected result.

### 4. Duplicate detection

The validator checks for duplicate test-case IDs and duplicate scenarios so the suite does not contain accidental repetitions.

### 5. Category and type consistency

A test case labelled Positive, Negative, Boundary, or Edge must use a matching test type. Incorrect combinations are rejected.

### 6. Coverage quality

The quality validator checks that all four requested categories are represented. The dashboard shows the resulting coverage profile so gaps are visible immediately.

### 7. Human review remains part of QA

Validation checks structure, consistency, and coverage. It cannot prove that every scenario matches the product perfectly. Review the generated suite against the original requirement before using it in production testing.

## Product flow

```text
Requirement, document, or wireframe
                ↓
        Extract and validate input
                ↓
        Build a structured AI prompt
                ↓
          Generate test cases
                ↓
      Parse and validate the response
                ↓
       Review coverage and details
                ↓
            Export JSON or CSV
```

## Technology

- Java 25 LTS
- Maven
- OpenRouter API
- OkHttp
- Jackson
- SLF4J
- Swing desktop UI
- React, Vite, and TypeScript dashboard
- JUnit 5

## Important files

### Java entry points

| File | Purpose |
|---|---|
| `Main.java` | Runs the CLI. It accepts a file path or asks for one when no argument is provided. |
| `GuiMain.java` | Opens the Swing desktop application with file browsing, previews, generation, and export. |
| `Launcher.java` | Selects GUI, CLI, or API-server mode from one entry point. |

### Java services

| File | Purpose |
|---|---|
| `GenerationService.java` | Coordinates reading, AI generation, parsing, validation, and file output. |
| `AIService.java` | Calls OpenRouter, handles timeouts/errors, and retries temporary failures. |
| `ApiServer.java` | Provides the local API used by the React dashboard. |
| `GenerationResult.java` | Holds generation status, messages, structured output, CSV, and file paths. |
| `PromptBuilder.java` | Builds the structured prompt sent to the model. |
| `ResponseParser.java` | Converts the AI response into Java model objects. |

### Validation and output

| File | Purpose |
|---|---|
| `TestCaseValidator.java` | Checks required fields, duplicates, and category/type consistency. |
| `TestCaseQualityValidator.java` | Checks coverage of all four test categories. |
| `CsvExporter.java` | Creates spreadsheet-friendly CSV output. |
| `FileUtil.java` | Creates output folders and writes generated files. |
| `TestCasePrinter.java` | Prints cases and validation summaries in the terminal. |

### Frontend

| File | Purpose |
|---|---|
| `frontend/src/App.tsx` | Main dashboard, input workflow, filters, results, and downloads. |
| `frontend/src/api/client.ts` | Calls the Java API or loads demo fixtures when no API URL is configured. |
| `frontend/src/utils/storyDocument.ts` | Reads TXT/PDF files and extracts story sections. |
| `frontend/src/automation/scripts.ts` | Builds automation previews. |
| `frontend/src/fixtures/generatedTestCases.ts` | Provides offline demo data. |

## Project folders

```text
input/                  Example user-story files
output/                 Generated JSON and CSV files
src/main/java/          Java CLI, GUI, API, services, and validators
src/main/resources/     Non-secret application configuration
src/test/java/          JUnit tests
frontend/               React dashboard
.vscode/                Java 25 settings and run profiles
pom.xml                Maven configuration
```

## Input format for Java CLI and API

A simple story can use these sections:

```text
Feature: User Login

User Story:
As a registered user, I want to log in to my account.

Expected Behavior:
Valid users should reach the dashboard.
Invalid users should see an error message.
```

The frontend recognizes:

- `Feature`
- `User Story`
- `Expected Behavior`
- `Acceptance Criteria`

## Setup

### 1. Configure the API key

The Java backend needs an OpenRouter key. Set it in the same PowerShell terminal used to start the backend:

```powershell
$env:OPENROUTER_API_KEY="your-openrouter-api-key"
```

**Why:** the key is kept on the server and out of browser-visible frontend code.

Never put `OPENROUTER_API_KEY` in a `VITE_*` variable or commit it to Git.

### 2. Confirm Java and Maven

```powershell
java -version
mvn -version
```

**Why:** the project compiles for Java 25 LTS and uses Maven to provide dependencies such as Jackson and OkHttp.

## Run the Java CLI

From the project root:

```powershell
mvn exec:java "-Dexec.mainClass=com.sakshi.Main" "-Dexec.args=input/user_story.txt"
```

**Why:** this runs the CLI with the Maven classpath and uses the supplied story file.

To be asked for a path instead:

```powershell
mvn exec:java "-Dexec.mainClass=com.sakshi.Main"
```

**Why:** without a file argument, `Main.java` prompts:

```text
Enter user story file path:
```

## Run the Java desktop GUI

```powershell
mvn exec:java "-Dexec.mainClass=com.sakshi.GuiMain"
```

**Why:** the Swing GUI is useful when you want file browsing, story previews, generated JSON, CSV-style results, and output locations without using the terminal.

## Run from VS Code

1. Open the repository root in VS Code.
2. Install the Extension Pack for Java.
3. Set `OPENROUTER_API_KEY` in the integrated terminal.
4. Open **Run and Debug** with `Ctrl+Shift+D`.
5. Select a profile and press `F5`.

Available profiles:

- **AI Test Case Generator (Launcher)**: opens the mode selector.
- **AI Test Case Generator (CLI)**: runs the example story.
- **AI Test Case Generator (GUI)**: opens the Swing application.

**Why:** the profiles use the Java project classpath and Maven dependencies, avoiding the common generic Code Runner classpath problem.

## Run the full React dashboard with real AI

Use two terminals because the Java process protects the API key and the React process serves the browser interface.

### Terminal 1: start the Java API

From the project root:

```powershell
$env:OPENROUTER_API_KEY="your-openrouter-api-key"
mvn exec:java "-Dexec.mainClass=com.sakshi.Launcher" "-Dexec.args=--server"
```

You should see:

```text
Java generator API listening at http://localhost:8080
```

Check the backend:

```text
http://localhost:8080/api/health
```

Expected response:

```json
{"status":"ok"}
```

**Why:** the Java API is the secure bridge between the browser and OpenRouter.

### Terminal 2: start the frontend

```powershell
cd frontend
npm install
$env:VITE_API_BASE_URL="http://localhost:8080"
npm run dev
```

Open the Vite URL, usually:

```text
http://localhost:5173
```

Then import or paste a story, attach optional context, choose coverage options, and click **Generate test cases**.

**Why:** `VITE_API_BASE_URL` tells the frontend to use real backend generation instead of fixture data.

### Optional frontend configuration

Create `frontend/.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Restart Vite after changing it. Do not place the OpenRouter key in this file.

### Demo mode

If `VITE_API_BASE_URL` is not set, the dashboard loads sample results from `frontend/src/fixtures/generatedTestCases.ts`.

**Why:** demo mode lets people explore the interface without Java, credentials, or API usage. It is not real AI generation.

## API endpoints

### Health check

```text
GET http://localhost:8080/api/health
```

### Generate test cases

```text
POST http://localhost:8080/api/generate
```

Example request:

```json
{
  "feature": "User Login",
  "userStory": "As a registered user, I want to log in.",
  "expectedBehavior": "Valid users reach the dashboard."
}
```

## Build and test

Java tests:

```powershell
mvn clean test
```

Frontend production build:

```powershell
npm --prefix frontend run build
```

The test suite covers response parsing, AI-service errors, file handling, CSV export, input validation, duplicate detection, category validation, and quality validation.

## Common problems

### The frontend shows demo data

Set the backend URL and restart Vite:

```powershell
$env:VITE_API_BASE_URL="http://localhost:8080"
npm run dev
```

### The frontend cannot connect

Keep the Java server terminal open and confirm `http://localhost:8080/api/health` returns `{"status":"ok"}`.

### API authentication fails

Set the key in the same terminal that starts the Java server. Do not put it in the frontend environment.

### Main.java does not ask for a path

A file path was passed as an argument. Run the CLI without `-Dexec.args`.

### VS Code shows dependency errors

Open the repository root, install the Java Extension Pack, reload VS Code, and use the project Run/Debug profiles instead of generic Code Runner.

## Security and responsible use

- Keep the OpenRouter key in `OPENROUTER_API_KEY`.
- Never commit the key to Git.
- Never put the key in frontend code or `VITE_*` variables.
- Review generated cases against the original requirement before production use.
- Treat the validator as a quality gate for structure and coverage, not a replacement for human QA judgment.

## Author

Sakshi Jamanani
