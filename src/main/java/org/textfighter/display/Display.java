package org.textfighter.display;

import org.textfighter.location.Location;
import org.textfighter.TextFighter;
import org.textfighter.display.*;

import java.util.ArrayList;

import java.io.*;

public class Display {

    public static final String PROMPT = " > ";

    public static final String[] colorNames = {"black", "red", "green", "yellow", "blue", "purple", "cyan", "white"};

    public static final String[] colorCodes = {"\u001B[30m", "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m", "\u001B[37m"};

    public static final String RESET = "\u001B[0m";

    public static String previousCommandString = "";

    public static String previousCommand = colorCodes[2];
    public static String error = colorCodes[1];
    public static String warning = colorCodes[3];
    public static String progress = colorCodes[4];
    public static String output = colorCodes[2];
    public static String prompt = colorCodes[2];

    public static ArrayList<UiTag> interfaceTags = new ArrayList<UiTag>();
    public static ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();
    public static UserInterface choiceInterface;

    public static ArrayList<UiTag> getInterfaceTags() { return interfaceTags; }
    public static ArrayList<UserInterface> getInterfaces() { return interfaces; }

    public static void displayError(String e) {
        // Used for displaying errors such as something could not be found
        System.err.println(error + "[Error] " + e + RESET);
    }

    public static void displayPackError(String e) {
        System.err.println(error + "[PackError] " + e + RESET);
    }

    public static void displayWarning(String e) {
        System.err.println(warning + "[Warning] " + e + RESET);
    }

    public static void displayOutputMessage(String e) {
        // Used for displaying messages that explain loading resource progess or something like that
        System.err.println(output + e + RESET);
    }

    public static void displayProgressMessage(String e) {
        // Used for displaying the output field in TextFighter class
        System.out.println(progress + "[Progress] " + e + RESET);
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

    public static void displayInterfaces(Location l) {
        l.filterPossibleChoices();
        for(UserInterface ui : l.getInterfaces()) {
            ui.parseInterface();
            System.out.println(ui.getParsedUI());
        }
    }

    public static void loadDesiredColors() {
        displayProgressMessage("Loading the display colors...");
        File displayColors = new File(TextFighter.configDir.getAbsolutePath() + "/displayColors");
        if(!displayColors.exists()) { return; }
        try (BufferedReader br = new BufferedReader(new FileReader(displayColors));) {
            String line;

            while ((line = br.readLine()) != null) {
                String key = "=";
                String value = "=";
                if(line.indexOf("=") != -1) {
                    key = line.substring(0,line.indexOf("="));
                    value = line.substring(line.indexOf("=")+1,line.length()).trim();
                }
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
                } else if(key.equals("warning")) {
                    for(int i=0; i<colorNames.length; i++) {
                        if(colorNames[i].equals(value)) {
                            warning = colorCodes[i];
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            displayError("Unable to read the colors in the displayColors (The file does exist).");
        }
    }
}
