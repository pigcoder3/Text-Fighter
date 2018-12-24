package org.textfighter;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import java.lang.reflect.*;

import org.textfighter.Display;
import org.textfighter.item.*;
import org.textfighter.item.tool.*;
import org.textfighter.item.armor.*;
import org.textfighter.item.weapon.*;
import org.textfighter.Player;
import org.textfighter.location.*;
import org.textfighter.location.UiTag;
import org.textfighter.enemy.Enemy;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")

public class TextFighter {

    static String gameName;

    static Player player = new Player();
    static Enemy currentEnemy = new Enemy();

    static File currentSaveFile;

    static String output = "";

    static File resourcesDir;
    static File tagFile;
    static File locationDir;
    static File enemyDir;
    static File savesDir;
    static File configDir;

    static JSONParser parser = new JSONParser();

    static ArrayList<Location> locations = new ArrayList<Location>();
    static ArrayList<UiTag> interfaceTags = new ArrayList<UiTag>();
    static ArrayList<String> saves = new ArrayList<String>();
    static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    static ArrayList<Enemy> possibleEnemies = new ArrayList<Enemy>();

    public static void addToOutput(String msg) {
        output+=msg + "\n";
    }

    public static boolean loadResources() {
        Display.displayProgressMessage("Loading the resources...");
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir + "/tags/tags.json");
        locationDir = new File(resourcesDir + "/locations");
        savesDir = new File("../../../saves");
        enemyDir = new File(resourcesDir + "/enemies");
        configDir = new File("../../../config");
        if (!loadLocations() || !loadParsingTags() || !loadEnemies()) { return false; }
        loadConfig();
        if (!savesDir.exists()) {
            Display.displayErrorWarning("WARNING: The saves directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { Display.displayErrorWarning("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    public static boolean loadLocations() {
        try {
            //Search through the locations directory for all locations
            for (String f : locationDir.list()) {
                if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                JSONObject uiArrayFile = (JSONObject) parser.parse(new FileReader(new File(locationDir.getAbsolutePath() + "/" + f)));
                JSONArray uiArray = (JSONArray)uiArrayFile.get("Interface");
                String name = (String) uiArrayFile.get("name");
                String uiString = "";
                for (int i = 0; i < uiArray.size(); i++) { uiString += uiArray.get(i) + "\n"; }
                JSONArray choiceArray = (JSONArray)uiArrayFile.get("Choices");
                ArrayList<Choice> choices = new ArrayList<Choice>();
                //Loads the choices from the file
                for (int i=0; i<choiceArray.size(); i++) {
                    JSONObject obj = (JSONObject)choiceArray.get(i);
                    JSONArray methodJSONArray = (JSONArray)obj.get("methods");
                    ArrayList<ChoiceMethod> methods = new ArrayList<ChoiceMethod>();
                    //Gets the methods
                    if(methodJSONArray != null && methodJSONArray.size() > 0) {
                        for(int p=0; p<methodJSONArray.size(); p++) {
                            JSONObject o = (JSONObject)methodJSONArray.get(p);
                            ArrayList<String> arguments = (JSONArray)o.get("arguments");
                            ArrayList<String> argumentTypesString = (JSONArray)o.get("argumentTypes");
                            ArrayList<Class> argumentTypes = new ArrayList<Class>();
                            if(argumentTypesString.size() > 0) {
                                for (int g=0; g<argumentTypesString.size(); g++) {
                                    if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                        argumentTypes.add(int.class);
                                    } else if(Integer.parseInt(argumentTypesString.get(g)) == 0) {
                                        argumentTypes.add(String.class);
                                    }
                                }
                            }
                            methods.add(new ChoiceMethod((String)o.get("method"), arguments, argumentTypes, (String)o.get("class"), (String)o.get("field")));
                        }
                    }
                    //Gets requirements if there is any
                    JSONArray requirementsJArray = (JSONArray)obj.get("requirements");
                    ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                    if(requirementsJArray != null && requirementsJArray.size() > 0) {
                        for(int p=0; p<requirementsJArray.size(); p++) {
                            JSONObject ro = (JSONObject)requirementsJArray.get(p);
                            ArrayList<String> arguments = (JSONArray)ro.get("requirementArgs");
                            ArrayList<String> argumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                            ArrayList<Class> argumentTypes = new ArrayList<Class>();
                            if(argumentTypesString.size() > 0) {
                                for (int g=0; g<argumentTypesString.size(); g++) {
                                    if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                        argumentTypes.add(int.class);
                                    } else if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                        argumentTypes.add(String.class);
                                    }
                                }
                            }
                            requirements.add(new Requirement((String)ro.get("method"), arguments, argumentTypes, (String)ro.get("class"), (String)ro.get("field")));
                        }
                    }
                    choices.add(new Choice((String)obj.get("name"), (String)obj.get("description"), (String)obj.get("usage"), methods, requirements));
                }
                locations.add(new Location(name, uiString, choices));
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }
    public static boolean loadEnemies() {
        try {
            for(String f : enemyDir.list()) {
                JSONObject enemyFile = (JSONObject) parser.parse(new FileReader(new File(enemyDir.getAbsolutePath() + "/" + f)));
                JSONArray requirementsJArray = (JSONArray)enemyFile.get("requirements");
                ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                if(requirements != null && requirementsJArray.size() > 0) {
                    for(int p=0; p<requirementsJArray.size(); p++) {
                        JSONObject ro = (JSONObject)requirementsJArray.get(p);
                        ArrayList<String> arguments = (JSONArray)ro.get("requirementArgs");
                        ArrayList<String> argumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                        ArrayList<Class> argumentTypes = new ArrayList<Class>();
                        if(argumentTypesString.size() > 0) {
                            for (int g=0; g<argumentTypesString.size(); g++) {
                                if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                    argumentTypes.add(int.class);
                                } else if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                    argumentTypes.add(String.class);
                                }
                            }
                        }
                    requirements.add(new Requirement((String)ro.get("method"), arguments, argumentTypes, (String)ro.get("class"), (String)ro.get("field")));
                    }
                }
                enemies.add(new Enemy((String)enemyFile.get("name"), Integer.parseInt((String)enemyFile.get("health")), Integer.parseInt((String)enemyFile.get("strength")), Integer.parseInt((String)enemyFile.get("levelRequirement")), requirements));
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
                ArrayList<String> arguments = (ArrayList<String>)obj.get("arguments");
                ArrayList<String> argumentTypesString = (ArrayList<String>)obj.get("argumentTypes");
                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                Method method;
                Class clazz;
                try { clazz = Class.forName((String)obj.get("class")); } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Display.displayErrorWarning("WARNING: Class not found '" + (String)obj.get("class") + "' Omitting tag '" + (String)obj.get("name") + "'");
                    continue;
                }
                if(arguments.size() > 0) {
                    for (int g=0; g<argumentTypesString.size(); g++) {
                        if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                            argumentTypes.add(int.class);
                        } else {
                            argumentTypes.add(String.class);
                        }
                    }
                }
                try { method = clazz.getMethod((String)obj.get("function"), argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){
                    Display.displayErrorWarning("WARNING: Omitting tag '" + (String)obj.get("tag") + "'"); e.printStackTrace(); continue;}
                Class returnType = method.getReturnType();
                if(returnType != String.class && method.getReturnType() != ArrayList.class && method.getReturnType() != int.class ) {
                    Display.displayErrorWarning("WARNING: UiTag '" + (String)obj.get("tag") + "' method does not return String, int, or ArrayList! Omitting tag.");
                    continue;
                }
                if(argumentTypes.size() != arguments.size()) {
                    Display.displayErrorWarning("WARNING: There is an incorrect number of arguments for this tag's function parameters! Omitting tag.");
                    continue;
                }
                interfaceTags.add(new UiTag((String)obj.get("tag"), method, arguments, argumentTypes, clazz));
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;

    }

    public static void loadConfig() {
        if(configDir.exists()) {
            Display.loadDesiredColors();
        } else {
            Display.displayErrorWarning("The config directory could not be found!\n Creating new config directory.");
            configDir.mkdirs();
        }
    }

    public static boolean loadGame(String name) {

        boolean areThereAnySaves = false;
        for(String s : savesDir.list()) { if(s.substring(s.lastIndexOf("."),s.length()).equals(".json")) { areThereAnySaves=true; continue; } }
        if(!areThereAnySaves){ addToOutput("There are no saves, create one."); return false;}

        File f = new File(savesDir.getPath() + "/" + name + ".json");
        if(!f.exists()) { addToOutput("Unable to find a save with that name."); return false; }

        currentSaveFile = f;

        try {
            JSONObject file = (JSONObject)parser.parse(new FileReader(f));
            gameName = (String)file.get("name");

            JSONObject stats = (JSONObject)file.get("stats");
            int level = Integer.parseInt((String)stats.get("level"));
            int experience = Integer.parseInt((String)stats.get("experience"));
            int score = Integer.parseInt((String)stats.get("score"));
            int maxhp = Integer.parseInt((String)stats.get("maxhealth"));
            int hp = Integer.parseInt((String)stats.get("health"));
            int coins = Integer.parseInt((String)stats.get("coins"));
            int magic = Integer.parseInt((String)stats.get("magic"));

            JSONObject inventory = (JSONObject)file.get("inventory");

            ArrayList<Item> newInventory = new ArrayList<Item>();

            Class[] items = {Pickaxe.class, Helmet.class, Chestplate.class, Leggings.class, Boots.class, Sword.class, Bow.class};

            for (Object key : inventory.keySet()) {
                JSONObject jsonobj = (JSONObject)inventory.get(key);
                int itemlevel = Integer.parseInt(((String)jsonobj.get("level")));
                int itemexperience = Integer.parseInt(((String)jsonobj.get("experience")));
                int itemtype = Integer.parseInt(((String)jsonobj.get("type")));
                for(Class c : items) {
                    if(c.getSimpleName().equals(jsonobj.get("itemtype"))) {
                        try {
                            Item item = new Item(itemlevel, itemexperience, itemtype);
                            Constructor cons = c.getConstructors()[0];
                            item = (Item)cons.newInstance(itemlevel, itemexperience, itemtype);
                            newInventory.add(item);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); continue; }
                    }
                }
            }

            player = new Player(hp, maxhp, coins, magic, level, experience, score, newInventory);
            addToOutput("Loaded save '" + name + "'");

        } catch (IOException | ParseException e) { addToOutput("Unable to read the save"); e.printStackTrace(); return false; }

        return true;

    }
    public static void newGame(String name) {

        if(getSaves().contains(name)) {
            addToOutput("There is already a save with that name. Pick another.");
            return;
        }

        File newGameFile = new File(savesDir.getPath() + "/" + name + ".json");
        try { newGameFile.createNewFile(); } catch (IOException e) {
            addToOutput("Failed to create new file");
            e.printStackTrace();
            return;
        }

        currentSaveFile = newGameFile;

        JSONObject base = new JSONObject();

        JSONObject stats = new JSONObject();
        stats.put("level", "1");
        stats.put("experience", "0");
        stats.put("score", "0");
        stats.put("maxhealth", Integer.toString(Player.defaulthp));
        stats.put("health", Integer.toString(Player.defaulthp));
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
        newInventory.add(new Sword(1,0,0));

        player = new Player(Player.defaulthp, Player.defaulthp, 0, 0, 1, 0, 0, newInventory);

        addToOutput("Added new save '" + name + "'");

        try (FileWriter w = new FileWriter(newGameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static boolean saveGame(String name) {
        //Rewrite the whole file

        File gameFile = new File(savesDir.getPath() + "/" + gameName + ".json");
        if(!gameFile.exists()) { try { gameFile.createNewFile(); } catch (IOException e) { addToOutput("Unable to save game!"); return false; }}

        JSONObject base = new JSONObject();

        JSONObject stats = new JSONObject();
        stats.put("level", Integer.toString(player.getLevel()));
        stats.put("experience", Integer.toString(player.getExperience()));
        stats.put("score", Integer.toString(player.getScore()));
        stats.put("health", Integer.toString(player.getHp()));
        stats.put("maxhealth", Integer.toString(player.getMaxHp()));
        stats.put("coins", Integer.toString(player.getCoins()));
        stats.put("magic", Integer.toString(player.getMagic()));

        JSONObject inventory = new JSONObject();

        Class[] items = {Pickaxe.class, Helmet.class, Chestplate.class, Leggings.class, Boots.class, Sword.class, Bow.class};

        //For all items
        for(Class c : items) {
            if(player.isCarrying(c.getName())) {
                Item obj = player.getFromInventory(c.getName());
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
                inventory.put(c, jsonobj);
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

    public static ArrayList<String> getSaves() { saves = getSaveFiles(); return saves; }
    public static void addSave(String name) { saves.add(name); }
    public static void removeSave(String name) {
        boolean saveExists = false;
        for(int i=0;i<saves.size();i++){
            if(saves.get(i).equals(name)) {
                saveExists = true;
                new File(savesDir.getAbsolutePath() + "/" + saves.get(i) + ".json").delete();
                saves.remove(i);
                addToOutput("Removed save '" + name + "'");
            }
        }
        if(!saveExists) {
            addToOutput("File with that name not found. No action performed");
        }
    }

    public static ArrayList<UiTag> getInterfaceTags() { return interfaceTags; }

    public static ArrayList<String> getSaveFiles() {
        ArrayList<String> filteredSaves = new ArrayList<String>();
        for(String s : savesDir.list()) {
            if(s.substring(s.lastIndexOf(".")).equals(".json")) {
                filteredSaves.add(s.substring(0,s.lastIndexOf(".")));
            }
        }
        return filteredSaves;
    }
    public static void setPossibleEnemies() {
        sortEnemies();
        ArrayList<Enemy> possible = new ArrayList<Enemy>();
        for(Enemy e : enemies) {
            boolean valid = true;
            if(e.getLevelRequirement() >= player.getLevel()) {
                for(Requirement r : e.getRequirements()) {
                    if(!r.invokeMethod()) {
                        valid=false;
                        break;
                    }
                }
                if(valid) {
                    possible.add(e);
                }
            }
        }
        possibleEnemies = possible;
    }
    public static void sortEnemies() {
        ArrayList<Enemy> sortedList = new ArrayList<Enemy>();
        int highestDifficulty = 0;
        for(Enemy e : enemies) {
            if(e.getDifficulty() > highestDifficulty) {
                sortedList.add(e);
            }
        }
        enemies=sortedList;
    }
    public static ArrayList<String> getPossibleEnemyOutputs() {
        ArrayList<String> outputs = new ArrayList<String>();
        for(Enemy e : possibleEnemies) {
            outputs.add(e.getOutput());
        }
        return outputs;
    }

    public static boolean movePlayer(String location) {
        for(Location l : locations) {
            if(l.getName().equals(location)) {
                player.setLocation(location);
                return true;
            }
        }
        return false;
    }

    public static void invokePlayerInput() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            Display.promptUser();
            String input = in.readLine();
            Display.resetColors();
            Display.previousCommandString = input;
            if(input != null) {
                boolean validChoice = false;
                for(Choice c : player.getLocation().getPossibleChoices()){
                    if(input.indexOf(" ") != -1) {
                        if (c.getName().equals(input.substring(0,input.indexOf(" ")))) {
                            validChoice = true;
                            break;
                        }
                    } else {
                        if (c.getName().equals(input)) {
                            validChoice = true;
                            break;
                        }
                    }
                }
                if(validChoice) {
                    ArrayList<String> inputArrayList = new ArrayList<String>(Arrays.asList(input.split(" ")));
                    String commandName = inputArrayList.get(0);
                    inputArrayList.remove(0);
                    for(Choice c : player.getLocation().getPossibleChoices()) {
                        if(c.getName().equals(commandName)) {
                            if(inputArrayList != null) {
                                c.invokeMethods(inputArrayList);
                            } else {
                                c.invokeMethods(new ArrayList<String>());
                            }
                        }
                    }
                } else {
                    if(input.indexOf(" ") != -1) {
                        addToOutput("Invalid choice - '" + input.substring(0,input.indexOf(" ")) + "'");
                    } else {
                        addToOutput("Invalid choice - '" + input + "'");
                    }
                }
            } else {addToOutput("Pick one of the choices shown.");}
        } catch (IOException e) {
            Display.displayErrorWarning("An error occured while reading input!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean fight(String en) {
        boolean validEnemy = false;
        for(int i=0; i<enemies.size(); i++) {
            if (enemies.get(i).getName().equals(en)) {
                validEnemy = true;
                try { currentEnemy = (Enemy)enemies.get(i).clone(); } catch (CloneNotSupportedException e) { addToOutput("Could not create an enemy of that type! ('" + e + "')"); e.printStackTrace(); return false; }
                break;
            }
        }
        if(validEnemy) {
            while(player.getInFight()) {
                playGame();
                if(player.getHp() < 1) {
                    player.setAlive(false);
                    player.setInFight(false);
                } else if(currentEnemy.getHp() < 1) {
                    player.setInFight(false);
                    return true;
                }
            }
        } else {
            addToOutput("Invalid enemy: '" + en + "'");
            return false;
        }
        return false;
    }

    public static void exitGame(int code) {
        System.exit(code);
    }

    public static void playGame() {
        //Display the interface for the user
        Display.displayInterface(player.getLocation());
        invokePlayerInput();
        System.out.println("\u001b[H \u001b[2J");
        Display.displayPreviousCommand();
        if(output != null) {
            Display.displayOutputMessage(output);
        }
        output="";
    }

    public static void main(String[] args) {
        System.out.println("\u001b[H\u001b[2J");
        if (!loadResources()) {
            Display.displayErrorWarning("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(1);
        }
        getSaveFiles();
        player.setLocation("saves");
        while(player.getAlive()) {
            playGame();
        }
    }

}
