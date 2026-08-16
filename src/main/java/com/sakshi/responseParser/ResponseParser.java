package com.sakshi.responseParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakshi.model.AIResponse;

public class ResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AIResponse parse(String response) {

        try {

            if (response == null || response.isBlank()) {
                System.out.println("ERROR: AI returned an empty response.");
                return null;
            }

            JsonNode root = objectMapper.readTree(response);

            JsonNode choices = root.get("choices");

            if (choices == null || !choices.isArray() || choices.isEmpty()) {

                System.out.println("ERROR: 'choices' is missing from AI response.");

                if (root.has("error")) {
                    System.out.println("OpenRouter error:");
                    System.out.println(root.get("error").toPrettyString());
                }

                return null;
            }

            JsonNode message = choices.get(0).get("message");

            if (message == null) {
                System.out.println("ERROR: 'message' is missing from AI response.");
                return null;
            }

            JsonNode content = message.get("content");

            if (content == null || content.isNull()) {
                System.out.println("ERROR: 'content' is missing from AI response.");
                return null;
            }

            String jsonContent = content.asText();

            return objectMapper.readValue(jsonContent, AIResponse.class);

        } catch (Exception e) {

            System.out.println("Failed to parse AI response.");
            System.out.println("Reason: " + e.getMessage());

            return null;
        }
    }
}