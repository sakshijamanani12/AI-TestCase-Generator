package com.sakshi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakshi.exporter.CsvExporter;
import com.sakshi.model.AIResponse;
import com.sakshi.model.UserStory;
import com.sakshi.prompt.PromptBuilder;
import com.sakshi.responseParser.ResponseParser;
import com.sakshi.util.FileUtil;
import com.sakshi.util.InputValidator;
import com.sakshi.util.UserStoryReader;
import com.sakshi.validator.TestCaseQualityValidator;
import com.sakshi.validator.TestCaseValidator;

import java.io.IOException;
import java.nio.file.Path;

/** Coordinates input, AI generation, validation, and persistence in one place. */
public final class GenerationService {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private GenerationService() {
    }

    public static GenerationResult generate(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return GenerationResult.failure("Select a user story file before generating test cases.");
        }

        try {
            String[] storyData = UserStoryReader.readUserStory(filePath);
            if (!InputValidator.isValid(storyData)) {
                return GenerationResult.failure("The user story must contain Feature, User Story, and Expected Behavior sections.");
            }
            GenerationResult generated = run(new UserStory(storyData[0], storyData[1], storyData[2]));
            if (!generated.success()) return generated;

            String baseName = baseName(filePath);
            Path jsonPath = FileUtil.saveToFileOrThrow(generated.json(), baseName + "_test_cases.json");
            Path csvPath = FileUtil.saveToFileOrThrow(generated.csv(), baseName + "_test_cases.csv");
            return new GenerationResult(true, generated.response(), generated.json(), generated.csv(), jsonPath, csvPath,
                    "Generated " + CsvExporter.toRows(generated.response()).size() + " test cases successfully.");
        } catch (IOException | RuntimeException ex) {
            return GenerationResult.failure(ex.getMessage() == null ? "The AI request or file operation failed." : ex.getMessage());
        }
    }

    public static GenerationResult generate(UserStory userStory) {
        if (userStory == null || userStory.getTitle() == null || userStory.getTitle().isBlank()
                || userStory.getDescription() == null || userStory.getDescription().isBlank()
                || userStory.getAcceptanceCriteria() == null || userStory.getAcceptanceCriteria().isBlank()) {
            return GenerationResult.failure("Feature, user story, and expected behavior are required.");
        }
        try {
            return run(userStory);
        } catch (IOException | RuntimeException ex) {
            return GenerationResult.failure(ex.getMessage() == null ? "The AI request failed." : ex.getMessage());
        }
    }

    private static GenerationResult run(UserStory userStory) throws IOException {
        String providerResponse = AIService.generateResponse(PromptBuilder.buildPrompt(userStory));
        AIResponse response = ResponseParser.parse(providerResponse);
        if (response == null) return GenerationResult.failure("The AI returned an empty or invalid response.");
        if (!TestCaseValidator.validate(response)) return GenerationResult.failure("The generated test cases failed structural validation.");
        if (!TestCaseQualityValidator.validate(response)) return GenerationResult.failure("The generated test cases did not cover all four categories.");

        String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        String csv = CsvExporter.toCsvString(response);
        return new GenerationResult(true, response, json, csv, null, null,
                "Generated " + CsvExporter.toRows(response).size() + " test cases successfully.");
    }

    private static String baseName(String filePath) {
        String fileName = Path.of(filePath).getFileName().toString();
        int extension = fileName.lastIndexOf('.');
        return extension > 0 ? fileName.substring(0, extension) : fileName;
    }
}
