package org.textfighter.display;

import org.textfighter.location.Location;
import org.textfighter.TextFighter;
import org.textfighter.display.*;

import java.util.ArrayList;
import java.util.Date;

import java.io.*;

public class Display {

    /***The directory where all error logs are stored*/
    public static final File logDir = new File("../../../logs");

    /***The file where the log is stored*/
    public static File logFile;

    /***The tabbing the is displayed before pack messages, warnings and errors*/
    public static String packTabbing = "";

    /***The prompt*/
    public static String promptString = " > ";

    /***The name of all colors*/
    public static final String[] COLORNAMES = {"black", "red", "green", "yellow", "blue", "purple", "cyan", "white"};

    /***The name of all color codes. They correspond with the {@link #COLORNAMES} by index.*/
    public static final String[] COLORCODES = {"\u001B[30m", "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m", "\u001B[37m"};

    /***The ansi code for reseting formatting.*/
    public static final String RESET = "\u001B[0m";
    /***The ansi code for bold text.*/
    public static final String BOLD = "\u001B[1m";

    /***Stores the previous player command.*/
    public static String previousCommandString = "";

    /***The default code for previous command.*/
    public static String previousCommand = COLORCODES[2];
    /***The default code for error.*/
    public static String error = COLORCODES[1];
    /***The default code for warning.*/
    public static String warning = COLORCODES[3];
    /***The default code for progress.*/
    public static String progress = COLORCODES[4];
    /***The default code for output.*/
    public static String output = COLORCODES[2];
    /***The default code for prompt.*/
    public static String prompt = COLORCODES[2];

    /***whether or not ansi codes whould be used.*/
    public static boolean ANSI = true;

    /**
     * Stores all the interface tags.
     * <p>Set to an empty ArrayList of UiTags.</p>
     */
    public static ArrayList<UiTag> interfaceTags = new ArrayList<UiTag>();
    /**
     * Stores all the interfaces.
     * <p>Set to an empty ArrayList of UserInterfaces.</p>
     */
    public static ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();

    /**
     * Turns an exception stack trace to a string.
     * @param e     The exception
     * @return      The exception stack trace converted to a String.
     */
    public static String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Writes an error message to the log file.
     * @param msg       The message to log.
     */
    public static void writeToLogFile(String msg) {
        try {
            if(logFile == null || !logFile.exists()) {
                if(!logDir.exists()) {
                    logDir.mkdir();
                }
                logFile = new File(logDir.getPath() + "/" + new Date().toString());
                logFile.createNewFile();
            }
        } catch (IOException e) { System.out.println("Could not write to log file '" + logFile.getName() + "' because unable to create one"); }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
            out.println(msg);
        } catch (IOException e) { System.out.println("Could not write to log file '" + logFile.getName() + "'"); }
    }
    /**
     * Displays an error message.
     * @param e     The message
     */
    public static void displayError(String e) {
        // Used for displaying errors such as something could not be found
        if(ANSI) {
            System.err.println(error + "[Error] " + e + RESET);
        } else {
            System.err.println("[Error] " + e);
        }
        writeToLogFile("[Error] " + e);
    }

    /**
     * Displays an error message.
     * @param e     The message
     */
    public static void displayPackError(String e) {
        // Displays errors that deal with packs
        if(TextFighter.testMode) {
            if(ANSI) {
                System.err.println(error + packTabbing + "[PackError] " + e + RESET);
            } else {
                System.err.println(packTabbing + "[PackError] " + e);
            }
            writeToLogFile(packTabbing + "[PackError] " + e);
        }
    }

    /**
     * Displays a pack message.
     * @param e     The message
     */
    public static void displayPackMessage(String e) {
        // Displays messages that deal with packs
        if(TextFighter.testMode) {
            if(ANSI) {
                System.err.println(progress + packTabbing + "[PackMessage] " + e + RESET);
            } else {
                System.err.println(packTabbing + "[PackMessage] " + e);
            }
            writeToLogFile(packTabbing + "[PackMessage] " + e);
        }
    }

    /**
     * Changes the pack tabbing. True increases, false decreases.
     * @param plusOrMinus   The message
     */
    public static void changePackTabbing(boolean plusOrMinus) {
        // true: add, false: remove
        if(plusOrMinus) {
            packTabbing+="  ";
        } else {
            if(packTabbing != null && packTabbing.length() > 1)
            packTabbing=packTabbing.substring(0,packTabbing.length()-2);
        }
    }

    /**
     * Displays a warning.
     * @param e     The message
     */
    public static void displayWarning(String e) {
        // Displays warnings
        if(ANSI) {
            System.err.println(warning + "[Warning] " + e + RESET);
        } else {
            System.err.println("[Warning] " + e);
        }
        writeToLogFile("[Warning] " + e);
    }

    /**
     * Displays an output message.
     * @param e     The message
     */
    public static void displayOutputMessage(String e) {
        // Used for displaying messages that explain loading resource progess
        if(ANSI) {
            System.err.println(output + e + RESET);
        } else {
            System.err.println(e);
        }
        writeToLogFile("[Output] " + e);

    }

     /**
     * Displays a loading progress message.
     * @param e     The message
     */
    public static void displayProgressMessage(String e) {
        // Used for displaying the output field in TextFighter class
        if(ANSI) {
            System.out.println(progress + "[Progress] " + e + RESET);
        } else {
            System.out.println("[Progress] " + e);
        }
        writeToLogFile("[Progress] " + e);
    }

    /*** Displays the previous command.*/
    public static void displayPreviousCommand() {
        //Displays the command the user previously inputted
        if(ANSI) {
            System.out.println(previousCommand + "Previous choice: '" + previousCommandString + "'" + RESET + "\n");
        } else {
            System.out.println("Previous choice: " + previousCommandString + "\n");
        }
        writeToLogFile("[PreviousCommand] " + previousCommand);
    }

    /***Displays the {@link #prompt}.*/
    public static void promptUser() {
        //Displays the prompt
        if(ANSI) {
            System.out.print(prompt + promptString + RESET);
        } else {
            System.out.print(promptString);
        }
        writeToLogFile("[Prompted User] " + promptString);
    }

    /**
     * Displays the achievement earned.
     * @param name      The name of the achievement.
     */
    public static void achievementEarned(String name) {
        if(ANSI) {
            TextFighter.addToOutput(output + BOLD + "You have earned the achievement '" + name + "'!" + RESET);
        } else {
            TextFighter.addToOutput("You have earned the achievement '" + name + "'!");
        }
        writeToLogFile("[Achievement] You have earned the achievement '" + name + "'!");
    }

    /***Resets text styling.*/
    public static void resetColors() {
        //Resets the colors for err and out
        if(ANSI) {
            System.out.println(RESET);
            System.err.println(RESET);
            writeToLogFile("[Colors Reset]");
        }
    }

    /***Clears the screen.*/
    public static void clearScreen() {
        //Puts the character back to the home and clears the screen
        if(ANSI) {
            System.out.println("\u001b[H \u001b[2J");
            writeToLogFile("[Screen Cleared]");
        }
    }

    /**
     * Display all the interfaces of the location given (generally uses the player's current location).
     * @param l     The location.
     */
    public static void displayInterfaces(Location l) {
        l.filterPossibleChoices();
        for(UserInterface ui : l.getInterfaces()) {
            ui.parseInterface();
            System.out.println(ui.getParsedUI());
        }
        writeToLogFile("[Interfaces Displayed]");
    }

    /**
     * Loads colors from the display file in the config directory.
     * <p>It loads each color by key and value. The values should correspond with the names
     * of the colors in {@link #COLORNAMES}, which corresponds with a color code by index.</p>
     * <p>If a line equals "disable", then colors are disabled. If a line equals "disableWarning",
     * then the color warning is disabled.</p>
     * <p>If the color warning is not disabled it will tell the user that, if they are using a console
     * that does not support ANSI, then ANSI codes will start showing up everywhere, and that they can disable it in the display config file.</p>
     */
    public static void loadDesiredColors() {
        displayProgressMessage("Loading the display colors...");
        File displayColors = new File(TextFighter.configDir.getAbsolutePath() + "/display");
        if(!displayColors.exists()) { return; }
        try (BufferedReader br = new BufferedReader(new FileReader(displayColors));) {
            String line;
            boolean colorWarning = true;
            while ((line = br.readLine()) != null) { //Make sure the line isnt null
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
                //Set each field to the value specified by the key
                if(key.equals("error")) {
                    for(int i=0; i<COLORNAMES.length; i++) {
                        if(COLORNAMES[i].equals(value)) {
                            error = COLORCODES[i];
                        }
                    }
                } else if(key.equals("progress")) {
                    for(int i=0; i<COLORNAMES.length; i++) {
                        if(COLORNAMES[i].equals(value)) {
                            progress = COLORCODES[i];
                        }
                    }
                } else if(key.equals("output")) {
                    for(int i=0; i<COLORNAMES.length; i++) {
                        if(COLORNAMES[i].equals(value)) {
                            output = COLORCODES[i];
                        }
                    }
                } else if(key.equals("prompt")) {
                    for(int i=0; i<COLORNAMES.length; i++) {
                        if(COLORNAMES[i].equals(value)) {
                            prompt = COLORCODES[i];
                        }
                    }
                } else if(key.equals("previousCommand")) {
                    for(int i=0; i<COLORNAMES.length; i++) {
                        if(COLORNAMES[i].equals(value)) {
                            previousCommand = COLORCODES[i];
                        }
                    }
                } else if(key.equals("warning")) {
                    for(int i=0; i<COLORNAMES.length; i++) {
                        if(COLORNAMES[i].equals(value)) {
                            warning = COLORCODES[i];
                        }
                    }
                } else if(key.equals("promptString")) {
                    promptString = value;
                }
            }
            if(!TextFighter.testMode) {
                displayProgressMessage("Display colors loaded.");
                //The warning is there just so that the users know about this
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
