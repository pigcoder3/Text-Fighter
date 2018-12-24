package org.textfighter;

import org.textfighter.location.Location;
import org.textfighter.TextFighter;

import java.io.*;

public class Display {

    public static final String PROMPT = " > ";

    public static final String[] colorNames = {"black", "red", "green", "yellow", "blue", "purple", "cyan", "white"};

    public static final String[] colorCodes = {"\u001B[30m", "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m", "\u001B[37m"};

    public static final String RESET = "\u001B[0m";

    public static String previousCommandString = "";

    public static String previousCommand = colorCodes[2];
    public static String error = colorCodes[1];
    public static String progress = colorCodes[2];
    public static String output = colorCodes[3];
    public static String prompt = colorCodes[2];

    public static void displayErrorWarning(String e) {
        // Used for displaying errors such as something could not be found
        System.err.println(error + e + RESET);
    }

    public static void displayOutputMessage(String e) {
        // Used for displaying messages that explain loading resource progess or something like that
        System.err.println(progress + e + RESET);
    }

    public static void displayProgressMessage(String e) {
        // Used for displaying the output field in TextFighter class
        System.out.println(output + e + RESET);
    }

    public static void displayPreviousCommand() {
        //Displays the command the user previously inputted
        System.out.println(previousCommand + previousCommandString + RESET);
    }

    public static void promptUser() {
        System.out.print(prompt + PROMPT + RESET);
    }

    public static void resetColors() {
        System.out.println(RESET);
        System.err.println(RESET);
    }

    public static void displayInterface(Location i) {
        i.filterPossibleChoices();
        i.parseInterface();
        System.out.println(i.getParsedUI());
    }

    public static void loadDesiredColors() {
        File displayColors = new File(TextFighter.configDir.getAbsolutePath() + "/displayColors");
        if(!displayColors.exists()) {
            displayErrorWarning("Could not find the displayColors config file.\nCreating a new one.");
            try { displayColors.createNewFile(); } catch (IOException e) { Display.displayErrorWarning("Could not create a new displayColors config file!"); }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(displayColors));) {
            String line;

            while ((line = br.readLine()) != null) {
                String key = line.substring(0,line.indexOf(" "));
                String value = line.substring(line.indexOf(" "),line.length()-1).trim();
                if(key.equals("error")) {
                    for(int i=0; i<colorNames.length; i++) {
                        if(colorNames[i].equals(value)) {
                            error = colorCodes[i];
                        }
                    }
                } else if(key.equals("progress")) {
                    for(int i=0; i<colorNames.length; i++) {
                        if(colorNames[i].equals(value)) {
                            progress = colorCodes[i];
                        }
                    }
                } else if(key.equals("output")) {
                    for(int i=0; i<colorNames.length; i++) {
                        if(colorNames[i].equals(value)) {
                            output = colorCodes[i];
                        }
                    }
                } else if(key.equals("prompt")) {
                    for(int i=0; i<colorNames.length; i++) {
                        if(colorNames[i].equals(value)) {
                            prompt = colorCodes[i];
                        }
                    }
                } else if(key.equals("previousCommand")) {
                    for(int i=0; i<colorNames.length; i++) {
                        if(colorNames[i].equals(value)) {
                            previousCommand = colorCodes[i];
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            displayErrorWarning("Unable to read the colors in the displayColors");
        }
    }
}
