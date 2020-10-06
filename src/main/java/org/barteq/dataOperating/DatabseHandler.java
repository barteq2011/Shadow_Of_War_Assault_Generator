package org.barteq.dataOperating;

import org.barteq.alerting.Alerts;
import org.barteq.entities.Assault;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Class that will handle database file and user save file
public class DatabseHandler {
    private static final DatabseHandler instance = new DatabseHandler();
    // Name and path to database with fortesses
    private static final String DATABASE_NAME = "fortesses.csv";
    private final URL DATABASE_STRING = getClass().getResource(DATABASE_NAME);
    // Name and path to user save file
    private static final String SAVE_FILE_NAME = "save.csv";
    private static final String SAVE_FILE = System.getProperty("user.home") + "\\Documents\\" + SAVE_FILE_NAME;


    public static DatabseHandler getInstance() {
        return instance;
    }


    public String[] queryFortesses() {
        try {
            Path path = Paths.get(DATABASE_STRING.toURI());
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String row;
                // Split rows by ";", because of csv format
                if ((row = reader.readLine()) != null) {
                    return row.split(";");
                }
            } catch (IOException e) {
                Alerts.showError("Error connecting to database");
                System.out.println(e.getMessage());
            }
        } catch (URISyntaxException e) {
            Alerts.showError("Wrong URI syntax");
        }
        return null;
    }

    public Assault getCurrentAssault() {
        // If user save file not exists, create it
        createFileIfNotExists();
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            // There is only one row in the file, so load it and split by ";" (because of csv format)
            String row = reader.readLine();
            if(row!=null) {
                String[] save = row.split(";");
                // First cell is the actual fortess name, second is the time to assault
                String fortessName = save[0];
                int assaultTime = Integer.parseInt(save[1]);
                // Return new Assault instance
                return Assault.of(fortessName, assaultTime);
            }
        } catch (IOException e) {
            Alerts.showError("Cannot get current Assault from save file.");
        }
        return null;
    }
    public void saveCurrentAssault(Assault assault) {
        // Create user save file if not exists
        createFileIfNotExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE, false))) {
            // Save progress (NAME_OF_CURRENT_FORTESS;TIME_UNTIL_ASSAULT)
            writer.write(assault.getFortess()+";"+assault.getTimeRemaining());
        } catch (IOException e) {
            Alerts.showError("Error saving current assault to save file.");
        }
    }
    public void createFileIfNotExists() {
        Path saveFilePath = Paths.get(SAVE_FILE);
        // If file not exists, create one
        if(!Files.exists(saveFilePath)) {
            try {
                Files.createFile(saveFilePath);
            } catch (IOException e) {
                Alerts.showError("Error creating new SaveFile.");
            }
        }
    }
}
