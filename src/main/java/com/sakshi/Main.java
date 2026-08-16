package com.sakshi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakshi.exporter.CsvExporter;
import com.sakshi.model.AIResponse;
import com.sakshi.model.UserStory;
import com.sakshi.printer.TestCasePrinter;
import com.sakshi.prompt.PromptBuilder;
import com.sakshi.responseParser.ResponseParser;
import com.sakshi.service.AIService;
import com.sakshi.util.FileUtil;
import com.sakshi.util.UserStoryReader;
import com.sakshi.util.InputValidator;
import com.sakshi.validator.TestCaseValidator;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("API key loaded: " +
                (System.getenv("OPENROUTER_API_KEY") != null));

        try {

            System.out.println("==================================");
            System.out.println(" AI Powered Test Case Generator");
            System.out.println("==================================");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter user story file path: ");
            String filePath=scanner.nextLine().trim();

            if(filePath.isEmpty()){
                System.out.println("ERROR: User story file path cannot be empty.");
                return;
            }

            //Read user story
            String[] storyData=UserStoryReader.readUserStory(filePath);

            if(storyData==null){
                System.out.println("ERROR: Unable to read user story file.");
                return;
            }

            String feature=storyData[0];
            String userStoryText=storyData[1];
            String expectedBehavior=storyData[2];

            //Create UserStory
            UserStory userStory=new UserStory(feature,userStoryText,expectedBehavior);

            //Build prompt
            String prompt = PromptBuilder.buildPrompt(userStory);

            //Call AI
            AIService aiService = new AIService();
            String response = aiService.generateResponse(prompt);

            //Parse response
            AIResponse aiResponse = ResponseParser.parse(response);

            //Validate test cases
            boolean isValid= TestCaseValidator.validate(aiResponse);

            if(!isValid){
                System.out.println("Generated testcases are invalid");
                return;
            }

            //Print test cases
            TestCasePrinter.print(aiResponse);

            //Convert to JSON
            ObjectMapper mapper = new ObjectMapper();

            String jsonOutput = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(aiResponse);

            //Create dynamic output filename
            String fileName=java.nio.file.Path.of(filePath).getFileName().toString();

            String baseName=fileName;

            if (baseName.contains(".")){
                baseName=baseName.substring(0,baseName.lastIndexOf("."));
            }

            String jsonFileName=baseName+"_test_cases.json";
            String csvFileName=baseName+"_test_cases.csv";

            //Save JSON
            FileUtil.saveToFile(jsonOutput,jsonFileName);

            //Save CSV
            CsvExporter.export(aiResponse,csvFileName);

            System.out.println("\nTest cases generated successfully!");
            System.out.println("JSON output: "+jsonFileName);
            System.out.println("CSV output: "+csvFileName);

        } catch (Exception e) {
            System.out.println("Something went wrong!");
            System.out.println("Error: "+e.getMessage());
        }
    }
}