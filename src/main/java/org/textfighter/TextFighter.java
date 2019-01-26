package org.textfighter;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import java.lang.reflect.*;

import org.textfighter.display.*;
import org.textfighter.item.*;
import org.textfighter.*;
import org.textfighter.location.*;
import org.textfighter.enemy.*;
import org.textfighter.method.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")

public class TextFighter {

    // Pack testing variables
    public static boolean testMode = false;
    public static boolean parsingPack = false;

    //Version
    public static File versionFile = new File("../../../VERSION.txt");
    public static String version;

    // Important game variables
    public static String gameName;

    public static Player player = new Player();
    public static Enemy currentEnemy = new Enemy();

    public static File currentSaveFile;

    public static String output = "";

    public static File packUsed;

    // All default pack directories
    public static File resourcesDir;
    public static File tagFile;
    public static File defaultValuesDirectory;
    public static File interfaceDir;
    public static File locationDir;
    public static File enemyDir;
    public static File savesDir;
    public static File achievementDir;
    public static File itemDir;

    // Config
    public static File configDir;

    // All pack directories
    public static File packFile;
    public static File packDir;

    public static JSONParser parser = new JSONParser();

    public static ArrayList<Location> locations = new ArrayList<Location>();
    public static ArrayList<String> saves = new ArrayList<String>();
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public static ArrayList<Enemy> possibleEnemies = new ArrayList<Enemy>();
    public static ArrayList<Achievement> achievements = new ArrayList<Achievement>();
    public static ArrayList<Armor> armors = new ArrayList<Armor>();
    public static ArrayList<Weapon> weapons = new ArrayList<Weapon>();
    public static ArrayList<SpecialItem> specialItems = new ArrayList<SpecialItem>();
    public static ArrayList<Tool> tools = new ArrayList<Tool>();

    public static boolean needsSaving = false;

    public static void addToOutput(String msg) { output+=msg + "\n"; }

    public static String readVersionFromFile() {
        if(versionFile == null) { Display.displayWarning("Could not read the version from file"); return "Unknown"; }
        try (BufferedReader br = new BufferedReader(new FileReader(versionFile))) {
            String line = br.readLine();
            if(line != null) { return line; } else { return "Unknown"; }
        } catch(IOException e) { Display.displayWarning("Could not read the version from file");}
        return "Unknown";
    }

    public static String getVersion() { return version; }

    public static boolean loadResources() {
        Display.displayProgressMessage("Loading the resources...");
        //Loads all the directories
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir.getPath() + "/tags/tags.json");
        defaultValuesDirectory = new File(resourcesDir.getPath() + "/defaultvalues");
        interfaceDir = new File(resourcesDir.getPath() + "/userInterfaces/");
        locationDir = new File(resourcesDir.getPath() + "/locations");
        savesDir = new File("../../../saves");
        enemyDir = new File(resourcesDir.getPath() + "/enemies");
        achievementDir = new File(resourcesDir.getPath() + "/achievements");
        itemDir = new File(resourcesDir.getPath() + "/items");
        configDir = new File("../../../config");
        packFile = new File(configDir.getPath() + "/pack");
        packDir = new File("../../../packs");
        version = readVersionFromFile();
        loadConfig();
        loadDefaultValues();

        //loads the content
        //If any of the necessary content fails to load, send an error and exit the game
        if (!loadItems() || !loadParsingTags() || !loadInterfaces() || !loadLocations() || !loadEnemies() || !loadAchievements()) { return false; }
        if (!savesDir.exists()) {
            Display.displayWarning("The saves directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { Display.displayError("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    /*
      Loads a singular method
      Useful for loading a Method that is an argument that you wouldnt pass a JSONArray to
    */
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
        if(methodString == null || clazzString == null) { Display.displayPackError("This method has no class or method. Omitting..."); Display.changePackTabbing(false); return null; }
        if(!((argumentsRaw != null && argumentTypesString != null) && (argumentsRaw.size() == argumentTypesString.size()) || (argumentTypesString == null && argumentsRaw == null))) { Display.displayPackError("This method's does not have the same amount of arguments as argumentTypes. Omitting..."); Display.changePackTabbing(false); return null; }
        //Fields and fieldclasses can be null (Which just means the method does not act upon a field
        if(argumentTypesString != null && argumentTypesString.size() > 0) {
            for (int g=0; g<argumentTypesString.size(); g++) {
                int num = Integer.parseInt(argumentTypesString.get(g));
                if(num == 0) {
                    argumentTypes.add(String.class);
                } else if(num == 1) {
                    argumentTypes.add(int.class);
                } else if(num == 2) {
                    argumentTypes.add(double.class);
                } else if(num == 3) {
                    argumentTypes.add(boolean.class);
                } else if(num == 4) {
                    argumentTypes.add(Class.class);
                } else {
                    Display.displayPackError("This method has arguments that are not String, int, double, boolean, or class. Omitting...");
                    Display.changePackTabbing(false);
                    continue;
                }
            }
        }

        //Gets the class
        Class clazz = null;
        try { clazz = Class.forName(clazzString); } catch (ClassNotFoundException e){ Display.displayPackError("This method has an invalid class. Omitting..."); Display.changePackTabbing(false); return null; }

        //Gets the fieldclass
        Class fieldclass = null;
        if(fieldclassString != null && fieldString != null) { try { fieldclass = Class.forName(fieldclassString); } catch (ClassNotFoundException e){ Display.displayPackError("This method has an invalid fieldclass. Omitting..."); Display.changePackTabbing(false); return null; } }

        //Gets the field from the class
        Field field = null;
        try {
            if(fieldString != null && fieldclassString != null) {
                field = fieldclass.getField(fieldString);
            }
        } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This method has an invalid field. Omitting..."); Display.changePackTabbing(false); return null; }

        //Gets the method from the class
        Method method = null;
        try {
            if(argumentTypes != null ) {
                method = clazz.getMethod(methodString, argumentTypes.toArray(new Class[argumentTypes.size()]));
            } else {
                method = clazz.getMethod(methodString);
            }
        } catch (NoSuchMethodException e){ Display.displayPackError("Method '" + methodString + "' of class '" + clazzString + "' with given arguments could not be found. Omitting..."); Display.changePackTabbing(false); return null; }

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
                    } else if(argumentTypes.get(p).equals(double.class)) {
                        arguments.add(Double.parseDouble((String)argumentsRaw.get(p)));
                    } else if (argumentTypes.get(p).equals(boolean.class)) {
                        arguments.add(Boolean.parseBoolean((String)argumentsRaw.get(p)));
                    } else if (argumentTypes.get(p).equals(Class.class)) {
                        try { arguments.add(Class.forName((String)argumentsRaw.get(p))); } catch(ClassNotFoundException e) { Display.displayPackError("An argument tried to get a class that does not exist. Omitting... "); Display.changePackTabbing(false); return null; }
                    } else {
                        Display.displayPackError("This method has arguments that are not String, int, double, boolean, or class. Omitting...");
                        Display.changePackTabbing(false);
                        continue;
                    }
                }
            }
        }

        Object o = null;

        //Creates the correct Method type
        if(type.equals(ChoiceMethod.class)) {
            o=new ChoiceMethod(method, arguments, argumentTypes, clazz, field, fieldclass, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), ChoiceMethod.class));
        } else if(type.equals(Requirement.class)) {
            boolean neededBoolean = Requirement.defaultNeededBoolean; if(obj.get("neededBoolean") != null) {neededBoolean=Boolean.parseBoolean((String)obj.get("neededBoolean"));}
            o=new Requirement(method, arguments, clazz, field, fieldclass, neededBoolean);
        } else if(type.equals(EnemyActionMethod.class)) {
            o=new EnemyActionMethod(method, arguments, clazz, field, fieldclass);
        } else if(type.equals(TFMethod.class)) {
            o=new TFMethod(method, arguments, clazz, field, fieldclass, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), TFMethod.class));
        } else if(type.equals(Reward.class)) {
            if(parentType.equals(Enemy.class)) {
                int chance = Reward.defaultChance; if((String)obj.get("chance") != null){chance=Integer.parseInt((String)obj.get("chance"));}
                if(chance == 0) { Display.displayPackError("This reward have no chance. Omitting..."); return null; }
                o=new Reward(method, arguments, clazz, field, fieldclass, loadMethods(Reward.class, (JSONArray)obj.get("requirements"), Enemy.class), chance, (String)obj.get("rewarditem"));
            } else {
                o=new Reward(method, arguments, clazz, field, fieldclass, loadMethods(Reward.class, (JSONArray)obj.get("requirements"), Enemy.class), 100, (String)obj.get("rewarditem"));
            }
        } else if(type.equals(UiTag.class)) {
            String tag = (String)obj.get("tag");
            if(tag == null) { Display.displayPackError("This tag has no tagname. Omitting..."); Display.changePackTabbing(false); return null; }
            o=new UiTag(tag, method, arguments, clazz, field, fieldclass, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), UiTag.class));
        }
        Display.changePackTabbing(false);
        return o;
    }

    /*
      Loads the methods in things like requirements, choicemethods, and others
      Used if you have a JSONArray full of methods (And only methods) to parse
    */
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
            ArrayList<String> usedNames = new ArrayList<String>(); // Used to override default interfaces with ones in packs
            File directory = interfaceDir;
            //Determine if there is a pack to be loaded
            if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
                for(String s : packUsed.list()) {
                    if(s.equals("interfaces")) {
                        File pack = new File(packUsed.getPath() + "/interfaces");
                        if(pack.exists() && pack.isDirectory()) {
                            directory = pack;
                            Display.displayPackMessage("loading interfaces from pack '" + packUsed.getName() + "'");
                            parsingPack = true;
                        }
                    }
                }
            }

            ArrayList<String> namesToBeOmitted = new ArrayList<String>(); //Place where all names that are located in the omit file are stored
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

            //Now parses the interfaces
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading interfaces from the default pack."); }
                if(directory.list() != null) {
                    for (String f : directory.list()) {
                        if(f.equals("tags.json")) { continue; }
                        if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                        JSONObject interfaceFile = (JSONObject)parser.parse(new FileReader(new File(directory.getPath() + "/" + f)));
                        JSONArray interfaceArray = (JSONArray)interfaceFile.get("interface");
                        String name = (String)interfaceFile.get("name");
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                        Display.displayPackMessage("Loading interface '" + name + "'");
                        Display.changePackTabbing(true);
                        if(name == null) { Display.displayPackError("This does not have a name. Omitting..."); Display.changePackTabbing(false); continue; }
                        if(interfaceArray == null) { Display.displayPackError("This does not have an interface array. Omitting..."); Display.changePackTabbing(false); continue; }
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
            if(!locationDir.exists()) { Display.displayError("Could not find the default locations directory."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = locationDir;
            //Determine if there is a pack to be loaded
            if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
                for(String s : packUsed.list()) {
                    if(s.equals("locations")) {
                        File pack = new File(packUsed.getPath() + "/locations");
                        if(pack.exists() && pack.isDirectory()) {
                            directory = pack;
                            Display.displayPackMessage("loading locations from pack '" + packUsed.getName() + "'");
                            parsingPack = true;
                        }
                    }
                }
            }
            ArrayList<String> namesToBeOmitted = new ArrayList<String>(); //Place where names to be ommitted are stored
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
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                        Display.displayPackMessage("Loading Location '" + name + "'");
                        Display.changePackTabbing(true);
                        if(name == null) { Display.displayPackError("This location does not have a name. Omitting..."); Display.changePackTabbing(false); continue; }
                        if(interfaceJArray == null) { Display.displayPackError("Location '" + name + "' does not have any interfaces. Omitting..."); Display.changePackTabbing(false); continue; }
                        ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();
                        boolean hasChoiceInterface = false;
                        //Determines if the location has a choices array
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
                            Display.displayPackError("This does not have a choices interface. Omitting...");
                            Display.changePackTabbing(false);
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
                                if(obj.get("methods") == null) { Display.displayPackError("This choice has no methods. Omitting..."); Display.changePackTabbing(false); continue; }
                                if(choicename == null) { Display.displayPackError("Ths choice has no name. Omitting..."); Display.changePackTabbing(false); continue; }
                                choices.add(new Choice(choicename, desc, usage, loadMethods(ChoiceMethod.class, (JSONArray)obj.get("methods"), Choice.class), loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), Choice.class)));
                                Display.changePackTabbing(false);
                            }
                        }

                        //Adds the quit choice
                        if(!usedNames.contains("quit")) {
                            Display.displayPackMessage("Adding the quit choice");
                            Display.changePackTabbing(true);

                            Method method;
                            try {
                                method = TextFighter.class.getMethod("exitGame", new Class[] {int.class});
                            } catch (NoSuchMethodException e){ Display.displayPackError("Cannot find method 'exitGame'. Omitting..."); Display.changePackTabbing(false); continue; }
                            ArrayList<Object> arguments = new ArrayList<Object>(); arguments.add(0);
                            ArrayList<Class> argumentTypes = new ArrayList<Class>(); argumentTypes.add(int.class);
                            ArrayList<ChoiceMethod> choiceMethods = new ArrayList<ChoiceMethod>(); choiceMethods.add(new ChoiceMethod(method, arguments, argumentTypes, org.textfighter.TextFighter.class, null, null, null));
                            choices.add(new Choice("quit", "quits the game", "quit", choiceMethods, null));
                            Display.changePackTabbing(false);
                        }
                        //Adds the loaded location
                        locations.add(new Location(name, interfaces, choices, loadMethods(TFMethod.class, (JSONArray)locationFile.get("premethods"), Location.class), loadMethods(TFMethod.class, (JSONArray)locationFile.get("postmethods"), Location.class)));
                        usedNames.add(name);
                        Display.changePackTabbing(false);
                    }
                }
                directory = locationDir;
                parsingPack = false;
            }
            Display.displayProgressMessage("Locations loaded.");
            Display.changePackTabbing(false);
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
            //Determine if there is a pack to be loaded
            if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
                for(String s : packUsed.list()) {
                    if(s.equals("enemies")) {
                        File pack = new File(packUsed.getPath() + "/enemies");
                        if(pack.exists() && pack.isDirectory()) {
                            directory = pack;
                            Display.displayPackMessage("loading enemies from pack '" + packUsed.getName() + "'");
                            parsingPack = true;
                        }
                    }
                }
            }
            //Gets omitted names
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
                        //If a value is not specified in the file, it automatically goes to the default values
                        String name = Enemy.defaultName;                        if(enemyFile.get("name") != null){              name=(String)enemyFile.get("name");}
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                        Display.displayPackMessage("Loading enemy '" + name + "'");
                        Display.changePackTabbing(true);
                        int health = Enemy.defaultHp;                           if(enemyFile.get("health") != null){            health=Integer.parseInt((String)enemyFile.get("health"));}
                        int maxhealth = Enemy.defaultMaxhp;                     if(enemyFile.get("maxhp") != null){             maxhealth=Integer.parseInt((String)enemyFile.get("maxhp"));}
                        int strength = Enemy.defaultStrength;                   if(enemyFile.get("strength") != null){          strength=Integer.parseInt((String)enemyFile.get("strength"));}
                        int levelRequirement =  Enemy.defaultLevelRequirement;  if(enemyFile.get("levelRequirement") != null){  levelRequirement=Integer.parseInt((String)enemyFile.get("levelRequirement"));}
                        boolean finalBoss = false;                              if(enemyFile.get("finalBoss") != null){         finalBoss=Boolean.parseBoolean((String)enemyFile.get("finalBoss"));}
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
                        Display.changePackTabbing(false);
                    }
                }
                directory=enemyDir;
                parsingPack=false;
            }
            sortEnemies();
            Display.displayProgressMessage("Enemies loaded.");
        } catch (IOException | ParseException e) { e.printStackTrace(); Display.changePackTabbing(false); return false; }
        Display.changePackTabbing(false);
        return true;
    }
    public static boolean loadParsingTags() {
        //Custom parsing tags are located in interface packs
        try {
            Display.displayProgressMessage("Loading the parsing tags...");
            if(!tagFile.exists()) { Display.displayError("Could not find the default tags file."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File file = tagFile;
            //Determine if there is a pack to be loaded
            if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
                for(String s : packUsed.list()) {
                    if(s.equals("tags")) {
                        File dir = new File(packUsed.getPath() + "/tags");
                        if(dir.exists() && dir.isDirectory()) {
                            File thefile = new File(dir + "/tags.json");
                            if(thefile.exists() && !thefile.isDirectory()) {
                                file = thefile;
                                Display.displayPackMessage("loading tags from pack '" + packUsed.getName() + "'");
                                parsingPack = true;
                            }
                        }
                    }
                }
            }
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading parsing tags from the default pack."); }
                JSONObject tagsFile = (JSONObject)parser.parse(new FileReader(file));
                JSONArray tagsArray = (JSONArray)tagsFile.get("tags");
                if(tagsArray == null) { continue; }
                for(UiTag t : (ArrayList<UiTag>)loadMethods(UiTag.class, (JSONArray)tagsFile.get("tags"), null)) {
                    Display.interfaceTags.add(t);
                }
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
            if(!achievementDir.exists()) { Display.displayError("Could not find the default achievements directory."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = achievementDir;
            //Determine if there is a pack to be loaded
            if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
                for(String s : packUsed.list()) {
                    if(s.equals("achievements")) {
                        File pack = new File(packUsed.getPath() + "/achievements");
                        if(pack.exists() && pack.isDirectory()) {
                            directory = pack;
                            Display.displayPackMessage("loading achievements from pack '" + packUsed.getName() + "'");
                            parsingPack = true;
                        }
                    }
                }
            }
            //Ones to be omitted
            ArrayList<String> namesToBeOmitted = new ArrayList<String>();
            if(parsingPack) {
                File omissionFile = new File(directory + "/" + "omit.txt");
                if(omissionFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            namesToBeOmitted.add(line);
                        }
                    } catch (IOException e) { Display.displayWarning("IOException when attempting to read the pack's omit file (The file does exist). Continuing normally...");}
                }
            }
            //Loads them
            for(int num=0; num<2; num++) {
                if(!parsingPack) { num++; Display.displayPackMessage("Loading achievements from the default pack."); }
                if(directory.list() != null) {
                    for(String f : directory.list()) {
                        JSONObject achievementFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f)));
                        String name = (String)achievementFile.get("name");
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                        Display.displayPackMessage("Loading achievement '" + name + "'");
                        Display.changePackTabbing(true);
                        achievements.add(new Achievement(name, loadMethods(Requirement.class, (JSONArray)achievementFile.get("requirements"), Achievement.class), loadMethods(Reward.class, (JSONArray)achievementFile.get("rewards"), Achievement.class)));
                        Display.changePackTabbing(false);
                        usedNames.add(name);
                    }
                }
                directory=achievementDir;
                parsingPack=false;
            }
            Display.displayProgressMessage("Achievements loaded.");
            Display.changePackTabbing(false);
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }

    public static boolean loadItems() {
        try {
            Display.displayProgressMessage("Loading the items...");
            if(!enemyDir.exists()) { Display.displayError("Could not find the default items directory."); return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            File directory = itemDir;
            //Determine if there is a pack to be loaded
            if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
                for(String s : packUsed.list()) {
                    if(s.equals("items")) {
                        File pack = new File(packUsed.getPath() + "/items");
                        if(pack.exists() && pack.isDirectory()) {
                            directory = pack;
                            Display.displayPackMessage("loading items from pack '" + packUsed.getName() + "'");
                            parsingPack = true;
                        }
                    }
                }
            }
            //Gets omitted names
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
                if(!parsingPack) { num++; Display.displayPackMessage("Loading items from the default pack."); }
                Display.changePackTabbing(true);
                if(directory.list() != null) {
                    for(String s : directory.list()) {
                        if(s.equals("weapons")) {
                            File weaponsDirectory = new File(directory.getPath() + "/weapons");
                            if(weaponsDirectory.exists() && weaponsDirectory.list() != null) {
                                Display.displayPackMessage("Loading weapons");
                                for(String f : weaponsDirectory.list()) {
                                    Display.changePackTabbing(true);
                                    JSONObject itemFile = (JSONObject) parser.parse(new FileReader(new File(weaponsDirectory.getAbsolutePath() + "/" + f)));
                                    String name = Weapon.defaultName;                           if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                    if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                    Display.displayPackMessage("Loading item '" + name + "' of type 'weapon'");
                                    int damage = Weapon.defaultDamage;                          if(itemFile.get("damage") != null) { damage = Integer.parseInt((String)itemFile.get("damage")); }
                                    int critChance = Weapon.defaultCritChance;                  if(itemFile.get("critchance") != null) { critChance = Integer.parseInt((String)itemFile.get("critchance")); }
                                    int missChance = Weapon.defaultMissChance;                  if(itemFile.get("misschance") != null) { missChance = Integer.parseInt((String)itemFile.get("misschance")); }
                                    String description = Weapon.defaultDescription;             if(itemFile.get("description") != null) { description = (String)itemFile.get("description"); }
                                    weapons.add(new Weapon(name, description, damage, critChance, missChance));
                                    usedNames.add(name);
                                    Display.changePackTabbing(false);
                                }
                            }
                        }
                        if(s.equals("armor")) {
                            File armorDirectory = new File(directory.getPath() + "/armor");
                            if(armorDirectory.exists() && armorDirectory.list() != null) {
                                Display.displayPackMessage("Loading armor");
                                for(String f : armorDirectory.list()) {
                                    Display.changePackTabbing(true);
                                    JSONObject itemFile = (JSONObject) parser.parse(new FileReader(new File(armorDirectory.getAbsolutePath() + "/" + f)));
                                    String name = Armor.defaultName;                            if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                    if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                    Display.displayPackMessage("Loading item '" + name + "' of type 'armord'");
                                    double protectionamount = Armor.defaultProtectionAmount;    if(itemFile.get("protectionamount") != null) { protectionamount = Double.parseDouble((String)itemFile.get("protectionamount")); }
                                    String description = Armor.defaultDescription;              if(itemFile.get("description") != null) { description = (String)itemFile.get("description"); }
                                    armors.add(new Armor(name, description, protectionamount));
                                    usedNames.add(name);
                                    Display.changePackTabbing(false);
                                }
                            }
                        }
                        if(s.equals("tools")) {
                            File toolsDirectory = new File(directory.getPath() + "/tools");
                            if(toolsDirectory.exists() && toolsDirectory.list() != null) {
                                Display.displayPackMessage("Loading tools");
                                for(String f : toolsDirectory.list()) {
                                    Display.changePackTabbing(true);
                                    JSONObject itemFile = (JSONObject) parser.parse(new FileReader(new File(toolsDirectory.getAbsolutePath() + "/" + f)));
                                    String name = Tool.defaultName;                             if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                    if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                    Display.displayPackMessage("Loading item '" + name + "' of type 'tool'");
                                    String description = Tool.defaultDescription;               if(itemFile.get("description") != null) { description = (String)itemFile.get("description"); }
                                    tools.add(new Tool(name, description));
                                    usedNames.add(name);
                                    Display.changePackTabbing(false);
                                }
                            }
                        }
                        if(s.equals("specialitems")) {
                            File specialitemsDirectory = new File(directory.getPath() + "/specialitems");
                            if(specialitemsDirectory.exists() && specialitemsDirectory.list() != null) {
                                Display.displayPackMessage("Loading specialitems");
                                for(String f : specialitemsDirectory.list()) {
                                    Display.changePackTabbing(true);
                                    JSONObject itemFile = (JSONObject) parser.parse(new FileReader(new File(specialitemsDirectory.getAbsolutePath() + "/" + f)));
                                    String name = SpecialItem.defaultName;                      if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                    if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                    Display.displayPackMessage("Loading item '" + name + "' of type 'specialitem'");
                                    String description = SpecialItem.defaultDescription;        if(itemFile.get("description") != null) { name = (String)itemFile.get("description"); }
                                    specialItems.add(new SpecialItem(name, description));
                                    usedNames.add(name);
                                    Display.changePackTabbing(false);
                                }
                            }
                        }
                    }
                }
                directory=enemyDir;
                parsingPack=false;
            }
            Display.changePackTabbing(false);
            Display.displayProgressMessage("Items loaded.");
        } catch (IOException | ParseException e) { e.printStackTrace(); Display.changePackTabbing(false); return false; }
        Display.changePackTabbing(false);
        return true;
    }

    public static void loadConfig() {
        if(configDir.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(packFile));) {
                String line = br.readLine();
                if(line != null && packDir != null && packDir.exists() && packDir.list() != null) {
                    for(String s : packDir.list()){
                        if(s.equals(line)) {
                            packUsed = new File(packDir.getPath() + "/" + line);
                            Display.displayProgressMessage("Current pack: " + line);
                        }
                    }
                }
            } catch(IOException e) { Display.displayWarning("Could not read the config file that defines desired pack!"); }
            Display.loadDesiredColors();
            Display.displayProgressMessage("Config loaded.");
        } else {
            Display.displayWarning("The config directory could not be found!\n Creating new config directory.");
            configDir.mkdirs();
        }
    }

    public static void loadDefaultValues() {
        //Custom parsing tags are located in interface packs
        Display.displayProgressMessage("Loading the default player/enemy/item values...");
        File directory = defaultValuesDirectory;
        //Determine if there is a pack to be loaded
        if(packUsed != null && packUsed.exists() && packUsed.isDirectory() && packUsed.list() != null) {
            for(String s : packUsed.list()) {
                if(s.equals("defaultValues")) {
                    File possibleDirectory = new File(packUsed.getPath() + "/defaultValues");
                    if(possibleDirectory.isDirectory()) {
                        directory = possibleDirectory;
                        Display.displayPackMessage("loading default player/enemy/item values from pack '" + packUsed.getName() + "'");
                        parsingPack = true;
                    }
                }
            }
        } else { return; }
        for(String s : directory.list()) {
            if(s.equals("player.json")) {
                File file = new File(directory.getPath() + "/enemy.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Player values
                    if(valuesFile.get("coins") != null) {                           Player.defaultCoins = Integer.parseInt((String)valuesFile.get("coins")); }
                    if(valuesFile.get("magic") != null) {                           Player.defaultMagic = Integer.parseInt((String)valuesFile.get("magic")); }
                    if(valuesFile.get("metalscraps") != null) {                     Player.defaultMetalscraps = Integer.parseInt((String)valuesFile.get("metalscraps")); }
                    if(valuesFile.get("strength") != null) {                        Player.defaultStrength = Integer.parseInt((String)valuesFile.get("strength")); }
                    if(valuesFile.get("health") != null) {                          Player.defaulthp = Integer.parseInt((String)valuesFile.get("health")); }
                    if(valuesFile.get("maxhp") != null) {                           Player.defaultMaxhp = Integer.parseInt((String)valuesFile.get("maxhp")); }
                    if(valuesFile.get("healthPotions") != null) {                   Player.defaultHealthPotions = Integer.parseInt((String)valuesFile.get("healthPotions")); }
                    if(valuesFile.get("strengthPotions") != null) {                 Player.defaultStrength = Integer.parseInt((String)valuesFile.get("strengthPotions")); }
                    if(valuesFile.get("invincibilityPotions") != null) {            Player.defaultInvincibilityPotions = Integer.parseInt((String)valuesFile.get("invincibilityPotions")); }
                    if(valuesFile.get("hpHealthPotionsGive") != null) {             Player.hpHealthPotionsGive = Integer.parseInt((String)valuesFile.get("hpHealthPotionsGive")); }
                    if(valuesFile.get("turnsStrengthPotionsGive") != null) {        Player.turnsStrengthPotionsGive = Integer.parseInt((String)valuesFile.get("turnsStrengthPotionsGive")); }
                    if(valuesFile.get("turnsInvincibilityPotionsGive") != null) {   Player.turnsInvincibilityPotionsGive = Integer.parseInt((String)valuesFile.get("turnsInvincibilityPotionsGive")); }
                    if(valuesFile.get("turnsWithStrengthLeft") != null) {           Player.defaultTurnsWithStrengthLeft = Integer.parseInt((String)valuesFile.get("turnsWithStrengthLeft")); }
                    if(valuesFile.get("turnsWithInvincibilityLeft") != null) {      Player.defaultTurnsWithInvincibilityLeft = Integer.parseInt((String)valuesFile.get("turnsWithInvincibilityLeft")); }
                } catch (IOException | ParseException e) {continue; }
            } else if(s.equals("enemy.json")) {
                File file = new File(directory.getPath() + "/enemy.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Enemy values
                    if(valuesFile.get("name") != null) {                        Enemy.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("health") != null) {                      Enemy.defaultHp = Integer.parseInt((String)valuesFile.get("health")); }
                    if(valuesFile.get("maxhp") != null) {                       Enemy.defaultMaxhp = Integer.parseInt((String)valuesFile.get("maxhp")); }
                    if(valuesFile.get("strength") != null) {                    Enemy.defaultStrength = Integer.parseInt((String)valuesFile.get("strength")); }
                    if(valuesFile.get("levelRequirement") != null) {            Enemy.defaultLevelRequirement = Integer.parseInt((String)valuesFile.get("levelRequirement")); }
                } catch (IOException | ParseException e) {continue; }
            } else if(s.equals("item.json")) {
                File file = new File(directory.getPath() + "/enemy.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Item values
                    if(valuesFile.get("name") != null) {                        Item.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Item.defaultDescription = (String)valuesFile.get("description"); }
                } catch (IOException | ParseException e) { continue; }
            } else if(s.equals("weapon.json")) {
                File file = new File(directory.getPath() + "/weapon.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Weapon values
                    if(valuesFile.get("name") != null) {                        Weapon.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Weapon.defaultDescription = (String)valuesFile.get("description"); }
                    if(valuesFile.get("damage") != null) {                      Weapon.defaultDamage = Integer.parseInt((String)valuesFile.get("damage")); }
                    if(valuesFile.get("critChance") != null) {                  Weapon.defaultCritChance = Integer.parseInt((String)valuesFile.get("critChance")); }
                    if(valuesFile.get("missChance") != null) {                  Weapon.defaultMissChance = Integer.parseInt((String)valuesFile.get("missChance")); }
                } catch (IOException | ParseException e) { continue; }
            } else if(s.equals("armor.json")) {
                File file = new File(directory.getPath() + "/armor.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Armor values
                    if(valuesFile.get("name") != null) {                        Armor.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Armor.defaultDescription = (String)valuesFile.get("description"); }
                    if(valuesFile.get("protectionAmount") != null) {            Armor.defaultName = (String)valuesFile.get("protectionAmount"); }
                } catch (IOException | ParseException e) { continue; }
            } else if(s.equals("tool.json")) {
                File file = new File(directory.getPath() + "/tool.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Tool values
                    if(valuesFile.get("name") != null) {                        Tool.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Tool.defaultDescription = (String)valuesFile.get("description"); }
                } catch (IOException | ParseException e) { continue; }
            } else if(s.equals("specialitem.json")) {
                File file = new File(directory.getPath() + "/specialitem.json");
                if(!file.exists()) { continue; }
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //SpecialItem values
                    if(valuesFile.get("name") != null) {                        SpecialItem.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 SpecialItem.defaultDescription = (String)valuesFile.get("description"); }
                } catch (IOException | ParseException e) { continue; }
            }
            parsingPack = false;
            directory = defaultValuesDirectory;
        }
        Display.changePackTabbing(false);
        Display.displayPackError("Loaded default player/enemy/item values");
        return;
    }

    public static boolean loadGame(String saveName) {

        if(!areThereAnySaves()){ addToOutput("There are no saves, create one."); return false;}

        File f = new File(savesDir.getPath() + "/" + saveName + ".json");
        if(!f.exists()) { addToOutput("Unable to find a save with that name."); return false; }

        currentSaveFile = f;

        try {
            JSONObject file = (JSONObject)parser.parse(new FileReader(f));
            gameName = (String)file.get("name");

            JSONObject stats = (JSONObject)file.get("stats");
            int level = 0;                                                              if(stats.get("level") != null)                      {level = Integer.parseInt((String)stats.get("level"));}
            int experience = 0;                                                         if(stats.get("experience") != null)                 {experience = Integer.parseInt((String)stats.get("experience"));}
            int score = 0;                                                              if(stats.get("score") != null)                      {score = Integer.parseInt((String)stats.get("score"));}
            int maxhp = player.defaulthp;                                               if(stats.get("maxhealth") != null)                  {maxhp = Integer.parseInt((String)stats.get("maxhealth"));}
            int hp = player.defaulthp;                                                  if(stats.get("health") != null)                     {hp = Integer.parseInt((String)stats.get("health"));}
            int coins = player.defaultCoins;                                            if(stats.get("coins") != null)                      {coins = Integer.parseInt((String)stats.get("coins"));}
            int magic = player.defaultMagic;                                            if(stats.get("magic") != null)                      {magic = Integer.parseInt((String)stats.get("magic"));}
            int metalscraps = player.defaultMetalscraps;                                if(stats.get("metalscraps") != null)                {metalscraps = Integer.parseInt((String)stats.get("metalscraps"));}
            int healthPotions = player.defaultHealthPotions;                            if(stats.get("healthPotions") != null)              {healthPotions = Integer.parseInt((String)stats.get("healthPotions"));}
            int strengthPotions = player.defaultStrengthPotions;                        if(stats.get("strengthPotions") != null)            {strengthPotions = Integer.parseInt((String)stats.get("strengthPotions"));}
            int invincibilityPotions = Player.defaultInvincibilityPotions;              if(stats.get("invincibilityPotions") != null)       {invincibilityPotions = Integer.parseInt((String)stats.get("invincibilityPotions"));}
            int turnsWithStrengthLeft = Player.defaultTurnsWithStrengthLeft;            if(stats.get("turnsWithStrengthLeft") != null)      {turnsWithStrengthLeft = Integer.parseInt((String)stats.get("turnsWithInvincibilityLeft"));}
            int turnsWithInvincibilityLeft = Player.defaultTurnsWithInvincibilityLeft;  if(stats.get("turnsWithInvincibilityLeft") != null) {turnsWithInvincibilityLeft = Integer.parseInt((String)stats.get("turnsWithInvincibilityLeft"));}
            Weapon currentWeapon = null;
            String currentWeaponString = Player.defaultCurrentWeaponString;             if(stats.get("currentWeapon") != null)              {currentWeaponString = (String)stats.get("currentWeapon");}
            System.out.println(weapons.size());
            for(Weapon w : weapons) {
                if(w.getName().equals(currentWeaponString)) {
                    currentWeapon = w;
                }
            }
            if(currentWeapon == null) {
                for(Weapon w : weapons) {
                    if(w.getName().equals(player.defaultCurrentWeaponString)) {
                        currentWeapon = w;
                    }
                }
            }
            boolean gameBeaten = false;                                                 if(stats.get("gameBeaten") != null)                 {gameBeaten = Boolean.parseBoolean((String)stats.get("gameBeaten"));}

            JSONObject inventory = (JSONObject)file.get("inventory");

            ArrayList<Item> newInventory = new ArrayList<Item>();

            //Inventory items
            if(inventory != null) {
                for (Object key : inventory.keySet()) {
                    JSONObject jsonobj = (JSONObject)inventory.get(key);
                    if(jsonobj.get("itemtype") != null) {
                        if(jsonobj.get("itemtype").equals("armor")) {
                            String name = Armor.defaultName;                            if(jsonobj.get("name") != null) { name = (String)jsonobj.get("name"); }
                            String description = Armor.defaultDescription;              if(jsonobj.get("description") != null) { description = (String)jsonobj.get("description"); }
                            double protectionAmount = Armor.defaultProtectionAmount;    if(jsonobj.get("protectionAmount") != null) { protectionAmount = Double.parseDouble((String)jsonobj.get("protectionAmount")); }
                            Armor item = new Armor(name, description, protectionAmount);
                            newInventory.add(item);
                        } else if(jsonobj.get("itemtype").equals("weapon")) {
                            String name = Weapon.defaultName;                           if(jsonobj.get("name") != null) { name = (String)jsonobj.get("name"); }
                            String description = Weapon.defaultDescription;             if(jsonobj.get("description") != null) { description = (String)jsonobj.get("description"); }
                            int damage = Weapon.defaultDamage;                          if(jsonobj.get("damage") != null) { damage = Integer.parseInt((String)jsonobj.get("damage")); }
                            int missChance = Weapon.defaultMissChance;                  if(jsonobj.get("missChance") != null) { missChance = Integer.parseInt((String)jsonobj.get("missChance")); }
                            int critChance = Weapon.defaultCritChance;                  if(jsonobj.get("critChance") != null) { critChance = Integer.parseInt((String)jsonobj.get("critChance")); }
                            Weapon item = new Weapon(name, description, damage, critChance, critChance);
                            newInventory.add(item);
                        }  else if(jsonobj.get("itemtype").equals("tool")) {
                            String name = Tool.defaultName;                             if(jsonobj.get("name") != null) { name = (String)jsonobj.get("name"); }
                            String description = Tool.defaultDescription;               if(jsonobj.get("description") != null) { description = (String)jsonobj.get("description"); }
                            Tool item = new Tool(name, description);
                            newInventory.add(item);
                        }
                    }
                }
            }

            // SpecialItems
            ArrayList<SpecialItem> specialItems = new ArrayList<SpecialItem>();
            JSONObject specialitemsArray = (JSONObject)file.get("specialitems");

            if(specialitemsArray != null) {
                for (Object key : specialitemsArray.keySet()) {
                    JSONObject jsonobj = (JSONObject)inventory.get(key);
                    String name = SpecialItem.defaultName;                          if(jsonobj.get("name") != null) { name = (String)jsonobj.get("name"); }
                    String description = SpecialItem.defaultDescription;            if(jsonobj.get("description") != null) { description = (String)jsonobj.get("description"); }
                    SpecialItem item = new SpecialItem(name, description);
                    specialItems.add(item);
                }
            }

            ArrayList<Achievement> playerAchievements = new ArrayList<Achievement>();
            JSONArray achievementsArray = (JSONArray)file.get("achievements");

            // Adds the previously earned achievements to a new array
            if(achievementsArray != null) {
                for(int i=0; i<achievementsArray.size(); i++) {
                    for(Achievement a : achievements) {
                        if(achievementsArray.get(i) == a.getName()) {
                            playerAchievements.add(a);
                        }
                    }
                }
            }

            player = new Player(hp, maxhp, coins, magic, metalscraps, level, experience, score, healthPotions, strengthPotions, invincibilityPotions, currentWeapon, gameBeaten, newInventory, playerAchievements, specialItems);
            addToOutput("Loaded save '" + saveName + "'");

        } catch (IOException | ParseException e) { addToOutput("Unable to read the save"); e.printStackTrace(); return false; }

        return true;

    }
    public static void newGame(String name) {

        // Tells the player if they would overwrite a game (And doesnt allow them to do so)
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
        gameName = name;

        //Writes all the default stuff to the file

        JSONObject base = new JSONObject();

        JSONObject stats = new JSONObject();
        stats.put("level", Integer.toString(Player.defaultLevel));
        stats.put("experience", Integer.toString(Player.defaultExperience));
        stats.put("score", Integer.toString(Player.defaultScore));
        stats.put("maxhealth", Integer.toString(Player.defaulthp));
        stats.put("health", Integer.toString(Player.defaulthp));
        stats.put("coins", Integer.toString(Player.defaultCoins));
        stats.put("magic", Integer.toString(Player.defaultMagic));
        stats.put("hppotions", Integer.toString(Player.defaultHealthPotions));
        stats.put("strpotions", Integer.toString(Player.defaultStrengthPotions));
        stats.put("invincibilityPotions", Integer.toString(Player.defaultInvincibilityPotions));
        stats.put("turnsWithStrengthLeft", Integer.toString(Player.defaultTurnsWithStrengthLeft));
        stats.put("turnsWithInvincibilityLeft", Integer.toString(Player.defaultTurnsWithInvincibilityLeft));
        stats.put("currentWeapon", Player.defaultCurrentWeaponString);

        JSONObject inventory = new JSONObject();

        base.put("stats", stats);
        base.put("name", name);

        Weapon currentWeapon = null;
        if(currentWeapon == null) {
            for(Weapon w : weapons) {
                if(w.getName().equals(player.defaultCurrentWeaponString)) {
                    currentWeapon = w;
                }
            }
        }

        //Initializes the game with a default player
        player = new Player(currentWeapon);
        player.setLocation("saves");

        addToOutput("Added new save '" + name + "'");

        //Writes it
        try (FileWriter w = new FileWriter(newGameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static boolean saveGame() {

        if(currentSaveFile != null && !currentSaveFile.exists()) { try { currentSaveFile.createNewFile(); } catch (IOException e) { addToOutput("Unable to save game!"); return false; }}

        //Rewrites the whole save file with the new stuff

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
        stats.put("healthPotions", Integer.toString(player.getHealthPotions()));
        stats.put("strengthpotions", Integer.toString(player.getStrengthPotions()));
        stats.put("invincibilityPotions", Integer.toString(player.getInvincibilityPotions()));
        stats.put("turnsWithStrengthLeft", Integer.toString(player.getTurnsWithStrengthLeft()));
        stats.put("turnsWithInvincibilityLeft", Integer.toString(player.getTurnsWithInvincibilityLeft()));
        if(player.getCurrentWeapon() != null) {stats.put("currentWeapon", player.getCurrentWeapon().getName());} else { stats.put("currentWeapon", "fists"); }

        JSONObject inventory = new JSONObject();
        //For items in inventory
        if(player.getInventory() != null) {
            for(Item i : player.getInventory()) {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("itemtype", i.getItemType());
                if(i.getClass().getSuperclass().equals(Armor.class)) {
                    jsonobj.put("protectionAmount", String.valueOf(((Armor)i).getProtectionAmount()));
                }
                if(i.getClass().getSuperclass().equals(Weapon.class)) {
                    jsonobj.put("damage", Integer.toString(((Weapon)i).getDamage()));
                }
                inventory.put(i.getClass().getSimpleName(), jsonobj);
            }
        }

        JSONObject specialitems = new JSONObject();
        //For all special specialitems
        if(player.getSpecialItems() != null) {
            for(SpecialItem si : player.getSpecialItems()) {
                JSONObject jsonobj = new JSONObject();
                jsonobj.put("itemtype", si.getClass().getSimpleName());
                specialitems.put(si.getClass().getSimpleName(), jsonobj);
            }
        }

        JSONArray earnedAchievements = new JSONArray();
        //For all achievements
        if(player.getAchievements() != null) {
            for(Achievement a : player.getAchievements()) {
                earnedAchievements.add(a.getName());
            }
        }

        base.put("achievements", earnedAchievements);
        base.put("inventory", inventory);
        base.put("specialitems", specialitems);
        base.put("stats", stats);
        base.put("name", gameName);

        //Writes it
        try (FileWriter w = new FileWriter(currentSaveFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); return false; }

        return true;

    }

    public static void addSave(String name) { saves.add(name); }
    public static void removeSave(String name) {
        if(name == null) { return; }
        if(name.lastIndexOf(".") != -1) { name = name.substring(0,name.lastIndexOf(".")); }
        getSaveFiles();
        //Makes sure the player isnt attempting to delete the save that is currently being used and the player hasnt beaten the game
        //When the game has been beaten, saves are not deleted when the player dies
        if(currentSaveFile != null && ( ( (name+".json").equals(currentSaveFile.getName()) && player.getAlive()) || player.getGameBeaten())) {return; }
        boolean saveExists = false;
        for(int i=0;i<saves.size();i++){
            if(saves.get(i).equals(name)) {
                saveExists = true;
                File gameFile = new File(savesDir.getAbsolutePath() + "/" + saves.get(i) + ".json");
                if(!gameFile.delete()) {
                    addToOutput("File found, but was unable to delete it");
                    return;
                }
                saves.remove(i);
                addToOutput("Removed save '" + name + "'");
                break;
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
        ArrayList<Enemy> possible = new ArrayList<Enemy>();
        for(Enemy e : enemies) {
            boolean valid = true;
            if(e.getLevelRequirement() <= player.getLevel()) {
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
        if(enemies.size() < 2) { return; } //There is no need to sort if there are no enemies or just one
        ArrayList<Enemy> sorted = new ArrayList<Enemy>();
        ArrayList<Enemy> remaining = enemies;
        while(sorted.size() != enemies.size()) {
            int lowestDiffIndex = 0;
            int lowestKnownDifficulty = 0;
            for(int i=0; i<remaining.size(); i++) {
                if(i == 0) {
                    lowestKnownDifficulty = remaining.get(i).getDifficulty();
                } else if(remaining.get(i).getDifficulty() < lowestKnownDifficulty) {
                    lowestDiffIndex = i;
                }
            }
            sorted.add(remaining.get(lowestDiffIndex));
            remaining.remove(lowestDiffIndex);
        }
        enemies = sorted;
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
        if(player.getLocation() != null && location.equals(player.getLocation())) { return true; }
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

    public static boolean invokePlayerInput() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            Display.promptUser();
            String input = in.readLine();
            Display.resetColors();
            Display.previousCommandString = input;
            if(input.trim() != null) {
                boolean validChoice = false;
                for(Choice c : player.getLocation().getPossibleChoices()){
                    //Makes sure the input has any arguments
                    if(input.indexOf(" ") != -1) {
                        if (c.getName().equals(input.substring(0,input.indexOf(" ")))) {
                            validChoice = true;
                            break;
                        }
                    //If no arguments
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
                                return(c.invokeMethods(inputArrayList));
                            } else {
                                return(c.invokeMethods(new ArrayList<String>()));
                            }
                        }
                    }
                } else {
                    if(input.indexOf(" ") != -1) {
                        addToOutput("Invalid choice - '" + input.substring(0,input.indexOf(" ")) + "'");
                    } else {
                        addToOutput("Invalid choice - '" + input + "'");
                    }
                    return false;
                }
            } else {addToOutput("Pick one of the choices shown.");}
        } catch (IOException e) {
            Display.displayError("An error occured while reading input!");
            e.printStackTrace();
            System.exit(1);
        }
        return false;
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
            movePlayer("fight");
            player.setInFight(true);
            currentEnemy.invokePremethods();
            //Does a lot of basic things from playGame(), but if they werent here, then things would look quite strange (screen not being cleared, no output from previous command, etc.)
            Display.clearScreen();
            Display.displayPreviousCommand();
            if(output != null) {
                Display.displayOutputMessage(output);
            }
            if(needsSaving && currentSaveFile != null && (player.getAlive() || player.getGameBeaten())) { saveGame(); }
            needsSaving = false;
            output = "";
            while(player.getInFight()) {
                Display.displayInterfaces(player.getLocation());
                //Invokes enemyInput and continues if invalid
                if(!invokePlayerInput()) { continue; }
                player.decreaseTurnsWithStrengthLeft(1);
                player.decreaseTurnsWithInvincibilityLeft(1);
                // Does enemy actions
                Random random = new Random();
                if(currentEnemy.getPossibleActions() != null && currentEnemy.getPossibleActions().size() > 0) {
                    int number = (Integer)(random.nextInt(currentEnemy.getPossibleActions().size()*2))+1;
                    if(number > currentEnemy.getPossibleActions().size()) {
                        currentEnemy.attack(null);
                    } else {
                        currentEnemy.getPossibleActions().get(random.nextInt(currentEnemy.getPossibleActions().size())).invokeMethods();
                    }
                    player.setCanBeHurtThisTurn(true);
                } else {
                    currentEnemy.attack(null);
                }
                Display.clearScreen();
                Display.displayPreviousCommand();
                if(output != null) {
                    Display.displayOutputMessage(output);
                }
                output="";
                if(needsSaving && currentSaveFile != null) { saveGame(); }
                needsSaving = false;
                //does a random enemy action
                if(player.getHp() < 1) {
                    player.setAlive(false);
                    player.setInFight(false);
                } else if(currentEnemy.getHp() < 1) {
                    player.setInFight(false);
                    addToOutput("You defeated the '" + currentEnemy.getName() + "'!");
                    currentEnemy.invokePostmethods();
                    addToOutput("Rewards:");
                    currentEnemy.invokeRewardMethods();
                    if(currentEnemy.getIsFinalBoss()) { playerWins(); }
                    currentEnemy=null;
                    movePlayer("inGameMenu");
                    return true;
                }
            }
        } else {
            addToOutput("Invalid enemy: '" + en + "'");
            movePlayer("enemyChoices");
            return false;
        }
        return false;
    }

    public static int calculateFromTwoIntegers(int value1, String calculation, int value2) {
        if(calculation != null) {
            switch(calculation) {
                case "+":
                    return(value1 + value2);
                case "-":
                    return(value1 - value2);
                case "*":
                    return(value1 * value2);
                case "/":
                    return(Math.round(value1 / value2));
                default:
                    Display.displayError("The pack gave an invalid comparison operator (" + calculation + ")");
            }
        }
        return 0;
    }

    public static double calculateFromTwoDoubles(double value1, String comparison, double value2) {
        if(comparison != null) {
            switch(comparison) {
                case "+":
                    return(value1 + value2);
                case "-":
                    return(value1 - value2);
                case "*":
                    return(value1 * value2);
                case "/":
                    return(value1 / value2);
                default:
                    Display.displayError("The pack gave an invalid comparison operator (" + comparison + ")");
            }
        }
        return 0;
    }

    public static boolean variableComparison(String value1, String value2) { return(value1.equals(value2)); } //No comparison operator necessary because you can only do '=' on strings

    public static boolean variableComparison(double value1, String comparison, double value2) {

        //Make the comparison
        if(comparison != null) {
            switch(comparison) {
                case "=":
                    return((double)value1 == (double)value2);
                case ">":
                    return((double)value1 > (double)value2);
                case "<":
                    return((double)value1 < (double)value2);
                case ">=":
                    return((double)value1 >= (double)value2);
                case "<=":
                    return((double)value1 <= (double)value2);
                default:
                    Display.displayError("The pack gave an invalid comparison operator (" + comparison + ")");
            }
        } else { Display.displayError("The pack did not give a comparison operator"); return false; }
        return false;
    }

    public static boolean variableComparison(int value1, String comparison, int value2) {

        //Make the comparison
        if(comparison != null) {
            switch(comparison) {
                case "=":
                    return((int)value1 == (int)value2);
                case ">":
                    return((int)value1 > (int)value2);
                case "<":
                    return((int)value1 < (int)value2);
                case ">=":
                    return((int)value1 >= (int)value2);
                case "<=":
                    return((int)value1 <= (int)value2);
                default:
                    Display.displayError("The pack gave an invalid comparison operator (" + comparison + ")");
            }
        } else { Display.displayError("The pack did not give a comparison operator"); return false; }
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
        //Determine if any achievements should be recieved
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
        if(needsSaving && currentSaveFile != null && (player.getAlive() || player.getGameBeaten())) { saveGame(); }
        needsSaving = false;
    }

    public static void main(String[] args) {
        //Determine if the game is run in pack test mode
        for(String a : args) {
            if(a.equals("test")) {
                testMode = true;
            }
        }
        if(!testMode) { Display.clearScreen(); }
        if (!loadResources()) {
            Display.displayError("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(1);
        }
        if(!testMode) {
            getSaveFiles();
            player.setLocation("start");
            while(player.getAlive() || player.getGameBeaten()) {
                playGame();
            }
        }
    }

    public static void doNothing() { return; }
}
