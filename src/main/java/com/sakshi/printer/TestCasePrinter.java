package com.sakshi.printer;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;

import java.util.List;

public class TestCasePrinter {

    public static void print(AIResponse aiResponse){

        if(aiResponse==null || aiResponse.getTestCases() == null){
            System.out.println("No testcaes available.");
            return;
        }

        TestCaseGroup testCaseGroup=aiResponse.getTestCases();

        System.out.println();
        System.out.println("==============================");
        System.out.println("       GENERATED TEST CASES       ");
        System.out.println("==============================");

        printCategory("POSITIVE TEST CASES",testCaseGroup.getPositive());
        printCategory("NEGATIVE TEST CASES",testCaseGroup.getNegative());
        printCategory("BOUNDARY TEST CASES",testCaseGroup.getBoundary());
        printCategory("EDGE TEST CASES",testCaseGroup.getEdge());


    }

    public static void printCategory(String categoryName, List<GeneratedTestCase> testCases){
        System.out.println();
        System.out.println("----------" + categoryName + "----------");

        if(testCases==null || testCases.isEmpty()){
            System.out.println("No testcases available.");
            return;
        }

        for(GeneratedTestCase testCase:testCases){
            System.out.println();
            System.out.println("Testcase ID: "+testCase.getId());
            System.out.println("Test Scenario: " + testCase.getScenario());
            System.out.println("Test Steps: " + testCase.getSteps());

            if(testCase.getSteps()!=null){
                int stepNumber=1;
                for(String step: testCase.getSteps()){
                    System.out.println("  "+ stepNumber + ". " + step);
                    stepNumber++;
                }
            }

            System.out.println("Expected Result: " + testCase.getExpectedResult());
            System.out.println("Type: " + testCase.getType());

            System.out.println("----------------------------------------");

        }
    }

}
