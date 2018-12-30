package org.textfighter;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import java.lang.reflect.*;

import org.textfighter.display.*;
import org.textfighter.item.*;
import org.textfighter.item.tool.*;
import org.textfighter.item.armor.*;
import org.textfighter.item.weapon.*;
import org.textfighter.Player;
import org.textfighter.location.*;
import org.textfighter.enemy.Enemy;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")

public class TextFighter {

    public static String gameName;

    public static Player player = new Player();
    public static Enemy currentEnemy = new Enemy();

    public static File currentSaveFile;

    public static String output = "";

    public static File resourcesDir;
    public static File tagFile;
    public static File interfaceDir;
    public static File locationDir;
    public static File enemyDir;
    public static File savesDir;
    public static File configDir;
    public static File packFile;
    public static File packDir;
    public static File modpackDir;
    public static File intpackDir;
    public static File enemypackDir;

    public static JSONParser parser = new JSONParser();

    public static ArrayList<Location> locations = new ArrayList<Location>();
    public static ArrayList<String> saves = new ArrayList<String>();
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public static ArrayList<Enemy> possibleEnemies = new ArrayList<Enemy>();

    public static void addToOutput(String msg) {
        output+=msg + "\n";
    }

    public static boolean loadResources() {
        Display.displayProgressMessage("Loading the resources...");
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir + "/tags/tags.json");
        interfaceDir = new File(resourcesDir + "/userinterfaces/");
        locationDir = new File(resourcesDir + "/locations");
        savesDir = new File("../../../saves");
        enemyDir = new File(resourcesDir + "/enemies");
        configDir = new File("../../../config");
        //Place where desired packs are defined
        packFile = new File(configDir + "/packs");
        packDir = new File("../../../packs");
        //Place where all the modPacks are
        modpackDir = new File(packDir + "/modpacks");
        //Place where all the interfacePacks are
        intpackDir = new File(packDir + "/intpacks");
        //Place where all the enemyPacks are
        enemypackDir = new File(packDir + "/enemypacks");
        if (!loadParsingTags() || !loadInterfaces() || !loadLocations() || !loadEnemies()) { return false; }
        loadConfig();
        if (!savesDir.exists()) {
            Display.displayWarning("The saves directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { Display.displayError("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    public static boolean loadInterfaces() {
        try {
            Display.displayProgressMessage("Loading the interfaces...");
            ArrayList<String> usedNames = new ArrayList<String>();
            boolean parsingPack = false;
            File directory = interfaceDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = "";
                        String value = "";
                        if(line.indexOf("=") != -1) {
                            key = line.substring(0,line.indexOf("="));
                            value = line.substring(line.indexOf("=")+1,line.length()).trim();
                        }
                        if(value == null || key == null || ! key.equals("intpack") || key.substring(0,1).equals("#")) { continue; }
                        File intpack = new File(intpackDir + "/" + value);
                        if(intpackDir.list() != null && new ArrayList<>(Arrays.asList(intpackDir.list())).contains(value) && intpack.isDirectory()) {
                            directory = intpack;
                            Display.displayProgressMessage("loading interfaces from interfacepack '" + value + "'");
                            parsingPack = true;
                        } else {
                            Display.displayWarning("Interface pack '" + value + "' not found. Falling back to default interfaces.");
                        }
                    }
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the packs file (The file does exist). Falling back to default interfaces."); }
            }

            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; }
                for (String f : directory.list()) {
                    if(f.equals("tags.json")) { continue; }
                    if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                    JSONObject interfaceFile = (JSONObject)parser.parse(new FileReader(new File(directory.getPath() + "/" + f)));
                    JSONArray interfaceArray = (JSONArray)interfaceFile.get("interface");
                    String name = (String)interfaceFile.get("name");
                    if(name == null) { Display.displayPackError("An interface does not have a name. Omitting..."); continue; }
                    if(interfaceArray == null) { Display.displayPackError("Interface '" + name + "' does not have an interface array. Omitting..."); continue; }
                    if(usedNames.contains(name)) { continue; }
                    String uiString = "";
                    for (int i = 0; i < interfaceArray.size(); i++) { uiString += interfaceArray.get(i) + "\n"; }
                    Display.interfaces.add(new UserInterface(name, uiString));
                    usedNames.add(name);
                }
                directory = interfaceDir;
                parsingPack = false;
            }
            return true;
        } catch (FileNotFoundException e) { Display.displayError("Could not find an interface file. It was likely deleted after the program got all files in the interfaces directory."); return false; }
        catch (IOException e) { Display.displayError("IOException when attempting to load the interfaces. The permissions are likely set to be unreadable."); return false;}
        catch (ParseException e) { Display.displayError("Having trouble parsing the interface files. Will continue as expected though."); return false; }
    }
    public static boolean loadLocations() {
        try {
            Display.displayProgressMessage("Loading the locations...");
            //Search through the locations directory for all locations
            ArrayList<String> usedNames = new ArrayList<String>();
            boolean parsingPack = false;
            File directory = locationDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = " ";
                        String value = " ";
                        if(line.indexOf("=") != -1) {
                            key = line.substring(0,line.indexOf("="));
                            value = line.substring(line.indexOf("=")+1,line.length()).trim();
                        }
                        if(value == null || key == null || ! key.equals("modpack")) { continue; }
                        File modpack = new File(modpackDir + "/" + value);
                        if(modpackDir.list() != null && new ArrayList<>(Arrays.asList(modpackDir.list())).contains(value) && modpack.isDirectory()) {
                            directory = modpack;
                            Display.displayProgressMessage("loading locations from modpack '" + value + "'");
                            parsingPack = true;
                        } else {
                            Display.displayWarning("Mod pack '" + value + "' not found. Falling back to default locations.");
                        }
                    }
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the packs file (The file does exist). Falling back to default locations."); }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; }
                for (String f : directory.list()) {
                    if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                    JSONObject locationFile = (JSONObject)parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f)));
                    JSONArray interfaceJArray = (JSONArray)locationFile.get("interfaces");
                    String name = (String)locationFile.get("name");
                    if(name == null) { Display.displayPackError("A location does not have a name."); continue; }
                    if(interfaceJArray == null) { Display.displayPackError("Location '" + name + "' does not have any interfaces."); continue; }
                    if(usedNames.contains(name)) { continue; }
                    ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();
                    boolean hasChoiceInterface = false;
                    for(int i=0; i<interfaceJArray.size(); i++) {
                        for(UserInterface ui : Display.interfaces) {
                            if(ui.getName().equals(interfaceJArray.get(i))) {
                                if(interfaceJArray.get(i).equals("choices")) {
                                    hasChoiceInterface = true;
                                }
                                interfaces.add(ui);
                            }
                        }
                    }
                    if(!hasChoiceInterface) {
                        Display.displayWarning("The location '" + name + "' does not have the choices interface. Omitting...");
                        continue;
                    }
                    JSONArray choiceArray = (JSONArray)locationFile.get("choices");
                    ArrayList<Choice> choices = new ArrayList<Choice>();
                    //Loads the choices from the file
                    for (int i=0; i<choiceArray.size(); i++) {
                        JSONObject obj = (JSONObject)choiceArray.get(i);
                        JSONArray methodJSONArray = (JSONArray)obj.get("methods");
                        ArrayList<ChoiceMethod> methods = new ArrayList<ChoiceMethod>();
                        String choicename = (String)obj.get("name");
                        String desc = (String)obj.get("description");
                        String usage = (String)obj.get("usage");
                        if(choicename == null) { Display.displayPackError("A choice in location '" + name + "' has no name. Omitting..."); }
                        //Gets the methods
                        if(methodJSONArray != null && methodJSONArray.size() > 0) {
                            for(int p=0; p<methodJSONArray.size(); p++) {
                                JSONObject o = (JSONObject)methodJSONArray.get(p);
                                ArrayList<String> arguments = (JSONArray)o.get("arguments");
                                ArrayList<String> argumentTypesString = (JSONArray)o.get("argumentTypes");
                                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                                String method = (String)o.get("method");
                                String clazz = (String)o.get("class");
                                String field = (String)o.get("field");
                                if(method == null || clazz == null) { Display.displayPackError("The choice '" + choicename + "' in location '" + name + "' has no class or method. Omitting..."); continue; }
                                //Fields can be null (Which just means the method does not act upon a field)
                                if(argumentTypesString.size() > 0) {
                                    for (int g=0; g<argumentTypesString.size(); g++) {
                                        if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                            argumentTypes.add(int.class);
                                        } else if(Integer.parseInt(argumentTypesString.get(g)) == 0) {
                                            argumentTypes.add(String.class);
                                        }
                                    }
                                }
                                methods.add(new ChoiceMethod(method, arguments, argumentTypes, clazz, field));
                            }
                        } else { Display.displayPackError("The location '" + name + "' does not have any choices. Omitting."); continue; }
                        //Gets requirements if there is any
                        JSONArray requirementsJArray = (JSONArray)obj.get("requirements");
                        ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                        if(requirementsJArray != null && requirementsJArray.size() > 0) {
                            for(int p=0; p<requirementsJArray.size(); p++) {
                                JSONObject ro = (JSONObject)requirementsJArray.get(p);
                                ArrayList<String> arguments = (JSONArray)ro.get("requirementArgs");
                                ArrayList<String> argumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                                String method = (String)ro.get("method");
                                String clazz = (String)ro.get("class");
                                String field = (String)ro.get("field");
                                if(method == null || clazz == null) { Display.displayPackError("A requirement of choice '" + choicename + "' in location '" + name + "' has no class or method. Omitting..."); continue; }
                                //Fields can be null (Which just means the method does not act upon a field)
                                if(argumentTypesString != null && argumentTypesString.size() > 0) {
                                    for (int g=0; g<argumentTypesString.size(); g++) {
                                        if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                            argumentTypes.add(int.class);
                                        } else if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                            argumentTypes.add(String.class);
                                        }
                                    }
                                }
                                requirements.add(new Requirement(method, arguments, argumentTypes, clazz, field));
                            }
                        }
                        choices.add(new Choice(choicename, desc, usage, methods, requirements));
                    }
                    locations.add(new Location(name, interfaces, choices));
                    usedNames.add(name);
                }
                directory = locationDir;
                parsingPack = false;
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }
    public static boolean loadEnemies() {
        try {
            Display.displayProgressMessage("Loading the enemies...");
            ArrayList<String> usedNames = new ArrayList<String>();
            boolean parsingPack = false;
            File directory = enemyDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = "";
                        String value = "";
                        if(line.indexOf("=") != -1) {
                            key = line.substring(0,line.indexOf("="));
                            value = line.substring(line.indexOf("=")+1,line.length()).trim();
                        }
                        if(value == null || key == null || ! key.equals("enemypack")) { continue; }
                        File enemypack = new File(enemypackDir + "/" + value);
                        if(enemypackDir.list() != null && new ArrayList<>(Arrays.asList(enemypackDir.list())).contains(value) && enemypack.isDirectory()) {
                            directory = enemypack;
                            Display.displayProgressMessage("loading enemies from enemypack '" + value + "'");
                            parsingPack = true;
                        } else {
                            Display.displayWarning("Enemy pack '" + value + "' not found. Falling back to default enemies.");
                        }
                    }
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the packs file (The file does exist). Falling back to default locations."); }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; }
                for(String f : directory.list()) {
                    JSONObject enemyFile = (JSONObject) parser.parse(new FileReader(new File(enemyDir.getAbsolutePath() + "/" + f)));
                    JSONArray requirementsJArray = (JSONArray)enemyFile.get("requirements");
                    ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                    String name = (String)enemyFile.get("name");
                    int health = Integer.parseInt((String)enemyFile.get("health"));
                    int strength = Integer.parseInt((String)enemyFile.get("strength"));
                    int levelRequirement = Integer.parseInt((String)enemyFile.get("levelRequirement"));
                    if(name == null) { Display.displayPackError("An enemy does not have a name. Omitting..."); continue; }
                    if(usedNames.contains(name)) { continue; }
                    if(health < 1 || strength < 0) { Display.displayPackError("Enemy '" + name + "' does not have valid strength or health. Ommitting..."); continue; }
                    if(levelRequirement < 1) { levelRequirement=1; }
                    if(requirements != null && requirementsJArray.size() > 0) {
                        for(int p=0; p<requirementsJArray.size(); p++) {
                            JSONObject ro = (JSONObject)requirementsJArray.get(p);
                            ArrayList<String> arguments = (JSONArray)ro.get("requirementArgs");
                            ArrayList<String> argumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                            ArrayList<Class> argumentTypes = new ArrayList<Class>();
                            String method = (String)ro.get("method");
                            String clazz = (String)ro.get("class");
                            String field = (String)ro.get("field");
                            if(method == null || clazz == null) { Display.displayPackError("A requirement in enemy '" + name + "' does not have a class or field. Omitting..."); continue; }
                            //Fields can be null (Which just means the method does not act upon a field)
                            if(argumentTypesString.size() > 0) {
                                for (int g=0; g<argumentTypesString.size(); g++) {
                                    if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                        argumentTypes.add(int.class);
                                    } else if(Integer.parseInt(argumentTypesString.get(g)) == 1) {
                                        argumentTypes.add(String.class);
                                    }
                                }
                            }
                        requirements.add(new Requirement(method, arguments, argumentTypes, clazz, field));
                        }
                    }
                    enemies.add(new Enemy(name, health, strength, levelRequirement, requirements));
                    usedNames.add(name);
                    directory=enemyDir;
                    parsingPack=false;
                }
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }
    public static boolean loadParsingTags() {
        try {
            Display.displayProgressMessage("Loading the parsing tags...");
            ArrayList<String> usedNames = new ArrayList<String>();
            boolean parsingPack = false;
            File file = tagFile;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = " ";
                        String value = " ";
                        if(line.indexOf("=") != -1) {
                            key = line.substring(0,line.indexOf("="));
                            value = line.substring(line.indexOf("=")+1,line.length()).trim();
                        }
                        if(value == null || key == null || ! key.equals("intpack")) { continue; }
                        File intpack = new File(intpackDir + "/" + value);
                        if(intpackDir.list() != null && new ArrayList<>(Arrays.asList(intpackDir.list())).contains(value) && intpack.isDirectory()) {
                            if(new ArrayList<>(Arrays.asList(intpack.list())).contains("tags.json")) {
                                file = new File(intpack + "/tags.json");
                                Display.displayProgressMessage("loading tags from intpack '" + value + "'");
                                parsingPack = true;
                            }
                        } else {
                            Display.displayWarning("Interface pack '" + value + "' not found. Falling back to default tags.");
                        }
                    }
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the packs file (The file does exist). Falling back to default tags."); }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; }
                JSONObject tagsFile = (JSONObject)parser.parse(new FileReader(tagFile));
                JSONArray tagsArray = (JSONArray)tagsFile.get("tags");
                if(tagsArray == null) { continue; }
                for (int i = 0; i < tagsArray.size(); i++) {
                    JSONObject obj = (JSONObject)tagsArray.get(i);
                    ArrayList<String> arguments = (ArrayList<String>)obj.get("arguments");
                    ArrayList<String> argumentTypesString = (ArrayList<String>)obj.get("argumentTypes");
                    ArrayList<Class> argumentTypes = new ArrayList<Class>();
                    String tag = (String)obj.get("tag");
                    String classname = (String)obj.get("class");
                    String methodname = (String)obj.get("method");
                    if(tag == null) { Display.displayPackError("A tag does not have a name. Omitting tag..."); continue; }
                    if(usedNames.contains(tag)) { continue; }
                    if(classname == null || methodname == null) { Display.displayPackError("Tag '" + tag + "' does not have a class or method. Omitting tag..."); continue; }
                    Method method;
                    Class clazz;
                    try { clazz = Class.forName((String)obj.get("class")); } catch (ClassNotFoundException e) {
                        Display.displayWarning("Class not found '" + classname + "' Omitting tag '" + tag + "'");
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
                    try { method = clazz.getMethod(methodname, argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){
                        Display.displayWarning("Method '" + methodname + "' of class '" + classname + "' does not exist. Omitting tag '" + tag + "'..."); e.printStackTrace(); continue;}
                    Class returnType = method.getReturnType();
                    if(returnType != String.class && method.getReturnType() != ArrayList.class && method.getReturnType() != int.class ) {
                        Display.displayWarning("Tag '" + tag + "' method does not return String, int, or ArrayList! Omitting tag...");
                        continue;
                    }
                    if(argumentTypes.size() != arguments.size()) {
                        Display.displayWarning("Incorrect number of arguments for tag '" + tag + "'s method parameters! Omitting tag...");
                        continue;
                    }
                    Display.interfaceTags.add(new UiTag(tag, method, arguments, argumentTypes, clazz));
                    usedNames.add(tag);
                    parsingPack = false;
                    file = tagFile;
                }
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;

    }

    public static void loadConfig() {
        if(configDir.exists()) {
            Display.loadDesiredColors();
        } else {
            Display.displayWarning("The config directory could not be found!\n Creating new config directory.");
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
        setPossibleEnemies();
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
                addToOutput("Moved to " + location);
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
            if(input.trim() != null) {
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
            Display.displayError("An error occured while reading input!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean fight(String en) {
        System.out.println(en);
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

    public static boolean gameLoaded() {
        if(currentSaveFile != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void playGame() {
        //Display the interface for the user
        Display.displayInterfaces(player.getLocation());
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
            Display.displayError("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(1);
        }
        getSaveFiles();
        player.setLocation("saves");
        while(player.getAlive()) {
            playGame();
        }
    }

}
