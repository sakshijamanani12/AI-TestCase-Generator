package com.sakshi.exporter;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvExporterTest {

    @Test
    void shouldCreateCsvFile() throws IOException {

        //Create testcase
        GeneratedTestCase testCase = new GeneratedTestCase();

        testCase.setId("TC001");
        testCase.setScenario("Verify successful login");
        testCase.setSteps(List.of(
                "Open login page",
                "Enter valid credentials",
                "Click login"
        ));
        testCase.setExpectedResult("User is redirected to dashboard.");
        testCase.setType("Positive");

        //Create testcase group
        TestCaseGroup group=new TestCaseGroup();

        group.setPositive(List.of(testCase));
        group.setNegative(List.of());
        group.setBoundary(List.of());
        group.setEdge(List.of());


        //Create AI response
        AIResponse aiResponse = new AIResponse();
        aiResponse.setTestCases(group);

        //Export CSV
        String fileName="test_cases_test.csv";

        CsvExporter.export(aiResponse,fileName);

        //Verify file exists
        Path filePath=Path.of("output",fileName);

        Assertions.assertTrue(Files.exists(filePath),"CSV file should be created");

        //Cleanup
        Files.deleteIfExists(filePath);

    }

    @Test
    void shouldWriteCorrectCsvContent() throws IOException {

        GeneratedTestCase testCase = new GeneratedTestCase();

        testCase.setId("TC001");
        testCase.setScenario("Verify successful login");
        testCase.setSteps(List.of(
                "Open login page",
                "Enter valid credentials",
                "Click login"
        ));
        testCase.setExpectedResult("User is redirected to dashboard.");
        testCase.setType("Positive");

        TestCaseGroup group=new TestCaseGroup();

        group.setPositive(List.of(testCase));
        group.setNegative(List.of());
        group.setBoundary(List.of());
        group.setEdge(List.of());

        AIResponse aiResponse = new AIResponse();
        aiResponse.setTestCases(group);

        String fileName="csv_content_test.csv";

        CsvExporter.export(aiResponse,fileName);
        Path filePath=Path.of("output",fileName);
        Assertions.assertTrue(Files.exists(filePath));

        String content=Files.readString(filePath);

        //Check header
        Assertions.assertTrue(content.contains("ID,Scenario,Steps,Expected Result,Type"));

        //Check test case data
        Assertions.assertTrue(content.contains("TC001"));
        Assertions.assertTrue(content.contains("Verify successful login"));
        Assertions.assertTrue(content.contains("Open login page"));
        Assertions.assertTrue(content.contains("Enter valid credentials"));
        Assertions.assertTrue(content.contains("Click login"));
        Assertions.assertTrue(content.contains("User is redirected to dashboard."));
        Assertions.assertTrue(content.contains("Positive"));

        Files.deleteIfExists(filePath);

    }

    @Test
    void shouldEscapeCommasAndQuotesCorrectly() throws IOException {

        GeneratedTestCase testCase = new GeneratedTestCase();

        testCase.setId("TC001");
        testCase.setScenario("Verify successful login, with valid credentials");
        testCase.setSteps(List.of(
                "Open login page",
                "Enter valid credentials",
                "Click \"Login\" button"
        ));
        testCase.setExpectedResult(
                "User is redirected to \"dashboard\"."
        );

        testCase.setType("Positive");

        TestCaseGroup group = new TestCaseGroup();

        group.setPositive(List.of(testCase));
        group.setNegative(List.of());
        group.setBoundary(List.of());
        group.setEdge(List.of());

        AIResponse aiResponse = new AIResponse();
        aiResponse.setTestCases(group);

        String fileName = "csv_escape_test.csv";

        CsvExporter.export(aiResponse, fileName);

        Path filePath = Path.of("output", fileName);

        Assertions.assertTrue(Files.exists(filePath));

        String content = Files.readString(filePath);

        //Scenario
        Assertions.assertTrue(content.contains("\"Verify successful login, with valid credentials\""));

        //Quotes inside steps
        Assertions.assertTrue(content.contains("Click \"\"Login\"\" button"));

        //Quotes inside expected result
        Assertions.assertTrue(content.contains("User is redirected to \"\"dashboard\"\"."));

        Files.deleteIfExists(filePath);
    }
}
