package org.textfighter;

import java.util.ArrayList;
import java.io.*;

import org.textfighter.Display;
import org.textfighter.item.Item;
import org.textfighter.Player;
import org.textfighter.item.Pickaxe;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TextFighter {

    static File resourcesDir;
    static File tagFile;
    static File interfaceDir;
    static File enemyDir;
    static File savesDir;

    static JSONParser parser = new JSONParser();

    static ArrayList<UserInterface> userInterfaces = new ArrayList<UserInterface>();
    static ArrayList<UiTag> interfaceTags = new ArrayList<String>();

    public static boolean loadResources() {
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir + "/tags/tags.json");
        interfaceDir = new File(resourcesDir + "/interfaces");
        savesDir = new File(resourcesDir + "/saves");
        if (!loadInterfaces() || !loadParsingTags()) { return false; }
        if (!savesDir.exists()) {
            System.out.println("WARNING: The save directory does not exist! \n Creating a new saves directory...");
            if (!new File(resourcesDir + "/saves/").mkdirs()) { System.out.println("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    public static boolean loadInterfaces() {
        try {
            for (String f : interfaceDir.list()) {
                if(!f.substring(f.indexOf(".")).equals(".json")) { continue; }
                JSONObject uiArrayFile = (JSONObject) parser.parse(new FileReader(new File(interfaceDir.getAbsolutePath() + "/" + f)));
                JSONArray uiArray = (JSONArray) uiArrayFile.get("Interface");
                String name = (String) uiArrayFile.get("name");
                String uiString = "";
                for (int u = 0; u < uiArray.size(); u++) { uiString = uiString + uiArray.get(u) + "\n"; }
                userInterfaces.add(new UserInterface(name, uiString));
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }

    public static boolean loadParsingTags() {

        try {
            JSONObject tagsObject = (JSONObject) parser.parse(new FileReader(tagFile));
            JSONArray tagsArray = (JSONArray) tagsObject.get(0);
            for (int i = 0; i < tagsArray.size(); i++) {
                interfaceTags.add((String)tagsObject.get(i));
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;

    }

    public static boolean loadSave(String name) {
        //Read from /Saves directory
        File saveDirectory = new File("Saves");

        return true;

    }

    public static void newGame() {
        //Create a .JSON file in /Saves
        //Write to it the default stuff
        /*
        try {

        } catch( ) {

        } */

    }

    public static boolean saveGame() {
        //Write to /Saves directory


        return true;

    }

    public static void fight() {

    }

    public static ArrayList<String> getSaveFiles() {
        ArrayList<String> filteredSaves = new ArrayList<String>();
        for(String s : savesDir.list()) {
            if(s.substring(s.indexOf(".")).equals(".json")) {
                filteredSaves.add(s);
            }
        }
        return filteredSaves;
    }

    public static void main(String[] args) {
        //System.out.println("\u001b[2J");
		if (!loadResources()) {
            System.out.println("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(0);
        }
        Player player = new Player();
        // Display all saves
        // Ask if user want to load saves
        // if yes, then load
        // else, make new
    }

}
