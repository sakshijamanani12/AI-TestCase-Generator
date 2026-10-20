package com.sakshi.exporter;

import com.sakshi.model.AIResponse;
import com.sakshi.model.GeneratedTestCase;
import com.sakshi.model.TestCaseGroup;
import com.sakshi.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CsvExporter {
    private static final String HEADER = "ID,Scenario,Steps,Expected Result,Type\n";

    private CsvExporter() {
    }

    public static void export(AIResponse aiResponse, String fileName) throws IOException {
        FileUtil.saveToFileOrThrow(toCsvString(aiResponse), fileName);
    }

    public static String toCsvString(AIResponse aiResponse) {
        StringBuilder csv = new StringBuilder(HEADER);
        for (String[] row : toRows(aiResponse)) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                    csv.append(',');
                }
                csv.append(escapeCsvField(row[i]));
            }
            csv.append('\n');
        }
        return csv.toString();
    }

    /** Returns unescaped table values for Swing and other previews. */
    public static List<String[]> toRows(AIResponse aiResponse) {
        List<String[]> rows = new ArrayList<>();
        if (aiResponse == null || aiResponse.getTestCases() == null) {
            return rows;
        }

        TestCaseGroup group = aiResponse.getTestCases();
        addRows(rows, group.getPositive());
        addRows(rows, group.getNegative());
        addRows(rows, group.getBoundary());
        addRows(rows, group.getEdge());
        return rows;
    }

    private static void addRows(List<String[]> rows, List<GeneratedTestCase> testCases) {
        if (testCases == null) {
            return;
        }
        for (GeneratedTestCase testCase : testCases) {
            rows.add(new String[]{
                    safe(testCase.getId()),
                    safe(testCase.getScenario()),
                    String.join(" | ", testCase.getSteps() == null ? List.of() : testCase.getSteps()),
                    safe(testCase.getExpectedResult()),
                    safe(testCase.getType())
            });
        }
    }

    public static String escapeCsvField(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
