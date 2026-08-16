package com.sakshi.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserStoryReaderTest {

    @Test
    void shouldReadUserStoryCorrectly() throws Exception {

        String content = """
                Feature: Login

                User Story:
                As a registered user,
                I want to login using my credentials,
                So that I can access my account.

                Expected Behavior:
                User should be redirected to the dashboard after successful login.
                """;

        Path tempFile = Files.createTempFile("user_story", ".txt");
        Files.writeString(tempFile, content);

        String[] result =
                UserStoryReader.readUserStory(tempFile.toString());

        assertNotNull(result);

        assertEquals("Login", result[0]);

        assertTrue(result[1].contains("As a registered user"));
        assertTrue(result[1].contains("I want to login using my credentials"));
        assertTrue(result[2].contains("User should be redirected to the dashboard"));

        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldReturnNullWhenFileDoesNotExist() {

        String filePath = "input/file_that_does_not_exist.txt";

        String[] result = UserStoryReader.readUserStory(filePath);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenFileIsEmpty() throws Exception {

        Path tempFile = Files.createTempFile("empty_user_story", ".txt");

        Files.writeString(tempFile, "");

        String[] result =
                UserStoryReader.readUserStory(tempFile.toString());

        assertNull(result);

        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldReturnNullWhenFeatureIsMissing() throws Exception {

        String content = """
            User Story:
            As a registered user,
            I want to login using my credentials.

            Expected Behavior:
            User should be redirected to the dashboard.
            """;

        Path tempFile = Files.createTempFile("missing_feature", ".txt");
        Files.writeString(tempFile, content);

        String[] result =
                UserStoryReader.readUserStory(tempFile.toString());

        assertNull(result);

        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldReturnNullWhenUserStoryIsMissing() throws Exception {

        String content = """
            Feature: Login

            Expected Behavior:
            User should be redirected to the dashboard.
            """;

        Path tempFile = Files.createTempFile("missing_user_story", ".txt");
        Files.writeString(tempFile, content);

        String[] result =
                UserStoryReader.readUserStory(tempFile.toString());

        assertNull(result);

        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldReturnNullWhenExpectedBehaviorIsMissing() throws Exception {

        String content = """
            Feature: Login

            User Story:
            As a registered user,
            I want to login using my credentials.
            """;

        Path tempFile =
                Files.createTempFile("missing_expected_behavior", ".txt");

        Files.writeString(tempFile, content);

        String[] result =
                UserStoryReader.readUserStory(tempFile.toString());

        assertNull(result);

        Files.deleteIfExists(tempFile);
    }

}