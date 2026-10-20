# AI Test Case Generator

This project creates software test cases from a user story using AI. It accepts a story, sends it to OpenRouter, validates the response, and exports the test cases as JSON and CSV.

## What the project provides

- Positive test cases
- Negative test cases
- Boundary test cases
- Edge test cases
- Duplicate ID and duplicate scenario detection
- Required-field validation
- Category and test-type validation
- JSON and CSV exports
- Command-line application
- Swing desktop application
- React dashboard
- Unit tests

## How the application works

```text
User story
   ↓
Read and validate the story
   ↓
Build an AI prompt
   ↓
Call OpenRouter
   ↓
Parse the AI response
   ↓
Validate the generated test cases
   ↓
Show and export the results
```

The validation step is important because AI output should be checked before it is used as a test suite.

## Technology used

- Java 25 LTS
- Maven
- OpenRouter API
- OkHttp
- Jackson
- Swing
- React and Vite
- TypeScript
- JUnit 5

## Important files

### Java entry points

| File | What it does | Why it is used |
|---|---|---|
| `Main.java` | Runs the command-line version. It accepts a file path or asks for one. | Useful for terminal use and scripts. |
| `GuiMain.java` | Opens the desktop Swing application. | Useful for users who prefer a graphical interface. |
| `Launcher.java` | Selects GUI, CLI, or API-server mode. | Provides one entry point for the Java application. |

### Java services

| File | What it does |
|---|---|
| `GenerationService.java` | Controls the complete generation process. |
| `AIService.java` | Sends the prompt to OpenRouter and handles API requests and retries. |
| `ApiServer.java` | Provides the local API used by the React dashboard. |
| `GenerationResult.java` | Stores the result, message, generated JSON, CSV, and output paths. |
| `PromptBuilder.java` | Builds the prompt sent to the AI model. |
| `ResponseParser.java` | Converts the AI JSON response into Java objects. |

### Models

| File | What it stores |
|---|---|
| `UserStory.java` | Feature, user story, and expected behavior. |
| `AIResponse.java` | The complete generated response. |
| `TestCaseGroup.java` | Test cases grouped by category. |
| `GeneratedTestCase.java` | One generated test case. |

### Validation and output

| File | What it does |
|---|---|
| `TestCaseValidator.java` | Checks required fields, duplicate IDs, duplicate scenarios, and category/type consistency. |
| `TestCaseQualityValidator.java` | Checks that all four test categories are covered. |
| `TestCasePrinter.java` | Prints test cases in the terminal. |
| `CsvExporter.java` | Creates CSV output. |
| `FileUtil.java` | Writes generated files to the output directory. |

### Input and configuration

| File | What it does |
|---|---|
| `UserStoryReader.java` | Reads the user-story text file. |
| `InputValidator.java` | Checks that the input has the required sections. |
| `AppConfig.java` | Loads application settings and the API key. |
| `Config.java` | Provides API configuration to the AI service. |
| `application.properties` | Stores non-secret defaults such as the model and output directory. |

## Project folders

```text
input/                         User-story files
output/                        Generated JSON and CSV files
src/main/java/                 Java application
src/main/resources/             Application configuration
src/test/java/                 JUnit tests
frontend/                      React dashboard
.vscode/                       VS Code Java settings and run profiles
pom.xml                        Maven configuration
```

## Input format

The Java application expects a text file with these sections:

```text
Feature: User Login

User Story:
As a registered user, I want to log in to my account.

Expected Behavior:
Valid users should reach the dashboard.
Invalid users should see an error message.
```

The frontend also recognizes:

- `Feature`
- `User Story`
- `Expected Behavior`
- `Acceptance Criteria`

The frontend can import TXT and PDF files.

## API key setup

The API key must be stored in an environment variable. This keeps the key out of the source code and out of the browser.

In PowerShell:

```powershell
$env:OPENROUTER_API_KEY="your-openrouter-api-key"
```

Do not put the key in a frontend `VITE_*` variable. Frontend variables are visible in the browser.

## Run the Java CLI

From the project root:

```powershell
mvn exec:java "-Dexec.mainClass=com.sakshi.Main" "-Dexec.args=input/user_story.txt"
```

### Why use this command?

It runs the Java application through Maven, so all required libraries such as Jackson and OkHttp are included.

To enter the path yourself, run without `-Dexec.args`:

```powershell
mvn exec:java "-Dexec.mainClass=com.sakshi.Main"
```

The application will ask:

```text
Enter user story file path:
```

## Run the Java GUI

```powershell
mvn exec:java "-Dexec.mainClass=com.sakshi.GuiMain"
```

### Why use the GUI?

The GUI lets you browse for a story file, preview it, generate test cases, view JSON, view the CSV-style table, and see where the output files were saved.

## Run from VS Code

The project includes Java 25 settings and run profiles in `.vscode/`.

1. Open the project root in VS Code.
2. Set the API key in the VS Code terminal.
3. Press `Ctrl+Shift+D`.
4. Select a profile.
5. Press `F5`.

Available profiles:

- **Launcher** — opens the GUI/CLI selector.
- **CLI** — runs with `input/user_story.txt`.
- **GUI** — opens the desktop application.

### Why use the VS Code profiles?

They automatically use the correct Java project classpath and Maven dependencies. This avoids errors caused by running Java files with a generic Code Runner command.

## Run the React frontend with real AI generation

The frontend uses the Java API server. Run the Java server and frontend in two separate terminals.

### Terminal 1: start the Java API

From the project root:

```powershell
$env:OPENROUTER_API_KEY="your-openrouter-api-key"
mvn exec:java "-Dexec.mainClass=com.sakshi.Launcher" "-Dexec.args=--server"
```

Keep this terminal open. You should see:

```text
Java generator API listening at http://localhost:8080
```

Check that the server is working:

```text
http://localhost:8080/api/health
```

The response should be:

```json
{"status":"ok"}
```

### Terminal 2: start the React frontend

```powershell
cd frontend
npm install
$env:VITE_API_BASE_URL="http://localhost:8080"
npm run dev
```

Open the URL printed by Vite, usually:

```text
http://localhost:5173
```

Import a story and click **Generate test cases**.

### Why are two terminals required?

The Java process holds the API key and communicates with OpenRouter. The React process only provides the user interface. Keeping these responsibilities separate protects the API key.

### Optional frontend configuration

To avoid setting the backend URL every time, create `frontend/.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Do not put `OPENROUTER_API_KEY` in this file.

### Demo mode

If `VITE_API_BASE_URL` is not set, the frontend uses sample data from `frontend/src/fixtures/generatedTestCases.ts`.

This mode is useful for viewing the interface without starting Java or using an API key, but the results are not generated by AI.

## API endpoint

The frontend sends this request:

```text
POST http://localhost:8080/api/generate
```

Example body:

```json
{
  "feature": "User Login",
  "userStory": "As a registered user, I want to log in.",
  "expectedBehavior": "Valid users reach the dashboard."
}
```

The health endpoint is:

```text
GET http://localhost:8080/api/health
```

## Output files

Generated files are written to `output/`:

```text
output/generated_test_cases.json
output/generated_test_cases.csv
```

- JSON preserves the complete structured response.
- CSV is convenient for spreadsheets and test-management tools.

## Build and test

Run all tests:

```powershell
mvn test
```

Run a clean build and tests:

```powershell
mvn clean test
```

The tests cover parsing, AI-service error handling, file handling, CSV export, input validation, duplicate detection, category validation, and quality validation.

## Common problems

### The frontend shows demo data

Set `VITE_API_BASE_URL` and restart Vite:

```powershell
$env:VITE_API_BASE_URL="http://localhost:8080"
npm run dev
```

### The frontend cannot connect to the backend

Make sure Terminal 1 is still running and that this URL works:

```text
http://localhost:8080/api/health
```

### API authentication fails

Set the API key in the same terminal used to start the Java server:

```powershell
$env:OPENROUTER_API_KEY="your-real-api-key"
```

### `Main.java` does not ask for a file path

A path was passed as an argument. Run `Main.java` without `-Dexec.args` to receive the prompt.

### VS Code shows dependency errors

Open the repository root, install the Java Extension Pack, reload VS Code, and use the Java Run/Debug profile instead of the generic Code Runner extension.

## Security reminder

- Keep the OpenRouter key in `OPENROUTER_API_KEY`.
- Never commit the key to Git.
- Never put the key in frontend code or `VITE_*` variables.
- Review AI-generated test cases before using them in a production test suite.

## Author

Sakshi Jamanani
