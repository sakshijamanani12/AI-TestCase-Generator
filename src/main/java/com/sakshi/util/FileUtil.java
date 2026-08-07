package com.sakshi.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static void saveToFile(String content, String fileName){
        try {

            File directory = new File("output");

            if (!directory.exists()) {
                directory.mkdir();
            }
            FileWriter writer = new FileWriter("output/" + fileName);
            writer.write(content);
            writer.close();
            System.out.println("File saved successfully");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
