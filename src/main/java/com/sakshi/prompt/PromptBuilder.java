package com.sakshi.prompt;

import com.sakshi.model.UserStory;

public class PromptBuilder {

    public static String buildPrompt(UserStory userStory) {

        return """
You are a Senior QA Automation Engineer.

Generate comprehensive functional test cases for the following user story.

User Story Title:
%s

Description:
%s

Acceptance Criteria:
%s

Instructions:
- Generate Positive, Negative, Boundary and Edge test cases.
- Return ONLY valid JSON.
- Do not include markdown.
- Do not include explanations.
- Do not wrap the JSON inside code blocks.

The JSON format MUST be exactly:

{
  "testCases": {
    "positive": [
      {
        "id": "TC001",
        "scenario": "Verify successful login",
        "steps": [
          "Step 1",
          "Step 2"
        ],
        "expectedResult": "User is redirected to dashboard.",
        "type": "Positive"
      }
    ],
    "negative": [],
    "boundary": [],
    "edge": []
  }
}

Rules:
- Use "id" as the test case identifier.
- id must be unique.
- scenario should describe the test case.
- steps must always be an array of strings.
- expectedResult must be a single sentence.
- type must be one of:
  - Positive
  - Negative
  - Boundary
  - Edge

Return ONLY the JSON.
"""
                .formatted(
                        userStory.getTitle(),
                        userStory.getDescription(),
                        userStory.getAcceptanceCriteria()
                );
    }
}