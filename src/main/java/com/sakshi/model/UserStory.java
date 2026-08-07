package com.sakshi.model;

public class UserStory {

    private String title;
    private String description;
    private String acceptanceCriteria;

    public UserStory(String title, String description, String acceptanceCriteria) {
        this.title = title;
        this.description = description;
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", acceptanceCriteria='" + acceptanceCriteria + '\'' +
                '}';
    }
}