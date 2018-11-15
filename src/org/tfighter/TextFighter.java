package tfighter;

import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import tfighter.Display;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class TextFighter {

    //Resources directory
    static String resourcesDir = "../../Resources";
    static JSONParser parser = new JSONParser();

    static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    static ArrayList<UserInterface> userInterfaces = new ArrayList<UserInterface>();

    public static void loadEnemies() {
        //Read from the /Resources/Enemies directory
        File enemyFile = new File("../Resources/Enemy");
    }

    public static void loadInterfaces() {
        //Read from the /Resources/Interfaces directory
        try {
            File interfaceDir = new File(resourcesDir + "/Interfaces/");
            for (File f : interfaceDir.listFiles()) {
                JSONArray uiArrayFile = (JSONArray) parser.parse(new FileReader(f));
                ArrayList<String> array = (ArrayList<String>)(uiArrayFile.get(2));
                String uiString = " ";
                for (int i = 0; i < array.size(); i++) {
                    uiString = uiString + array.get(i);
                }
                UserInterface ui = new UserInterface(uiString);
                userInterfaces.add(ui);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadParsingTags() {
        //Read from the /Resources/Tags/ file

    }

    public static void loadSave(String name) {
        //Read from /Saves directory
        File saveDirectory = new File("../Saves");


    }

    public static void newGame() {
        //Create a .JSON file in /Saves
        //Write to it the default stuff

    }

    public static void saveGame() {
        //Write to /Saves directory

    }

    public static void getChoices() {
        //Read from the /Resources/Choices directory

    }

    public static void main(String[] args) {
        loadEnemies();
        loadInterfaces();
        Player player = new Player();
        // Display all saves
        // Ask if user want to load saves
        // if yes, then load
        // else, make new
    }

}
