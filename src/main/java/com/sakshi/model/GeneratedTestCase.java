package com.sakshi.model;

import java.util.List;

public class GeneratedTestCase {

    private String id;
    private String scenario;
    private List<String> steps;
    private String expectedResult;
    private String type;

    public GeneratedTestCase() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GeneratedTestCase{" +
                "id='" + id + '\'' +
                ", scenario='" + scenario + '\'' +
                ", steps=" + steps +
                ", expectedResult='" + expectedResult + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}