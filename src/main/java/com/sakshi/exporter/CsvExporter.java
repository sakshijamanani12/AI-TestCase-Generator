package com.sakshi.exporter;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class CsvExporter {

    public static void export(AIResponse aiResponse, String fileName) throws IOException{

        File outputDirectory=new File("output");

        if(!outputDirectory.exists()){
            outputDirectory.mkdirs();
        }

        File file=new File(outputDirectory,fileName);

        try (FileWriter writer=new FileWriter(file)){
            writer.append("ID,Scenario,Steps,Expected Result,Type\n");

            TestCaseGroup testCaseGroup=aiResponse.getTestCases();

            writeCategory(writer,testCaseGroup.getPositive());
            writeCategory(writer,testCaseGroup.getNegative());
            writeCategory(writer,testCaseGroup.getBoundary());
            writeCategory(writer,testCaseGroup.getEdge());

            System.out.println("CSV file saved successfully: "+file.getPath());

            }
        }

        private static void writeCategory(FileWriter writer, List<GeneratedTestCase> testCases) throws IOException{
            if(testCases==null){
                return;
            }

            for(GeneratedTestCase testCase:testCases){
                String id=escape(testCase.getId());
                String scenario=escape(testCase.getScenario());
                String steps=escape(String.join(" | ",testCase.getSteps()));
                String expectedResult=escape(testCase.getExpectedResult());
                String type=escape(testCase.getType());

                writer.append(id).append(",").append(scenario).append(",").append(steps).append(",").append(expectedResult).append(",").append(type).append("\n");
            }
        }

        private static String escape(String value){
            if(value==null){
                return "";
            }
            value=value.replace("\"","\"\"");
            return "\"" + value + "\"";
        }
}

