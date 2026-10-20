package com.sakshi;

import com.sakshi.printer.TestCasePrinter;
import com.sakshi.service.GenerationResult;
import com.sakshi.service.GenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/** Command-line entry point for the test case generator. */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting AI Powered Test Case Generator");

        String filePath = args.length > 0 ? args[0] : readFilePath();
        if (filePath == null || filePath.isBlank()) {
            System.out.println("ERROR: User story file path cannot be empty.");
            return;
        }

        GenerationResult result = GenerationService.generate(filePath.trim());
        if (!result.success()) {
            logger.error("Generation failed: {}", result.message());
            System.out.println("ERROR: " + result.message());
            return;
        }

        TestCasePrinter.print(result.response());
        System.out.println("\n" + result.message());
        System.out.println("JSON output: " + result.jsonPath());
        System.out.println("CSV output: " + result.csvPath());
    }

    private static String readFilePath() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter user story file path: ");
            return scanner.nextLine();
        }
    }
}
