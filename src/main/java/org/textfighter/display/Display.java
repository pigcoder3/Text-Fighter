package org.textfighter.display;

import org.textfighter.location.Location;
import org.textfighter.TextFighter;
import org.textfighter.display.*;

import java.util.ArrayList;

import java.io.*;

public class Display {

    public static String promptString = " > ";

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

    public static boolean ANSI = true;

    public static ArrayList<UiTag> interfaceTags = new ArrayList<UiTag>();
    public static ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();
    public static UserInterface choiceInterface;

    public static ArrayList<UiTag> getInterfaceTags() { return interfaceTags; }
    public static ArrayList<UserInterface> getInterfaces() { return interfaces; }

    public static void filterTags() {
        ArrayList<UiTag> tags = new ArrayList<UiTag>();
        for(int i=0; i<interfaceTags.size(); i++) {
            if(interfaceTags.get(i).getValid()) { tags.add(interfaceTags.get(i)); }
        }
        interfaceTags = tags;
    }

    public static void displayError(String e) {
        // Used for displaying errors such as something could not be found
        if(ANSI) {
            System.err.println(error + "[Error] " + e + RESET);
        } else {
            System.err.println("[Error] " + e);
        }
    }

    public static void displayPackError(String e) {
        // Displays errors that deal with packs
        if(TextFighter.testMode) {
            if(ANSI) {
                System.err.println(error + "[PackError] " + e + RESET);
            } else {
                System.err.println("[PackError] " + e);
            }
        }
    }

    public static void displayPackMessage(String e) {
        if(TextFighter.testMode) {
            if(ANSI) {
                System.err.println(progress + "[PackMessage] " + e + RESET);
            } else {
                System.err.println("[PackMessage] " + e);
            }
        }
    }

    public static void displayWarning(String e) {
        // Displays warnings
        if(ANSI) {
            System.err.println(warning + "[Warning] " + e + RESET);
        } else {
            System.err.println("[Warning] " + e);
        }
    }

    public static void displayOutputMessage(String e) {
        // Used for displaying messages that explain loading resource progess
        if(ANSI) {
            System.err.println(output + e + RESET);
        } else {
            System.err.println(e);
        }

    }

    public static void displayProgressMessage(String e) {
        // Used for displaying the output field in TextFighter class
        if(ANSI) {
            System.out.println(progress + "[Progress] " + e + RESET);
        } else {
            System.out.println("[Progress] " + e);
        }
    }

    public static void displayPreviousCommand() {
        //Displays the command the user previously inputted
        if(ANSI) {
            System.out.println(previousCommand + previousCommandString + RESET);
        } else {
            System.out.println(previousCommandString);
        }
    }

    public static void promptUser() {
        //Displays the prompt
        if(ANSI) {
            System.out.print(prompt + promptString + RESET);
        } else {
            System.out.print(promptString);
        }
    }

    public static void resetColors() {
        //Resets the colors for err and out
        if(ANSI) {
            System.out.println(RESET);
            System.err.println(RESET);
        }
    }

    public static void clearScreen() {
        //Puts the character back to the home and clears the screen
        if(ANSI) {
            System.out.println("\u001b[H \u001b[2J");
        }
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
        File displayColors = new File(TextFighter.configDir.getAbsolutePath() + "/display");
        if(!displayColors.exists()) { return; }
        try (BufferedReader br = new BufferedReader(new FileReader(displayColors));) {
            String line;
            boolean colorWarning = true;
            while ((line = br.readLine()) != null) {
                String key = "";
                String value = "";
                if(key == null && value == null) { continue; }
                if(line.indexOf("=") != -1) {
                    key = line.substring(0,line.indexOf("="));
                    value = line.substring(line.indexOf("=")+1,line.length()).trim();
                } else {
                    if(line.trim().equals("disable")) {
                        ANSI = false;
                        displayProgressMessage("Colors are disabled.");
                    }
                    if(line.trim().equals("disableWarning")) {
                        colorWarning = false;
                    }
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
                } else if(key.equals("promptString")) {
                    promptString = value;
                }
            }
            if(!TextFighter.testMode) {
                displayProgressMessage("Display colors loaded.");
                if(ANSI && colorWarning) {
                    displayWarning("Display colors are enabled. If you wish to disable them,\nor you are getting unusual character sequences,\nyou can disable them in the display config file.\nIf you wish to disable this message,\ntype 'disableWarning' in the display config file.");
                } else if(!ANSI && colorWarning) {
                    displayWarning("Display colors are disabled. If you wish to enable them,\n remove 'disable' from the display config file.\nIf you wish to disable this message,\nIf you wish to disable this message,\ntype 'disableWarning' in the display config file.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            displayError("Unable to read the colors in the display file. (The file does exist).");
        }
    }
}
