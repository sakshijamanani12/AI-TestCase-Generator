package com.sakshi.validator;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestCaseQualityValidatorTest {

    private GeneratedTestCase createTestCase(String id, String scenario, String type){

        GeneratedTestCase testCase=new GeneratedTestCase();
        testCase.setId(id);
        testCase.setScenario(scenario);
        testCase.setSteps(List.of(
                "Open login page",
                "Enter credentials",
                "Click login"
        ));

        testCase.setExpectedResult("User is redirected to dashboard");
        testCase.setType(type);
        return testCase;
    }

    @Test
    void shouldPassWhenAllCategoriesAreCovered(){
        GeneratedTestCase positive=createTestCase("TC001", "Valid login", "Positive");
        GeneratedTestCase negative=createTestCase("TC002", "Invalid login", "Negative");
        GeneratedTestCase boundary=createTestCase("TC003", "Maximum password length","Boundary");
        GeneratedTestCase edge=createTestCase("TC004", "Unicode characters","Edge");

        TestCaseGroup group=new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of(boundary));
        group.setEdge(List.of(edge));

        AIResponse response=new AIResponse();
        response.setTestCases(group);

        Assertions.assertTrue(TestCaseQualityValidator.validate(response));

    }

    @Test
    void shouldFailWhenCategoryIsMissing(){

        GeneratedTestCase positive=createTestCase("TC001", "Valid login", "Positive");
        GeneratedTestCase negative=createTestCase("TC002", "Invalid login", "Negative");
        GeneratedTestCase edge=createTestCase("TC003", "Unicode characters","Edge");

        TestCaseGroup group=new TestCaseGroup();

        group.setPositive(List.of(positive));
        group.setNegative(List.of(negative));
        group.setBoundary(List.of());
        group.setEdge(List.of(edge));

        AIResponse response=new AIResponse();
        response.setTestCases(group);

        Assertions.assertFalse(TestCaseQualityValidator.validate(response));

    }

    @Test
    void shouldFailWhenNoTestCasesAreGenerated(){

        TestCaseGroup group=new TestCaseGroup();

        group.setPositive(List.of());
        group.setNegative(List.of());
        group.setBoundary(List.of());
        group.setEdge(List.of());

        AIResponse response=new AIResponse();
        response.setTestCases(group);

        Assertions.assertFalse(TestCaseQualityValidator.validate(response));

    }

}
