package com.sakshi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakshi.model.AIResponse;
import com.sakshi.model.UserStory;
import com.sakshi.prompt.PromptBuilder;
import com.sakshi.responseParser.ResponseParser;
import com.sakshi.service.AIService;
import com.sakshi.util.FileUtil;

public class Main {

    public static void main(String[] args) {

        try {

            System.out.println("==================================");
            System.out.println(" AI Powered Test Case Generator");
            System.out.println("==================================");

            UserStory userStory = new UserStory(
                    "Login Feature",
                    "As a registered user, I should be able to login using email and password.",
                    "User should be redirected to dashboard after successful login."
            );

            String prompt = PromptBuilder.buildPrompt(userStory);

            AIService aiService = new AIService();
            String response = aiService.generateResponse(prompt);

            AIResponse aiResponse = ResponseParser.parse(response);

            if (aiResponse == null) {
                System.out.println("Failed to parse AI response.");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();

            String jsonOutput = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(aiResponse);

            FileUtil.saveToFile(jsonOutput, "generated_test_cases.json");

            System.out.println("\nTest cases generated successfully!");
            System.out.println("Output saved to output/generated_test_cases.json");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}