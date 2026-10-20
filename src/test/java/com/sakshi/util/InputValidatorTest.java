package com.sakshi.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputValidatorTest {

    @Test
    void shouldReturnTrueForValidUserStory(){

        String[] userStoryData={
                "Login",
                "User should be able to login with valid credentials",
                "User should be redirected to the dashboard"
        };

        Assertions.assertTrue(InputValidator.isValid(userStoryData));
    }

    @Test
    void shouldReturnFalseWhenUserStoryDataIsNull(){
        Assertions.assertFalse(InputValidator.isValid(null));
    }

    @Test
    void shouldReturnFalseWhenUserStoryDataIsIncomplete(){

        String[] userStoryData={
                "Login",
                "User should be able to login"
        };

        Assertions.assertFalse(InputValidator.isValid(userStoryData));
    }

    @Test
    void shouldReturnFalseWhenFeatureIsMissing(){

        String[] userStoryData={
                "",
                "User should be able to login",
                "User should reach dashboard"
        };

        Assertions.assertFalse(InputValidator.isValid(userStoryData));
    }

    @Test
    void shouldReturnFalseWhenUserStoryIsMissing(){

        String[] userStoryData={
                "Login",
                "",
                "User should reach dashboard"
        };

        Assertions.assertFalse(InputValidator.isValid(userStoryData));
    }

    @Test
    void shouldReturnFalseWhenExpectedBehaviorIsMissing(){
        String[] userStoryData={
                "Login",
                "User should be able to login",
                ""
        };

        Assertions.assertFalse(InputValidator.isValid(userStoryData));
    }
}
