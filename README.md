# AI-Powered Test Case Generator

An AI-powered Java application that generates structured software test cases from user stories using Generative AI.

The application accepts a user story as input, sends it to an AI model through the OpenRouter API, parses the generated response, validates the test cases, and exports the results in JSON and CSV formats.

## Features

- Generate test cases from user stories using Generative AI
- Generate Positive test scenarios
- Generate Negative test scenarios
- Generate Boundary test scenarios
- Generate Edge test scenarios
- Validate AI-generated test cases
- Detect duplicate test case IDs
- Detect duplicate test scenarios
- Validate required test case fields
- Validate category and test case type consistency
- Parse structured AI responses using Jackson
- Handle API failures and network timeouts
- Retry failed AI requests
- Export test cases to JSON
- Export test cases to CSV
- Read user stories from external text files
- Automated unit tests using JUnit 5

## Technology Stack

- Java 17
- Maven
- OpenRouter API
- OkHttp
- Jackson
- JUnit 5
- Git
- GitHub

## Application Flow

User Story
|
v
UserStoryReader
|
v
UserStory
|
v
PromptBuilder
|
v
AIService
|
v
OpenRouter / AI Model
|
v
ResponseParser
|
v
AIResponse
|
v
TestCaseValidator
|
v
TestCasePrinter
|
+----------------+
|                |
v                v
JSON Output      CSV Output

## Project Structure

AI-TestCase-Generator
|
|-- input
|   `-- user_story.txt
|
|-- output
|   |-- generated_test_cases.json
|   `-- generated_test_cases.csv
|
|-- src
|   |-- main
|   |   `-- java
|   |       `-- com.sakshi
|   |           |-- Main.java
|   |           |
|   |           |-- model
|   |           |   |-- UserStory.java
|   |           |   |-- GeneratedTestCase.java
|   |           |   |-- TestCaseGroup.java
|   |           |   `-- AIResponse.java
|   |           |
|   |           |-- prompt
|   |           |   `-- PromptBuilder.java
|   |           |
|   |           |-- service
|   |           |   `-- AIService.java
|   |           |
|   |           |-- responseParser
|   |           |   `-- ResponseParser.java
|   |           |
|   |           |-- validator
|   |           |   `-- TestCaseValidator.java
|   |           |
|   |           |-- printer
|   |           |   `-- TestCasePrinter.java
|   |           |
|   |           |-- exporter
|   |           |   `-- CsvExporter.java
|   |           |
|   |           `-- util
|   |               |-- FileUtil.java
|   |               |-- UserStoryReader.java
|   |               `-- InputValidator.java
|   |
|   `-- test
|       `-- java
|           `-- com.sakshi
|               |-- validator
|               |-- responseParser
|               `-- util
|
|-- pom.xml
|-- .gitignore
`-- README.md

## Input

The application accepts a user story through a text file.

The default input file can be stored under:

input/user_story.txt

Example:

Feature: User Login

User Story:

As a registered user,
I want to log into the application,
so that I can access my account.

Expected Behavior:

The user should be able to log in successfully
using valid credentials.

Invalid credentials should display an appropriate
error message.

## How It Works

### 1. User Story Reading

The UserStoryReader reads the user story file and extracts:

- Feature
- User Story
- Expected Behavior

The extracted information is stored in the UserStory model.

### 2. Prompt Generation

PromptBuilder creates a structured prompt using the user story.

The prompt instructs the AI model to generate test cases across multiple testing categories.

### 3. AI Integration

AIService sends the generated prompt to an AI model through the OpenRouter API.

The service handles:

- API authentication
- HTTP requests
- AI responses
- API failures
- Network failures
- Request retries

### 4. Response Parsing

ResponseParser processes the AI response.

Jackson is used to parse the JSON response and convert it into the application's Java model classes.

### 5. Test Case Validation

TestCaseValidator validates the generated test cases.

The validation checks:

- AI response is present
- Test case groups are present
- All required categories contain test cases
- Test case IDs are present
- Test case IDs are unique
- Test scenarios are present
- Test scenarios are unique
- Test steps are present
- Expected results are present
- Test case types are present
- Test case category matches the test case type

### 6. Test Case Output

TestCasePrinter displays the generated test cases in the console.

The application also exports the generated test cases to:

- JSON
- CSV

## Test Case Categories

### Positive Test Cases

Verify that the application behaves correctly when valid inputs and expected conditions are provided.

### Negative Test Cases

Verify that the application handles invalid inputs and failure scenarios correctly.

### Boundary Test Cases

Verify behavior around minimum, maximum, and limit values.

### Edge Test Cases

Verify unusual, uncommon, or special scenarios that may occur outside normal usage.

## Example Generated Test Case

Testcase ID: TC001

Test Scenario:

Verify successful login with valid credentials

Test Steps:

1. Navigate to login page
2. Enter registered email address
3. Enter correct password
4. Click login button

Expected Result:

User is redirected to the dashboard.

Type:

Positive

## Validation Example

The application provides a validation summary after generating test cases.

Example:

========== TEST CASE SUMMARY ==========

Positive : 3
Negative : 8
Boundary : 5
Edge     : 6

----------------------------------------

Total    : 22

Unique IDs: PASSED
Duplicate Scenarios: NONE
Category Validation: PASSED

========================================

## Output

Generated files are stored in the output directory.

Example:

output/
generated_test_cases.json
generated_test_cases.csv

The JSON file contains the complete structured AI response.

The CSV file provides the generated test cases in a tabular format.

## Configuration

The application uses an OpenRouter API key to communicate with the AI model.

The API key should be stored as an environment variable.

Environment variable:

OPENROUTER_API_KEY

The API key should not be committed to the repository.

Example configuration:

public static final String API_KEY =
System.getenv("OPENROUTER_API_KEY");

## Running the Application

### Prerequisites

Install the following:

- Java 17 or higher
- Maven
- IntelliJ IDEA
- Git
- OpenRouter API key

### Run from IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Make sure Maven dependencies are loaded.
3. Configure the OPENROUTER_API_KEY environment variable.
4. Run Main.java.
5. Enter the path to the user story file when prompted.

Example:

input/user_story.txt

## Running Tests

The project uses JUnit 5 for unit testing.

The tests cover important application components including:

- Test case validation
- Response parsing
- User story reading
- File handling

Tests can be executed directly from IntelliJ IDEA.

They can also be executed using Maven:

mvn test

## Error Handling

The application handles common failures including:

- Missing input file
- Empty input file
- Missing user story sections
- Empty AI response
- Invalid AI response
- Missing AI response fields
- API errors
- Network timeouts
- Temporary AI service failures
- Invalid generated test cases

The AI service includes retry handling for temporary request failures.

## Why This Project?

Generating comprehensive test cases manually from user stories can be time-consuming and may result in missed scenarios.

This project explores how Generative AI can assist QA engineers by automatically generating structured test cases while applying automated validation rules to improve the quality and consistency of the generated output.

The project combines:

- Software testing
- Test case design
- Java development
- API integration
- Generative AI
- JSON processing
- Automated validation
- Unit testing
- Test automation concepts

## Testing Approach

The project follows a QA-focused approach by validating AI-generated output instead of directly trusting the AI response.

Generated test cases are checked for:

- Completeness
- Required fields
- Duplicate IDs
- Duplicate scenarios
- Correct test categories
- Valid test steps
- Expected results

This ensures that the AI-generated output goes through a validation layer before being exported.

## Author

Sakshi Jamanani