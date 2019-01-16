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
import org.textfighter.*;
import org.textfighter.location.*;
import org.textfighter.enemy.*;
import org.textfighter.method.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")

public class TextFighter {

    public static boolean testMode = false;
    public static boolean parsingPack = false;
    public static boolean defaultpackmsgs = false;

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
    public static File achievementsDir;
    public static File configDir;
    public static File packFile;
    public static File packDir;
    public static File modpackDir;
    public static File intpackDir;
    public static File enemypackDir;
    public static File achievementpackDir;

    public static JSONParser parser = new JSONParser();

    public static ArrayList<Location> locations = new ArrayList<Location>();
    public static ArrayList<String> saves = new ArrayList<String>();
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public static ArrayList<Enemy> possibleEnemies = new ArrayList<Enemy>();
    public static ArrayList<Achievement> achievements = new ArrayList<Achievement>();

    public static boolean needsSaving = false;

    public static void addToOutput(String msg) { output+=msg + "\n"; }

    public static boolean loadResources() {
        Display.displayProgressMessage("Loading the resources...");
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir.getPath() + "/tags/tags.json");
        interfaceDir = new File(resourcesDir.getPath() + "/userInterfaces/");
        locationDir = new File(resourcesDir.getPath() + "/locations");
        savesDir = new File("../../../saves");
        enemyDir = new File(resourcesDir + "/enemies");
        achievementsDir = new File(resourcesDir + "/achievements");
        configDir = new File("../../../config");
        //Place where desired packs are defined
        packFile = new File(configDir.getPath() + "/packs");
        packDir = new File("../../../packs");
        //Place where all the modPacks are
        modpackDir = new File(packDir.getPath() + "/modpacks");
        //Place where all the interfacePacks are
        intpackDir = new File(packDir.getPath() + "/intpacks");
        //Place where all the enemyPacks are
        enemypackDir = new File(packDir.getPath() + "/enemypacks");
        //Place where are the achivementPacks are
        achievementpackDir = new File(packDir.getPath() + "/achivementpacks");
        loadConfig();
        if (!loadParsingTags() || !loadInterfaces() || !loadLocations() || !loadEnemies() || !loadAchievements()) { return false; }
        if (!savesDir.exists()) {
            Display.displayWarning("The saves directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { Display.displayError("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    public static Object loadMethod(Class type, JSONObject obj, Class parentType) {
        ArrayList<Object> argumentsRaw = (JSONArray)obj.get("arguments");
        ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
        ArrayList<Class> argumentTypes = new ArrayList<Class>();
        if(type.equals(UiTag.class) && obj.get("tag") != null) { Display.displayPackMessage("Loading tag '" + (String)obj.get("tag") + "'"); }
        String methodString = (String)obj.get("method");
        Display.displayPackMessage("Loading method '" + methodString + "' of type '" + type.getSimpleName() + "'");
        Display.changePackTabbing(true);
        String clazzString = (String)obj.get("class");
        String fieldString = (String)obj.get("field");
        String fieldclassString = (String)obj.get("fieldclass");
        if(methodString == null || clazzString == null) { Display.displayPackError("This method has no class or method. Omitting..."); return null; }
        if(!((argumentsRaw != null && argumentTypesString != null) && (argumentsRaw.size() == argumentTypesString.size()) || (argumentTypesString == null && argumentsRaw == null))) { Display.displayPackError("This method's does not have the same amount of arguments as argumentTypes. Omitting..."); return null; }
        //Fields and fieldclasses can be null (Which just means the method does not act upon a field
        if(argumentTypesString != null && argumentTypesString.size() > 0) {
            for (int g=0; g<argumentTypesString.size(); g++) {
                int num = Integer.parseInt(argumentTypesString.get(g));
                if(num == 0) {
                    argumentTypes.add(String.class);
                } else if(num == 1) {
                    argumentTypes.add(int.class);
                } else if(num == 2) {
                    argumentTypes.add(boolean.class);
                } else {
                    Display.displayPackError("This method has arguments that are not String, int, or boolean. Omitting...");
                    continue;
                }
            }
        }

        //Gets the class
        Class clazz = null;
        try { clazz = Class.forName(clazzString); } catch (ClassNotFoundException e){ Display.displayPackError("This method has an invalid class. Omitting..."); return null; }

        //Gets the fieldclass
        Class fieldclass = null;
        if(fieldclassString != null && fieldString != null) { try { fieldclass = Class.forName(fieldclassString); } catch (ClassNotFoundException e){ Display.displayPackError("This method has an invalid fieldclass. Omitting..."); return null; } }

        //Gets the field from the class
        Field field = null;
        try {
            if(fieldString != null) {
                field = fieldclass.getField(fieldString);
            }
        } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This method has an invalid field. Omitting..."); return null; }

        //Gets the method from the class
        Method method = null;
        try {
            if(argumentTypes != null ) {
                method = clazz.getMethod(methodString, argumentTypes.toArray(new Class[argumentTypes.size()]));
            } else {
                method = clazz.getMethod(methodString);
            }
        } catch (NoSuchMethodException e){ Display.displayPackError("Method '" + methodString + "' of class '" + clazzString + "' could not be found. Omitting..."); return null; }

        //Makes the arguments the correct type (String, int, or boolean)
        ArrayList<Object> arguments = new ArrayList<Object>();
        if(argumentsRaw != null) {
            for (int p=0; p<argumentTypes.size(); p++) {
                if(argumentsRaw.get(p).getClass().equals(JSONObject.class)) {
                    arguments.add(loadMethod(TFMethod.class, (JSONObject)argumentsRaw.get(p), type));
                } else {
                    if(argumentTypes.get(p).equals(int.class)) {
                        arguments.add(Integer.parseInt((String)argumentsRaw.get(p)));
                    } else if (argumentTypes.get(p).equals(String.class)) {
                        if(argumentsRaw.get(p).equals("%null%")) { arguments.add(null); }
                        else { arguments.add((String)argumentsRaw.get(p)); }
                    } else if (argumentTypes.get(p).equals(boolean.class)) {
                        arguments.add(Boolean.parseBoolean((String)argumentsRaw.get(p)));
                    } else {
                        Display.displayPackError("This method has arguments that are not String, int, or boolean. Omitting...");
                        continue;
                    }
                }
            }
        }

        Object o = null;

        //Creates the correct Method type
        if(type.equals(ChoiceMethod.class)) {
            o=new ChoiceMethod(method, arguments, argumentTypes, clazz, field, fieldclass);
        } else if(type.equals(Requirement.class)) {
            String neededBooleanString = (String)obj.get("neededBoolean");
            boolean neededBoolean = Boolean.parseBoolean(neededBooleanString);
            if(neededBooleanString == null) { neededBoolean = true; }
            o=new Requirement(method, arguments, clazz, field, fieldclass, neededBoolean);
        } else if(type.equals(EnemyActionMethod.class)) {
            o=new EnemyActionMethod(method, arguments, clazz, field, fieldclass);
        } else if(type.equals(TFMethod.class)) {
            o=new TFMethod(method, arguments, clazz, field, fieldclass, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), TFMethod.class));
        } else if(type.equals(Reward.class)) {
            if(parentType.equals(Enemy.class)) {
                int chance = Integer.parseInt((String)obj.get("chance"));
                if(chance == 0) { Display.displayPackError("This reward have no chance. Omitting..."); return null; }
                o=new Reward(method, arguments, clazz, field, fieldclass, loadMethods(Reward.class, (JSONArray)obj.get("requirements"), Enemy.class), chance, (String)obj.get("rewarditem"));
            } else {
                o=new Reward(method, arguments, clazz, field, fieldclass, loadMethods(Reward.class, (JSONArray)obj.get("requirements"), Enemy.class), 100, (String)obj.get("rewarditem"));
            }
        } else if(type.equals(UiTag.class)) {
            String tag = (String)obj.get("tag");
            if(tag == null) { Display.displayPackError("This tag has no tagname. Omitting..."); return null; }
            o=new UiTag(tag, method, arguments, clazz, field, fieldclass);
        }
        Display.changePackTabbing(false);
        return o;
    }

    public static ArrayList loadMethods(Class type, JSONArray methodArray, Class parentType) {
        ArrayList<Object> methods = new ArrayList<Object>();
        if((methodArray != null && methodArray.size() > 0)) {
            for(int i=0; i<methodArray.size(); i++) {
                JSONObject o = (JSONObject)methodArray.get(i);
                Object method = loadMethod(type, o, parentType);
                if(method != null) { methods.add(method); }
            }
            return methods;
        } else { return null; }
    }

    public static boolean loadInterfaces() {
        try {
            Display.displayProgressMessage("Loading the interfaces...");
            if(!interfaceDir.exists()) { Display.displayError("Could not find the default interfaces directory."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = interfaceDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = "";
                        String value = "";
                        if(key == null && value == null) { continue; }
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

            ArrayList<String> namesToBeOmitted = new ArrayList<String>();
            if(parsingPack) {
                File omissionFile = new File(directory + "/" + "omit.txt");
                if(omissionFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            namesToBeOmitted.add(line);
                        }
                    } catch (IOException e) { Display.displayWarning("IOException when attempting to read the interfacepack's omit file (The file does exist). Continuing normally..."); }
                }
            }

            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading interfaces from the default pack."); }
                if(directory.list() != null) {
                    for (String f : directory.list()) {
                        if(f.equals("tags.json")) { continue; }
                        if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                        JSONObject interfaceFile = (JSONObject)parser.parse(new FileReader(new File(directory.getPath() + "/" + f)));
                        JSONArray interfaceArray = (JSONArray)interfaceFile.get("interface");
                        String name = (String)interfaceFile.get("name");
                        Display.displayPackMessage("Loading interface '" + name + "'");
                        Display.changePackTabbing(true);
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
                        if(name == null) { Display.displayPackError("This does not have a name. Omitting..."); continue; }
                        if(interfaceArray == null) { Display.displayPackError("This does not have an interface array. Omitting..."); continue; }
                        String uiString = "";
                        for (int i = 0; i < interfaceArray.size(); i++) { uiString += interfaceArray.get(i) + "\n"; }
                        Display.interfaces.add(new UserInterface(name, uiString));
                        usedNames.add(name);
                        Display.changePackTabbing(false);
                    }
                }
                directory = interfaceDir;
                parsingPack = false;
            }
            Display.displayProgressMessage("Interfaces loaded.");
            Display.changePackTabbing(false);
            return true;
        } catch (FileNotFoundException e) { Display.displayError("Could not find an interface file. It was likely deleted after the program got all files in the interfaces directory."); Display.changePackTabbing(false); return false; }
        catch (IOException e) { Display.displayError("IOException when attempting to load the interfaces. The permissions are likely set to be unreadable."); Display.changePackTabbing(false); return false;}
        catch (ParseException e) { Display.displayError("Having trouble parsing the interface files. Will continue as expected though."); Display.changePackTabbing(false); return false; }
    }
    public static boolean loadLocations() {
        try {
            Display.displayProgressMessage("Loading the locations...");
            Display.changePackTabbing(true);
            if(!locationDir.exists()) { Display.displayError("Could not find the default locations directory."); return false;}
            //Search through the locations directory for all locations
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = locationDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = " ";
                        String value = " ";
                        if(key == null && value == null) { continue; }
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
            ArrayList<String> namesToBeOmitted = new ArrayList<String>();
            if(parsingPack) {
                File omissionFile = new File(directory + "/" + "omit.txt");
                if(omissionFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            namesToBeOmitted.add(line);
                        }
                    } catch (IOException e) { Display.displayWarning("IOException when attempting to read the modpack's omit file (The file does exist). Continuing normally..."); }
                }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading locations from the default pack."); }
                if(directory.list() != null) {
                    for (String f : directory.list()) {
                        if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                        JSONObject locationFile = (JSONObject)parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f)));
                        JSONArray interfaceJArray = (JSONArray)locationFile.get("interfaces");
                        String name = (String)locationFile.get("name");
                        Display.displayPackMessage("Loading Location '" + name + "'");
                        Display.changePackTabbing(true);
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
                        if(name == null) { Display.displayPackError("This location does not have a name. Omitting..."); continue; }
                        if(interfaceJArray == null) { Display.displayPackError("Location '" + name + "' does not have any interfaces. Omitting..."); continue; }
                        ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();
                        boolean hasChoiceInterface = false;
                        for(int i=0; i<interfaceJArray.size(); i++) {
                            for(UserInterface ui : Display.interfaces) {
                                if(ui.getName().equals(interfaceJArray.get(i))) {
                                    if(((String)interfaceJArray.get(i)).contains("choices")) {
                                        hasChoiceInterface = true;
                                    }
                                    interfaces.add(ui);
                                }
                            }
                        }
                        if(!hasChoiceInterface) {
                            Display.displayWarning("This does not have a choices interface. Omitting...");
                            continue;
                        }
                        JSONArray choiceArray = (JSONArray)locationFile.get("choices");
                        ArrayList<Choice> choices = new ArrayList<Choice>();
                        //Loads the choices from the file
                        if(choiceArray != null && choiceArray.size() > 0) {
                            for (int i=0; i<choiceArray.size(); i++) {
                                JSONObject obj = (JSONObject)choiceArray.get(i);
                                String choicename = (String)obj.get("name");
                                Display.displayPackMessage("Loading choice '" + choicename + "'");
                                Display.changePackTabbing(true);
                                String desc = (String)obj.get("description");
                                String usage = (String)obj.get("usage");
                                if(choicename == null) { Display.displayPackError("A choice in location '" + name + "' has no name. Omitting..."); }
                                choices.add(new Choice(choicename, desc, usage, loadMethods(ChoiceMethod.class, (JSONArray)obj.get("methods"), Choice.class), loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), Choice.class)));
                                Display.changePackTabbing(false);
                            }
                        }

                        if(!usedNames.contains("quit")) {
                            Display.displayPackMessage("Adding the quit choice");
                            Display.changePackTabbing(true);

                            Method method;
                            try {
                                method = TextFighter.class.getMethod("exitGame", new Class[] {int.class});
                            } catch (NoSuchMethodException e){ Display.displayPackError("Cannot find method 'exitGame'. Omitting..."); continue; }
                            ArrayList<Object> arguments = new ArrayList<Object>(); arguments.add(0);
                            ArrayList<Class> argumentTypes = new ArrayList<Class>(); argumentTypes.add(int.class);
                            ArrayList<ChoiceMethod> choiceMethods = new ArrayList<ChoiceMethod>(); choiceMethods.add(new ChoiceMethod(method, arguments, argumentTypes, org.textfighter.TextFighter.class, null, null));
                            choices.add(new Choice("quit", "quits the game", "quit", choiceMethods, null));
                            Display.changePackTabbing(false);
                        }
                        //Adds the loaded location
                        locations.add(new Location(name, interfaces, choices, loadMethods(TFMethod.class, (JSONArray)locationFile.get("premethods"), Location.class), loadMethods(TFMethod.class, (JSONArray)locationFile.get("postmethods"), Location.class)));
                        usedNames.add(name);
                        Display.changePackTabbing(false);
                        directory = locationDir;
                        parsingPack = false;
                    }
                }
                Display.displayProgressMessage("Locations loaded.");
                Display.changePackTabbing(false);
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); Display.changePackTabbing(false); return false; }
        Display.changePackTabbing(false);
        return true;
    }
    public static boolean loadEnemies() {
        try {
            Display.displayProgressMessage("Loading the enemies...");
            if(!enemyDir.exists()) { Display.displayError("Could not find the default enemies directory."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = enemyDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = "";
                        String value = "";
                        if(key == null && value == null) { continue; }
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
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the enemypack's file (The file does exist). Falling back to default enemies."); }
            }
            ArrayList<String> namesToBeOmitted = new ArrayList<String>();
            if(parsingPack) {
                File omissionFile = new File(directory + "/" + "omit.txt");
                if(omissionFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            namesToBeOmitted.add(line);
                        }
                    } catch (IOException e) { Display.displayWarning("IOException when attempting to read the pack's omit file (The file does exist). Continuing normally..."); }
                }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading enemies from the default pack."); }
                if(directory.list() != null) {
                    for(String f : directory.list()) {
                        JSONObject enemyFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f)));
                        String name = (String)enemyFile.get("name");
                        Display.displayPackMessage("Loading enemy '" + name + "'");
                        Display.changePackTabbing(true);
                        int health = Integer.parseInt((String)enemyFile.get("health"));
                        int strength = Integer.parseInt((String)enemyFile.get("strength"));
                        int levelRequirement = Integer.parseInt((String)enemyFile.get("levelRequirement"));
                        boolean finalBoss = Boolean.parseBoolean((String)enemyFile.get("finalBoss"));
                        if(name == null) { Display.displayPackError("This enemy does not have a name. Omitting..."); continue; }
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
                        if(health < 1 || strength < 0) { Display.displayPackError("Enemy '" + name + "' does not have valid strength or health. Ommitting..."); continue; }
                        if(levelRequirement < 1) { levelRequirement=1; }
                        JSONArray enemyActionArray = (JSONArray)enemyFile.get("actions");
                        ArrayList<EnemyAction> enemyActions = new ArrayList<EnemyAction>();
                        if(enemyActionArray != null && enemyActionArray.size() > 0) {
                            for (int i=0; i<enemyActionArray.size(); i++) {
                                JSONObject obj = (JSONObject)enemyActionArray.get(i);
                                enemyActions.add(new EnemyAction(loadMethods(EnemyActionMethod.class, (JSONArray)obj.get("methods"), EnemyAction.class), loadMethods(EnemyActionMethod.class, (JSONArray)obj.get("requirements"), Enemy.class)));
                            }
                        }
                        enemies.add(new Enemy(name, health, strength, levelRequirement, loadMethods(Requirement.class, (JSONArray)enemyFile.get("requirements"), Enemy.class), Boolean.parseBoolean((String)enemyFile.get("finalBoss")), loadMethods(TFMethod.class, (JSONArray)enemyFile.get("preMethods"), Enemy.class), loadMethods(TFMethod.class, (JSONArray)enemyFile.get("postMethods"), Enemy.class), loadMethods(Reward.class, (JSONArray)enemyFile.get("rewardMethods"), Enemy.class), enemyActions));
                        usedNames.add(name);
                        directory=enemyDir;
                        parsingPack=false;
                        Display.changePackTabbing(false);
                    }
                }
                Display.displayProgressMessage("Enemies loaded.");
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); Display.changePackTabbing(false); return false; }
        Display.changePackTabbing(false);
        return true;
    }
    public static boolean loadParsingTags() {
        try {
            Display.displayProgressMessage("Loading the parsing tags...");
            if(!tagFile.exists()) { Display.displayError("Could not find the default tags file."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = intpackDir;
            File file = tagFile;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = " ";
                        String value = " ";
                        if(key == null && value == null) { continue; }
                        if(line.indexOf("=") != -1) {
                            key = line.substring(0,line.indexOf("="));
                            value = line.substring(line.indexOf("=")+1,line.length()).trim();
                        }
                        if(value == null || key == null || ! key.equals("intpack")) { continue; }
                        File intpack = new File(intpackDir + "/" + value);
                        if(intpackDir.list() != null && new ArrayList<>(Arrays.asList(intpackDir.list())).contains(value) && intpack.isDirectory()) {
                            if(new ArrayList<>(Arrays.asList(intpack.list())).contains("tags.json")) {
                                directory = intpack;
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
                if(!parsingPack) { num++; Display.displayPackMessage("Loading parsing tags from the default pack."); }
                JSONObject tagsFile = (JSONObject)parser.parse(new FileReader(tagFile));
                JSONArray tagsArray = (JSONArray)tagsFile.get("tags");
                if(tagsArray == null) { continue; }
                Display.interfaceTags = loadMethods(UiTag.class, (JSONArray)tagsFile.get("tags"), null);
                parsingPack = false;
                file = tagFile;
                Display.displayProgressMessage("Parsing tags loaded.");
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); Display.changePackTabbing(false); return false; }
        Display.changePackTabbing(false);
        return true;
    }

    public static boolean loadAchievements() {
        try {
            Display.displayProgressMessage("Loading the achievements...");
            if(!achievementsDir.exists()) { Display.displayError("Could not find the default achievements directory."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = achievementsDir;
            if(packFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String key = "";
                        String value = "";
                        if(key == null && value == null) { continue; }
                        if(line.indexOf("=") != -1) {
                            key = line.substring(0,line.indexOf("="));
                            value = line.substring(line.indexOf("=")+1,line.length()).trim();
                        }
                        if(value == null || key == null || !key.equals("achievementpack")) { continue; }
                        File achievementpack = new File(achievementpackDir + "/" + value);
                        if(achievementpackDir.list() != null && new ArrayList<>(Arrays.asList(achievementpackDir.list())).contains(value) && achievementpack.isDirectory()) {
                            directory = achievementpack;
                            Display.displayProgressMessage("loading achievements from achievementpack '" + value + "'");
                            parsingPack = true;
                        } else {
                            Display.displayWarning("Achievement pack '" + value + "' not found. Falling back to default achievements.");
                        }
                    }
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the pack file (The file does exist). Falling back to default achievements."); }
            }
            ArrayList<String> namesToBeOmitted = new ArrayList<String>();
            if(parsingPack) {
                File omissionFile = new File(directory + "/" + "omit.txt");
                if(omissionFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            namesToBeOmitted.add(line);
                        }
                    } catch (IOException e) { Display.displayWarning("IOException when attempting to read the pack's omit file (The file does exist). Continuing normally..."); }
                }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading achievements from the default pack."); }
                if(directory.list() != null) {
                    for(String f : directory.list()) {
                        JSONObject achievementFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f)));
                        String name = (String)achievementFile.get("name");
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
                        Display.displayPackMessage("Loading achievement '" + name + "'");
                        Display.changePackTabbing(true);
                        achievements.add(new Achievement(name, loadMethods(Requirement.class, (JSONArray)achievementFile.get("requirements"), Achievement.class), loadMethods(Reward.class, (JSONArray)achievementFile.get("rewards"), Achievement.class)));
                        Display.changePackTabbing(false);
                        usedNames.add(name);
                        directory=enemyDir;
                        parsingPack=false;
                    }
                }
                Display.displayProgressMessage("Achievements loaded.");
                Display.changePackTabbing(false);
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }

    public static void loadConfig() {
        if(configDir.exists()) {
            Display.loadDesiredColors();
            Display.displayProgressMessage("Config loaded.");
        } else {
            Display.displayWarning("The config directory could not be found!\n Creating new config directory.");
            configDir.mkdirs();
        }
    }

    public static boolean loadGame(String name) {

        if(!areThereAnySaves()){ addToOutput("There are no saves, create one."); return false;}

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
            boolean gameBeaten = Boolean.parseBoolean((String)stats.get("gameBeaten"));

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

            ArrayList<Achievement> playerAchievements = new ArrayList<Achievement>();
            JSONArray achievementsArray = (JSONArray)file.get("achievements");

            if(achievementsArray != null) {
                for(int i=0; i<achievementsArray.size(); i++) {
                    for(Achievement a : achievements) {
                        if(achievementsArray.get(i) == a.getName()) {
                            playerAchievements.add(a);
                        }
                    }
                }
            }

            player = new Player(hp, maxhp, coins, magic, level, experience, score, gameBeaten, newInventory, playerAchievements);
            addToOutput("Loaded save '" + name + "'");

        } catch (IOException | ParseException e) { addToOutput("Unable to read the save"); e.printStackTrace(); return false; }

        return true;

    }
    public static void newGame(String name) {

        if(getSaveFiles().contains(name)) {
            addToOutput("There is already a save with that name. Pick another.");
            return;
        }

        File newGameFile = new File(savesDir.getPath() + "/" + name + ".json");
        try { newGameFile.createNewFile();} catch (IOException e) {
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

        player = new Player(Player.defaulthp, Player.defaulthp, 0, 0, 1, 0, 0, false, newInventory, new ArrayList<Achievement>());

        addToOutput("Added new save '" + name + "'");

        try (FileWriter w = new FileWriter(newGameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static boolean saveGame() {
        //Rewrite the whole file

        if(currentSaveFile != null && !currentSaveFile.exists()) { try { currentSaveFile.createNewFile(); } catch (IOException e) { addToOutput("Unable to save game!"); return false; }}

        JSONObject base = new JSONObject();

        JSONObject stats = new JSONObject();
        stats.put("level", Integer.toString(player.getLevel()));
        stats.put("experience", Integer.toString(player.getExperience()));
        stats.put("score", Integer.toString(player.getScore()));
        stats.put("health", Integer.toString(player.getHp()));
        stats.put("maxhealth", Integer.toString(player.getMaxHp()));
        stats.put("coins", Integer.toString(player.getCoins()));
        stats.put("magic", Integer.toString(player.getMagic()));
        stats.put("gameBeaten", Boolean.toString(player.getGameBeaten()));

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

        JSONArray earnedAchievements = new JSONArray();

        //For achievements
        if(player.getAchievements() != null) {
            for(Achievement a : player.getAchievements()) {
                earnedAchievements.add(a.getName());
            }
        }

        base.put("achievements", earnedAchievements);
        base.put("inventory", inventory);
        base.put("stats", stats);
        base.put("name", gameName);

        try (FileWriter w = new FileWriter(currentSaveFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); return false; }

        return true;

    }

    public static void addSave(String name) { saves.add(name); }
    public static void removeSave(String name) {
        getSaveFiles();
        if(currentSaveFile != null && name.equals(currentSaveFile.getName()) && player.getGameBeaten()) { return; }
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
    public static boolean areThereAnySaves() { return(getSaveFiles().size() > 0); }
    public static ArrayList<String> getSaveFiles() {
        ArrayList<String> filteredSaves = new ArrayList<String>();
        for(String s : savesDir.list()) {
            if(s.substring(s.lastIndexOf(".")).equals(".json")) {
                filteredSaves.add(s.substring(0,s.lastIndexOf(".")));
            }
        }
        saves = filteredSaves;
        return filteredSaves;
    }
    public static ArrayList<String> getChoiceOutputs() {
        Location l = player.getLocation();
        ArrayList<String> outputs = new ArrayList<String>();
        if(l != null && l.getPossibleChoices() != null) {
            for(Choice c : l.getPossibleChoices()) {
                if(c.getOutput() != null){
                    outputs.add(c.getOutput());
                }
            }
        }
        return outputs;
    }
    public static void setPossibleEnemies() {
        sortEnemies();
        ArrayList<Enemy> possible = new ArrayList<Enemy>();
        for(Enemy e : enemies) {
            boolean valid = true;
            if(e.getLevelRequirement() >= player.getLevel()) {
                if(e.getRequirements() != null) {
                    for(Requirement r : e.getRequirements()) {
                        if(!r.invokeMethod()) {
                            valid=false;
                            break;
                        }
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
                //Invokes the post methods of the previous location
                player.getLocation().invokePostmethods();
                player.setLocation(location);
                addToOutput("Moved to " + location);
                //Invokes the pre methods of the previous location
                l.invokePremethods();
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
        boolean validEnemy = false;
        for(int i=0; i<enemies.size(); i++) {
            if (enemies.get(i).getName().equals(en)) {
                validEnemy = true;
                try { currentEnemy = (Enemy)enemies.get(i).clone(); } catch (CloneNotSupportedException e) { addToOutput("Could not create an enemy of that type! ('" + e + "')"); e.printStackTrace(); return false; }
                break;
            }
        }
        if(validEnemy) {
            currentEnemy.invokePremethods();
            while(player.getInFight()) {
                playGame();
                //Make them fight
                Random random = new Random(currentEnemy.getPossibleActions().size());
                currentEnemy.getPossibleActions().get(random.nextInt(currentEnemy.getPossibleActions().size())).invokeMethods();
                player.setCanBeHurtThisTurn(true);
                if(player.getHp() < 1) {
                    movePlayer("dead");
                    player.setAlive(false);
                    player.setInFight(false);
                } else if(currentEnemy.getHp() < 1) {
                    player.setInFight(false);
                    addToOutput("You defeated the '" + currentEnemy.getName() + "'!");
                    currentEnemy.invokePostmethods();
                    currentEnemy.invokeRewardMethods();
                    addToOutput("Rewards:");
                    currentEnemy.invokeRewardMethods();
                    if(currentEnemy.getIsFinalBoss()) { playerWins(); }
                    currentEnemy=null;
                    return true;
                }
            }
        } else {
            addToOutput("Invalid enemy: '" + en + "'");
            return false;
        }
        return false;
    }

    public static void playerWins() {
        movePlayer("Win");
    }

    public static void exitGame(int code) { System.exit(code); }

    public static boolean gameLoaded() { return (currentSaveFile != null); }

    public static void playGame() {
        //Display the interface for the user
        Display.displayInterfaces(player.getLocation());
        invokePlayerInput();
        Display.clearScreen();
        Display.displayPreviousCommand();
        for(Achievement a : achievements) {
            if(!player.getAchievements().contains(a)) {
                boolean earned = true;
                for(Requirement r : a.getRequirements()) {
                    if(!r.invokeMethod() == r.getNeededBoolean()) {
                        earned = false;
                        break;
                    }
                }
                if(earned) {
                    player.achievementEarned(a);
                }
            }
        }
        if(output != null) {
            Display.displayOutputMessage(output);
        }
        output="";
        if(needsSaving && currentSaveFile != null) { saveGame(); }
        needsSaving = false;
    }

    public static void main(String[] args) {
        for(String a : args) {
            if(a.equals("testpacks")) {
                testMode = true;
            } else if (a.equals("defaultpackmsgs") && testMode) {
                defaultpackmsgs = true;
            }
        }
        if(!testMode) { Display.clearScreen(); }
        if (!loadResources()) {
            Display.displayError("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(1);
        }
        if(!testMode) {
            getSaveFiles();
            player.setLocation("saves");
            while(player.getAlive() || player.getGameBeaten()) {
                playGame();
            }
        }
    }

    public static void nullTest(String e) { return; }

}
