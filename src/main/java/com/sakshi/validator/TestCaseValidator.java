package com.sakshi.validator;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestCaseValidator {

    public static boolean validate(AIResponse aiResponse) {

        if (aiResponse == null) {
            System.out.println("Validation failed: AI response is null.");
            return false;
        }

        TestCaseGroup testCaseGroup = aiResponse.getTestCases();

        if (testCaseGroup == null) {
            System.out.println("Validation failed: Test cases is null.");
            return false;
        }

        // Store all IDs so that duplicates are detected
        Set<String> testCaseIds = new HashSet<>();
        Set<String> scenarios=new HashSet<>();

        if (!validateCategory(
                "Positive",
                testCaseGroup.getPositive(),
                testCaseIds,scenarios)) {
            return false;
        }

        if (!validateCategory(
                "Negative",
                testCaseGroup.getNegative(),
                testCaseIds,scenarios)) {
            return false;
        }

        if (!validateCategory(
                "Boundary",
                testCaseGroup.getBoundary(),
                testCaseIds,scenarios)) {
            return false;
        }

        if (!validateCategory(
                "Edge",
                testCaseGroup.getEdge(),
                testCaseIds,scenarios)) {
            return false;
        }

        int positiveCount=testCaseGroup.getPositive().size();
        int negativeCount=testCaseGroup.getNegative().size();
        int boundaryCount=testCaseGroup.getBoundary().size();
        int edgeCount=testCaseGroup.getEdge().size();

        int totalCount=positiveCount+negativeCount+boundaryCount+edgeCount;

        System.out.println();
        System.out.println("========== TEST CASE SUMMARY ==========");
        System.out.println();
        System.out.println("Positive : "+positiveCount);
        System.out.println("Negative : "+negativeCount);
        System.out.println("Boundary : "+boundaryCount);
        System.out.println("Edge     : "+edgeCount);
        System.out.println("----------------------------------------");
        System.out.println("Total    : "+totalCount);
        System.out.println();
        System.out.println("Unique IDs: PASSED");
        System.out.println("Duplicate Scenarios: NONE");
        System.out.println("Category Validation: PASSED");
        System.out.println("========================================");

        System.out.println();
        System.out.println("Test case validation successful");

        return true;
    }

    private static boolean validateCategory(
            String category,
            List<GeneratedTestCase> testCases,
            Set<String> testCaseIds,
            Set<String> scenarios) {

        if (testCases == null || testCases.isEmpty()) {
            System.out.println(
                    "Validation failed: No " + category + " test cases found."
            );
            return false;
        }

        for (GeneratedTestCase testCase : testCases) {

            if (testCase == null) {
                System.out.println(
                        "Validation failed: Null test case found in "
                                + category + " category."
                );
                return false;
            }

            if (testCase.getId() == null ||
                    testCase.getId().isBlank()) {

                System.out.println(
                        "Validation failed: Test case ID is missing."
                );
                return false;
            }

            // Check for duplicate ID
            if (!testCaseIds.add(testCase.getId())) {

                System.out.println(
                        "Validation failed: Duplicate test case ID: "
                                + testCase.getId()
                );
                return false;
            }

            //Validate scenario
            if (testCase.getScenario() == null ||
                    testCase.getScenario().isBlank()) {

                System.out.println(
                        "Validation failed: Scenario is missing for "
                                + testCase.getId()
                );
                return false;
            }

            //Check duplicate scenario
            String normalizedScenario=testCase.getScenario().trim().toLowerCase();

            if(!scenarios.add(normalizedScenario)){
                System.out.println("Validation failed: Duplicate scenario found: "+testCase.getScenario());
                return false;
            }

            //Validate steps
            if (testCase.getSteps() == null ||
                    testCase.getSteps().isEmpty()) {

                System.out.println(
                        "Validation failed: Steps are missing for "
                                + testCase.getId()
                );
                return false;
            }

            //Validate expected result
            if (testCase.getExpectedResult() == null ||
                    testCase.getExpectedResult().isBlank()) {

                System.out.println(
                        "Validation failed: Expected result is missing for "
                                + testCase.getId()
                );
                return false;
            }

            //Validate type
            if (testCase.getType() == null ||
                    testCase.getType().isBlank()) {

                System.out.println(
                        "Validation failed: Type is missing for "
                                + testCase.getId()
                );
                return false;
            }

            //Validate category and type match
            if(!category.equalsIgnoreCase(testCase.getType().trim())){
                System.out.println("Validation failed: Test case "+ testCase.getId() + " is in " + category+" category but has type "+ testCase.getType());
                return false;
            }
        }

        return true;

    }
}