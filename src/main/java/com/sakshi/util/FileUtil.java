package com.sakshi.util;

import com.sakshi.config.AppConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static void saveToFile(String content, String fileName) {
        try {
            saveToFileOrThrow(content, fileName);
            System.out.println("File saved successfully");
        } catch (IOException e) {
            System.err.println("Unable to save file: " + e.getMessage());
        }
    }

    public static Path saveToFileOrThrow(String content, String fileName) throws IOException {
        String outputDir = AppConfig.getOutputDir();
        if (outputDir == null || outputDir.isBlank()) {
            outputDir = "output";
        }

        Path directory = Path.of(outputDir);
        Files.createDirectories(directory);
        Path output = directory.resolve(fileName).normalize();
        if (!output.getParent().equals(directory.toAbsolutePath().normalize())
                && !output.getParent().equals(directory.normalize())) {
            throw new IOException("Invalid output filename: " + fileName);
        }
        Files.writeString(output, content, StandardCharsets.UTF_8);
        return output.toAbsolutePath();
    }

}