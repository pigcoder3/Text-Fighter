package tfighter;

import java.util.ArrayList;
import java.io.File;

import tfighter.Display;

import org.json.*;

public class TextFighter {

    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
ArrayList<UserInterface> userInterfaces = new ArrayList<UserInterface>();

    public static void loadEnemies() {
        //Read from the /Resources/Enemies directory
        File enemyFile = new File("../Resources/Enemy");
    }

    public static void loadInterfaces() {
        //Read from the /Resources/Interfaces directory
        File interfaceDirectory = new File("../Resources/Interfaces");
        for (File f : interfaceDirectory.listFiles()) {

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
