package com.sakshi.service;

import com.sakshi.model.AIResponse;

import java.nio.file.Path;

/** Result returned by the shared CLI and GUI generation workflow. */
public record GenerationResult(
        boolean success,
        AIResponse response,
        String json,
        String csv,
        Path jsonPath,
        Path csvPath,
        String message
) {
    public static GenerationResult failure(String message) {
        return new GenerationResult(false, null, null, null, null, null, message);
    }

    public static GenerationResult success(
            AIResponse response,
            String json,
            String csv,
            Path jsonPath,
            Path csvPath,
            String message) {
        return new GenerationResult(true, response, json, csv, jsonPath, csvPath, message);
    }
}
