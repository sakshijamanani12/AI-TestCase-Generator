package com.sakshi.validator;

import com.sakshi.model.AIResponse;
import com.sakshi.model.TestCaseGroup;

public class TestCaseQualityValidator {

    public static boolean validate(AIResponse aiResponse) {

        if(aiResponse==null || aiResponse.getTestCases()==null){

            System.out.println("Quality validation failed: Test cases are missing.");
            return false;

        }

        TestCaseGroup group=aiResponse.getTestCases();

        int positiveCount=group.getPositive()==null?0:group.getPositive().size();

        int negativeCount=group.getNegative()==null?0:group.getNegative().size();

        int boundaryCount=group.getBoundary()==null?0:group.getBoundary().size();

        int edgeCount=group.getEdge()==null?0:group.getEdge().size();

        int totalCount=positiveCount+negativeCount+boundaryCount+edgeCount;

        if (totalCount==0){
            System.out.println("Quality validation failed: No test cases generated.");
            return false;
        }

        int coveredCategories=0;

        if(positiveCount>0){
            coveredCategories++;
        }

        if(negativeCount>0){
            coveredCategories++;
        }

        if(boundaryCount>0){
            coveredCategories++;
        }

        if(edgeCount>0){
            coveredCategories++;
        }

        double coverPercentage=(coveredCategories/4.0)*100;

        System.out.println();
        System.out.println("========== TEST CASE QUALITY ==========");
        System.out.println("Total test cases  : "+totalCount);
        System.out.println("Positive          : "+positiveCount);
        System.out.println("Negative          : "+negativeCount);
        System.out.println("Boundary          : "+boundaryCount);
        System.out.println("Edge              : "+edgeCount);
        System.out.println("----------------------------------------");
        System.out.println("Covered Categories: "+String.format("%.0f",coverPercentage)+"%");
        System.out.println("Quality           : " + (coveredCategories == 4 ? "PASSED" : "FAILED"));
        System.out.println("=========================================");

        return coveredCategories==4;

    }

}
