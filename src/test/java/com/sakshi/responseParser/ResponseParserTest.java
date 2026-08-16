package com.sakshi.responseParser;

import com.sakshi.model.AIResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseParserTest {

    @Test
    void shouldParseValidAIResponse() {

        String aiContent = """
                {
                  "testCases": {
                    "positive": [
                      {
                        "id": "TC001",
                        "scenario": "Login with valid credentials",
                        "steps": [
                          "Open login page",
                          "Enter valid email",
                          "Enter valid password",
                          "Click login"
                        ],
                        "expectedResult": "User is redirected to dashboard",
                        "type": "Positive"
                      }
                    ],
                    "negative": [
                      {
                        "id": "TC002",
                        "scenario": "Login with invalid password",
                        "steps": [
                          "Open login page",
                          "Enter valid email",
                          "Enter invalid password",
                          "Click login"
                        ],
                        "expectedResult": "Error message is displayed",
                        "type": "Negative"
                      }
                    ],
                    "boundary": [
                      {
                        "id": "TC003",
                        "scenario": "Login with maximum password length",
                        "steps": [
                          "Open login page",
                          "Enter valid email",
                          "Enter maximum length password",
                          "Click login"
                        ],
                        "expectedResult": "User is redirected to dashboard",
                        "type": "Boundary"
                      }
                    ],
                    "edge": [
                      {
                        "id": "TC004",
                        "scenario": "Login with special characters",
                        "steps": [
                          "Open login page",
                          "Enter valid email",
                          "Enter password with special characters",
                          "Click login"
                        ],
                        "expectedResult": "User is redirected to dashboard",
                        "type": "Edge"
                      }
                    ]
                  }
                }
                """;

        // Escape the JSON so it can be placed inside "content"
        String escapedContent = aiContent
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        String response = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "%s"
                      }
                    }
                  ]
                }
                """.formatted(escapedContent);

        AIResponse result = ResponseParser.parse(response);

        assertNotNull(result);
        assertNotNull(result.getTestCases());

        assertNotNull(result.getTestCases().getPositive());
        assertNotNull(result.getTestCases().getNegative());
        assertNotNull(result.getTestCases().getBoundary());
        assertNotNull(result.getTestCases().getEdge());

        assertEquals("TC001", result.getTestCases().getPositive().get(0).getId());
    }

    @Test
    void shouldReturnNullWhenChoicesAreMissing() {

        String response = """
            {
              "error": {
                "message": "Upstream service unavailable",
                "code": 502
              }
            }
            """;

        AIResponse result = ResponseParser.parse(response);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenAIResponseIsEmpty() {

        String response = "";

        AIResponse result = ResponseParser.parse(response);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenAIResponseIsInvalidJson() {

        String response = """
            {
              "choices": [
                {
                  "message": {
                    "content": "This is not valid JSON
                  }
                }
              ]
            """;

        AIResponse result = ResponseParser.parse(response);

        assertNull(result);
    }

}