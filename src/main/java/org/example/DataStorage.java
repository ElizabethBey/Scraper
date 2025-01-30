package org.example;

import java.io.FileWriter;
import java.io.IOException;

public class DataStorage {
    private String fileName = "./posts.json";
    public DataStorage() {}

    public DataStorage(String fileName) {
        this.fileName = "./links.txt";
    }

    public void saveData(String message) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
