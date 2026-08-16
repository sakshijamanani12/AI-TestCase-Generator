package com.sakshi.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class UserStoryReader {

    public static String[] readUserStory(String filePath) {

        try {

            //1. Check if file exists
            Path path=Path.of(filePath);

            if(!Files.exists(path)){
                System.out.println("ERROR: User story file not found: "+filePath);
                return null;
            }

            //2. Read file
            String content=Files.readString(path).trim();

            //3. Chek if file s empty
            if (content.isEmpty()){
                System.out.println("ERROR: User story file is empty.");
                return null;
            }

            String feature = "";
            String userStory = "";
            String expectedBehavior = "";

            String[] lines = content.split("\\R");

            String currentSection = "";

            for (String line : lines) {

                line = line.trim();

                if (line.startsWith("Feature:")) {

                    feature = line.substring("Feature:".length()).trim();
                    currentSection = "feature";

                } else if (line.equals("User Story:")) {

                    currentSection = "userStory";

                } else if (line.equals("Expected Behavior:")) {

                    currentSection = "expectedBehavior";

                } else if (!line.isEmpty()) {

                    if (currentSection.equals("userStory")) {

                        userStory += line + " ";

                    } else if (currentSection.equals("expectedBehavior")) {

                        expectedBehavior += line + " ";
                    }
                }
            }

            //4. Validate required sections
            if (feature.isBlank()){
                System.out.println("ERROR: Feature is missing from user story.");
                return null;
            }

            if (userStory.isBlank()){
                System.out.println("ERROR: User Story is missing.");
                return null;
            }

            if (expectedBehavior.isBlank()){
                System.out.println("ERROR: Expected Behavior is missing.");
                return null;
            }

            return new String[]{
                    feature,
                    userStory.trim(),
                    expectedBehavior.trim()
            };

        } catch (Exception e) {
            System.out.println("ERROR: Failed to read user story file.");
            System.out.println(e.getMessage());
            return null;
        }
    }
}