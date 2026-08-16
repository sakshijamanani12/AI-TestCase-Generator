package com.sakshi.util;

public class InputValidator {

    public static boolean isValid(String[] userStoryData) {

        if (userStoryData == null) {
            System.out.println("ERROR: Could not read user story file.");
            return false;
        }

        if (userStoryData.length < 3) {
            System.out.println("ERROR: User story data is incomplete.");
            return false;
        }

        if (userStoryData[0] == null || userStoryData[0].isBlank()) {
            System.out.println("ERROR: Feature is missing.");
            return false;
        }

        if (userStoryData[1] == null || userStoryData[1].isBlank()) {
            System.out.println("ERROR: User story is missing.");
            return false;
        }

        if (userStoryData[2] == null || userStoryData[2].isBlank()) {
            System.out.println("ERROR: Expected behavior is missing.");
            return false;
        }

        return true;
    }
}