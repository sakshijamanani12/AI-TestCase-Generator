package com.sakshi.responseParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakshi.model.AIResponse;

public class ResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AIResponse parse(String response) {

        try {

            // Parse the OpenRouter response
            JsonNode root = objectMapper.readTree(response);

            // Get choices[0].message.content
            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            System.out.println("============== AI CONTENT ==============");
            System.out.println(content);
            System.out.println("========================================");

            // Convert the AI JSON into AIResponse
            return objectMapper.readValue(content, AIResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}