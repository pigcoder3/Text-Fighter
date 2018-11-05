import java.util.ArrayList;
import java.nio.File;

import org.json.*;

public class TextFighter {

    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    ArrayList<Interface> userInterfaces = new ArrayList<Interface>();

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

        // Display all saves
        // Ask if user want to load saves
        // if yes, then load
        // else, make new
    }

}
