package com.sakshi.model;

import java.util.List;

public class TestCaseGroup {

    private List<GeneratedTestCase> positive;
    private List<GeneratedTestCase> negative;
    private List<GeneratedTestCase> boundary;
    private List<GeneratedTestCase> edge;

    public TestCaseGroup() {
    }

    public List<GeneratedTestCase> getPositive() {
        return positive;
    }

    public void setPositive(List<GeneratedTestCase> positive) {
        this.positive = positive;
    }

    public List<GeneratedTestCase> getNegative() {
        return negative;
    }

    public void setNegative(List<GeneratedTestCase> negative) {
        this.negative = negative;
    }

    public List<GeneratedTestCase> getBoundary() {
        return boundary;
    }

    public void setBoundary(List<GeneratedTestCase> boundary) {
        this.boundary = boundary;
    }

    public List<GeneratedTestCase> getEdge() {
        return edge;
    }

    public void setEdge(List<GeneratedTestCase> edge) {
        this.edge = edge;
    }
}