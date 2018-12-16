package org.textfighter;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

import java.lang.reflect.*;

import org.textfighter.Display;
import org.textfighter.item.*;
import org.textfighter.item.tool.*;
import org.textfighter.item.armor.*;
import org.textfighter.item.weapon.*;
import org.textfighter.Player;
import org.textfighter.userinterface.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TextFighter {

    static String gameName;

    static Player player;

    static File resourcesDir;
    static File tagFile;
    static File interfaceDir;
    static File enemyDir;
    static File savesDir;

    static JSONParser parser = new JSONParser();

    static ArrayList<UserInterface> userInterfaces = new ArrayList<UserInterface>();
    static ArrayList<UiTag> interfaceTags = new ArrayList<UiTag>();
    static ArrayList<String> saves = new ArrayList<String>();

    public static boolean loadResources() {
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir + "/tags/tags.json");
        interfaceDir = new File(resourcesDir + "/interfaces");
        savesDir = new File("../../../saves");
        if (!loadInterfaces() || !loadParsingTags()) { return false; }
        if (!savesDir.exists()) {
            System.out.println("WARNING: The save directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { System.out.println("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    public static boolean loadInterfaces() {
        try {
            for (String f : interfaceDir.list()) {
                if(!f.substring(f.indexOf(".")).equals(".json")) { continue; }
                JSONObject uiArrayFile = (JSONObject) parser.parse(new FileReader(new File(interfaceDir.getAbsolutePath() + "/" + f)));
                JSONArray uiArray = (JSONArray)uiArrayFile.get("Interface");
                String name = (String) uiArrayFile.get("name");
                String uiString = "";
                for (int i = 0; i < uiArray.size(); i++) { uiString = uiString + uiArray.get(i) + "\n"; }
                JSONArray choiceArray = (JSONArray)uiArrayFile.get("Choices");
                ArrayList<Choice> choices = new ArrayList<Choice>();
                for (int i = 0; i < choiceArray.size(); i++) {
                    JSONObject obj = (JSONObject)choiceArray.get(i);
                    ArrayList<String> arguments = (JSONArray)obj.get("arguments");
                    ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
                    ArrayList<Class> argumentTypes = new ArrayList<Class>();
                    if(arguments.size() != argumentTypesString.size()) { System.exit(1); }
                    if(arguments.size() > 0) { for (int p=0; i>argumentTypesString.size(); i++) { if(Integer.parseInt(argumentTypesString.get(p)) == 1) { argumentTypes.add(int.class); } else { argumentTypes.add(String.class); }}}
                    choices.add(new Choice((String)obj.get("name"), (String)obj.get("description"), (String)obj.get("function"), arguments, argumentTypes, (String)obj.get("requirement"), (String)obj.get("class")));
                }
                userInterfaces.add(new UserInterface(name, uiString, choices));
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }

    public static boolean loadParsingTags() {
        try {
            JSONObject tagsFile = (JSONObject)parser.parse(new FileReader(tagFile));
            JSONArray tagsArray = (JSONArray)tagsFile.get("tags");
            for (int i = 0; i < tagsArray.size(); i++) {
                JSONObject obj = (JSONObject)tagsArray.get(i);
                ArrayList<String> arguments = (JSONArray)obj.get("arguments");
                ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                interfaceTags.add(new UiTag((String)obj.get("tag"),(String)obj.get("function"), arguments, argumentTypes, (String)obj.get("class")));
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;

    }

    public static boolean loadGame() {

        String name = "";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));) {
            boolean valid = false;
            while (!valid) {
                System.out.println("Which game would you like to load?");
                name = in.readLine();
                for (String s : savesDir.list()) {
                    if(s.substring(0,s.indexOf(".")).equalsIgnoreCase(name)) {
                        valid=true;
                        break;
                    } else {
                        valid=false;
                    }
                }
                if(!valid) { System.out.println("There is no save with that name.");}
            }
        } catch (IOException e) { System.out.println("An error occured while reading your input!"); e.printStackTrace(); System.exit(1); }

        File f = new File(savesDir.getPath() + "/" + name + ".json");
        if(!f.exists()) { System.out.println("Unable to find a save with that name (Or the file could not be found)"); System.exit(0); }

        try {
            JSONObject file = (JSONObject)parser.parse(new FileReader(f));
            gameName = (String)file.get("name");

            JSONObject stats = (JSONObject)file.get("stats");
            int level = Integer.parseInt((String)stats.get("level"));
            int experience = Integer.parseInt((String)stats.get("experience"));
            int score = Integer.parseInt((String)stats.get("score"));
            int hp = Integer.parseInt((String)stats.get("health"));
            int maxhp = Integer.parseInt((String)stats.get("maxHealth"));
            int coins = Integer.parseInt((String)stats.get("coins"));
            int magic = Integer.parseInt((String)stats.get("magic"));

            JSONObject inventory = (JSONObject)file.get("inventory");

            ArrayList<Item> newInventory = new ArrayList<Item>();

            Class[] items = {Pickaxe.class, Helmet.class, Chestplate.class, Leggings.class, Boots.class, Sword.class, Bow.class};

            for (Object key : inventory.keySet()) {
                System.out.println((String)key);
                JSONObject jsonobj = (JSONObject)inventory.get(key);
                int itemlevel = Integer.parseInt(((String)jsonobj.get("level")));
                int itemexperience = Integer.parseInt(((String)jsonobj.get("experience")));
                int itemtype = Integer.parseInt(((String)jsonobj.get("type")));
                for(Class c : items) {
                    if(c.getSimpleName().equals(jsonobj.get("itemtype"))) {
                        try {
                            Item item = new Item(itemlevel, itemexperience, itemtype);
                            Constructor cons = c.getConstructors()[0];
                            if(c.getSuperclass().equals(Weapon.class)) {item = (Item)cons.newInstance(itemlevel, itemexperience, itemtype, Integer.parseInt(((String)jsonobj.get("damage"))));}
                            else if(c.getSuperclass().equals(Armor.class)) {item = (Item)cons.newInstance(itemlevel, itemexperience, itemtype, Double.parseDouble(((String)jsonobj.get("protectionAmount"))));}
                            else{item = (Item)cons.newInstance(itemlevel, itemexperience, itemtype);}
                            newInventory.add(item);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); continue; }
                    }
                }
            }

            player = new Player(hp, maxhp, coins, magic, level, experience, score, newInventory);

        } catch (IOException | ParseException e) { System.out.println("An error occured while reading the save file!"); e.printStackTrace(); System.exit(1);}

        return true;

    }

    public static void newGame() {

        String name = "";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));) {
            boolean valid = false;
            while (!valid) {
                System.out.println("What would you like this save to be called?\nDo not use names already used before.");
                name = in.readLine();
                for(String s : savesDir.list()) {
                    if(s.substring(0,s.indexOf(".")).equalsIgnoreCase(name)) {
                        valid=false;
                    } else {
                        valid=true;
                        break;
                    }
                }
                if(!valid) { System.out.println("That name is already used! Pick another.");}
            }
            gameName=name;
        } catch (IOException e) { System.out.println("An error occured while reading your input!"); e.printStackTrace(); System.exit(1); }

        File newGameFile = new File(savesDir.getPath() + "/" + name + ".json");
        try { newGameFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }

        JSONObject base = new JSONObject();

        JSONObject stats = new JSONObject();
        stats.put("level", "1");
        stats.put("experience", "0");
        stats.put("score", "0");
        stats.put("hp", Integer.toString(Player.defaulthp));
        stats.put("maxhp", Integer.toString(Player.defaulthp));
        stats.put("coins", "0");
        stats.put("magic", "0");

        JSONObject inventory = new JSONObject();

        JSONObject sword = new JSONObject();
        sword.put("itemtype", "Sword");
        sword.put("type", "0");
        sword.put("level", "1");
        sword.put("experience", "0");
        inventory.put("sword", sword);

        base.put("inventory", inventory);
        base.put("stats", stats);
        base.put("name", name);

        //This is temporary as I will create methods for calculation based on level
        ArrayList<Item> newInventory = new ArrayList<Item>();
        newInventory.add(new Sword(1,0,0,10));

        player = new Player(Player.defaulthp, Player.defaulthp, 0, 0, 1, 0, 0, newInventory);

        try (FileWriter w = new FileWriter(newGameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static boolean saveGame() {
        //Rewrite the whole file

        File gameFile = new File(savesDir.getPath() + "/" + gameName + ".json");
        if(!gameFile.exists()) { try { gameFile.createNewFile(); } catch (IOException e) { System.out.println("Unable to save game!"); return false; }}

        JSONObject base = new JSONObject();

        JSONObject stats = new JSONObject();
        stats.put("level", Integer.toString(player.getLevel()));
        stats.put("experience", Integer.toString(player.getExperience()));
        stats.put("score", Integer.toString(player.getScore()));
        stats.put("health", Integer.toString(player.getHp()));
        stats.put("maxHealth", Integer.toString(player.getMaxHp()));
        stats.put("coins", Integer.toString(player.getCoins()));
        stats.put("magic", Integer.toString(player.getMagic()));

        JSONObject inventory = new JSONObject();

        Class[] items = {Pickaxe.class, Helmet.class, Chestplate.class, Leggings.class, Boots.class, Sword.class, Bow.class};

        //For all items
        for(Class c : items) {
            if(player.isCarrying(c)>0) {
                Item obj = player.getFromInventory(c);
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("itemtype", obj.getClass().getSimpleName());
                jsonobj.put("type", Integer.toString(obj.getType()));
                jsonobj.put("level", Integer.toString(obj.getLevel()));
                jsonobj.put("experience", Integer.toString(obj.getExperience()));
                if(c.getSuperclass().equals(Armor.class)) {
                    jsonobj.put("protectionAmount", String.valueOf(((Armor)obj).getProtectionAmount()));
                }
                if(c.getSuperclass().equals(Weapon.class)) {
                    jsonobj.put("damage", Integer.toString(((Weapon)obj).getDamage()));
                }
                inventory.put(c.getSimpleName(), jsonobj);
            }
        }

        base.put("inventory", inventory);
        base.put("stats", stats);
        base.put("name", gameName);

        try (FileWriter w = new FileWriter(gameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); return false; }

        return true;

    }

    public static ArrayList<String> getSaves() { return saves; }
    public static void addSave(String name) { saves.add(name); }
    public static void removeSave(String name) {
        for(int i=0;i<saves.size();i++){
            if(saves.get(i).equals(name)) {
                saves.remove(i);
            }
        }
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

    public static void quitGame() { System.exit(0); }

    public static void fight() {

    }

    public static void main(String[] args) {
        //System.out.println("\u001b[2J");
        if (!loadResources()) {
            System.out.println("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(0);
        }
        loadGame();
        player.gainCoins(1);
        saveGame();
        // Display all saves
        // Ask if user wants to load saves
        // if yes, then load
        // else, make new
    }

}
