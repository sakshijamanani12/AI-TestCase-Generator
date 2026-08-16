package com.sakshi.validator;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestCaseValidatorTest {

    private GeneratedTestCase createTestCase(String id, String scenario, String type) {

        GeneratedTestCase testCase = new GeneratedTestCase();

        testCase.setId(id);
        testCase.setScenario(scenario);

        testCase.setSteps(List.of("Open login page", "Enter credentials", "Click login"));

        testCase.setExpectedResult("User is redirected to dashboard");

        testCase.setType(type);

        return testCase;
    }

    @Test
    void shouldPassForValidTestCases() {

        GeneratedTestCase positive = createTestCase("TC001", "Login with valid credentials", "Positive");
        GeneratedTestCase negative = createTestCase("TC002", "Login with invalid password", "Negative");
        GeneratedTestCase boundary = createTestCase("TC003", "Login with maximum password length", "Boundary");
        GeneratedTestCase edge = createTestCase("TC004", "Login with Unicode characters", "Edge");

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();

        response.setTestCases(group);

        boolean result = TestCaseValidator.validate(response);

        assertTrue(result);
    }

    @Test
    void shouldFailWhenTestCaseIdIsMissing() {

        GeneratedTestCase positive = createTestCase("", "Login with valid credentials", "Positive");
        GeneratedTestCase negative = createTestCase("TC002", "Login with invalid password", "Negative");
        GeneratedTestCase boundary = createTestCase("TC003", "Login with maximum password length", "Boundary");
        GeneratedTestCase edge = createTestCase("TC004", "Login with Unicode characters", "Edge");

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();

        response.setTestCases(group);

        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

    @Test
    void shouldFailForDuplicateTestCaseId(){
        GeneratedTestCase positive = createTestCase("TC001", "Login with valid credentials", "Positive");
        GeneratedTestCase negative = createTestCase("TC001", "Login with invalid password", "Negative");
        GeneratedTestCase boundary = createTestCase("TC003", "Login with maximum password length", "Boundary");
        GeneratedTestCase edge = createTestCase("TC004", "Login with unicode characters", "Edge");

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();
        response.setTestCases(group);
        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

    @Test
    void shoudFailForDuplicateScenario(){
        GeneratedTestCase positive = createTestCase("TC001", "Login with valid credentials", "Positive");
        GeneratedTestCase negative = createTestCase("TC002", "Login with valid credentials", "Negative");
        GeneratedTestCase boundary = createTestCase("TC003", "Login with maximum password length", "Boundary");
        GeneratedTestCase edge = createTestCase("TC004", "Login with unicode characters", "Edge");

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();
        response.setTestCases(group);
        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

    @Test
    void shouldFailWhenStepsAreMissing(){
        GeneratedTestCase positive = createTestCase("TC001", "Login with valid credentials", "Positive");
        positive.setSteps(List.of());
        GeneratedTestCase negative = createTestCase("TC002", "Login with invalid password", "Negative");
        GeneratedTestCase boundary = createTestCase("TC003", "Login with maximum password length", "Boundary");
        GeneratedTestCase edge = createTestCase("TC004", "Login with unicode characters", "Edge");

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();
        response.setTestCases(group);
        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

    @Test
    void shouldFailWhenExpectedResultIsMissing() {

        GeneratedTestCase positive =
                createTestCase(
                        "TC001",
                        "Login with valid credentials",
                        "Positive"
                );

        // Remove expected result
        positive.setExpectedResult("");

        GeneratedTestCase negative =
                createTestCase(
                        "TC002",
                        "Login with invalid password",
                        "Negative"
                );

        GeneratedTestCase boundary =
                createTestCase(
                        "TC003",
                        "Login with maximum password length",
                        "Boundary"
                );

        GeneratedTestCase edge =
                createTestCase(
                        "TC004",
                        "Login with Unicode characters",
                        "Edge"
                );

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();
        response.setTestCases(group);

        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

    @Test
    void shouldFailWhenCategoryAndTypeDoNotMatch() {

        GeneratedTestCase positive =
                createTestCase(
                        "TC001",
                        "Login with valid credentials",
                        "Negative"
                );

        GeneratedTestCase negative =
                createTestCase(
                        "TC002",
                        "Login with invalid password",
                        "Negative"
                );

        GeneratedTestCase boundary =
                createTestCase(
                        "TC003",
                        "Login with maximum password length",
                        "Boundary"
                );

        GeneratedTestCase edge =
                createTestCase(
                        "TC004",
                        "Login with Unicode characters",
                        "Edge"
                );

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response = new AIResponse();
        response.setTestCases(group);

        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

    @Test
    void shouldFailWhenCategoryIsMissing() {

        GeneratedTestCase positive =
                createTestCase(
                        "TC001",
                        "Login with valid credentials",
                        "Positive"
                );

        GeneratedTestCase negative =
                createTestCase(
                        "TC002",
                        "Login with invalid password",
                        "Negative"
                );

        GeneratedTestCase boundary =
                createTestCase(
                        "TC003",
                        "Login with maximum password length",
                        "Boundary"
                );

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));

        // Edge category is deliberately not set

        AIResponse response = new AIResponse();
        response.setTestCases(group);

        boolean result = TestCaseValidator.validate(response);

        assertFalse(result);
    }

}