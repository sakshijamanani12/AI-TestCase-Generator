package com.sakshi.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void shouldSaveContentToFile() throws Exception {

        String fileName = "test_output.txt";
        String content = "This is a test file.";

        FileUtil.saveToFile(content, fileName);

        Path filePath = Path.of("output", fileName);

        assertTrue(Files.exists(filePath));

        String savedContent = Files.readString(filePath);

        assertEquals(content, savedContent);

        Files.deleteIfExists(filePath);
    }

    @Test
    void shouldOverwriteExistingFile() throws Exception {

        String fileName = "test_overwrite.txt";

        String firstContent = "First content";
        String secondContent = "Second content";

        // Create the file with initial content
        FileUtil.saveToFile(firstContent, fileName);

        // Save new content to the same file
        FileUtil.saveToFile(secondContent, fileName);

        Path filePath = Path.of("output", fileName);

        assertTrue(Files.exists(filePath));

        String savedContent = Files.readString(filePath);

        assertEquals(secondContent, savedContent);

        Files.deleteIfExists(filePath);
    }
}