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
import org.textfighter.*;

import org.json.simple.*;
import org.json.simple.parser.*;

/* HOW IT WORKS

    Mods are loaded.
Methods are extracted from the mods.
Methods are specified in the method JSONObjects and their
classes are specified with them.
The methods are extracted from the classes specified.
The pack also specifies arguments and classes of the arguments.

    Methods are invoked to do different things:
Determine if another method should be invoked (Among other things),
Just do regular things like set a value,
show output to the user,
make an enemy perform an action,
And a lot more.

    ChoiceMethods have arguments that are inputted by the user.
These areguments are specified by "%ph%", and are replaced
jsutu before they are invoked. If an argument is a method,
then placeholder arguments are replaced by input.
    If there are not enough arguments, then it tells the
user so by outputing the usage of the choice the user
chose.

*/

@SuppressWarnings("unchecked")

/**
 * @author      Sean Johnson <smjohns1@gmail.com>
 * @version     0.2.5
 */

public class TextFighter {

    // Pack testing variables
    /** True if testing packs.*/
    public static boolean testMode = false;
    /**True if loading resources from a pack.*/
    public static boolean parsingPack = false;

    //Version
    /**The file that the version of the game is stored in.*/
    public static File versionFile = new File("../../../VERSION.txt");
    /**Stores the current version.*/
    public static String version;

    // Important game variables
    /**Stores the name of the save currently being used.*/
    public static String gameName;

    /**Stores the player instance.*/
    public static Player player;
    /**Stores the enemy instance that the player is fighting (Assuming the player is in a fight).*/
    public static Enemy currentEnemy = new Enemy();

    /**Stores the file that the save is stored in*/
    public static File currentSaveFile;

    /**Stores the String that is outputted after every turn*/
    public static String output = "";

    /**
     * Stores the directory of the used pack.
     * If there is no pack, this will remain null.
     */
    public static File packUsed;

    /**Stores the directory where all of the default resources are located*/
    public static File resourcesDir;
    /**Stores the file where the default tags are located*/
    public static File tagFile;
    /**Stores the directory where the default pack default values are located*/
    public static File defaultValuesDirectory;
    /**Stores the directory where the default interfaces are located*/
    public static File interfaceDir;
    /**Stores the directory where the default locations are located*/
    public static File locationDir;
    /**Stores the directory where the default enemies are located*/
    public static File enemyDir;
    /**Stores the directory where the saves are located*/
    public static File savesDir;
    /**Stores the directory where the default achievements are located*/
    public static File achievementDir;
    /**Stores the directory where the items are located*/
    public static File itemDir;
    /**Stores the directory where the default custom variables are located*/
    public static File customVariablesDir;

    /**Stores the directory where all config files are located*/
    public static File configDir;

    /**Stores the file where the pack used is defined*/
    public static File packFile;
    /**Stores the directory where all packs are to be put*/
    public static File packDir;

    /**The parser for all resources and saves*/
    public static JSONParser parser = new JSONParser();

    //All things in the game
    /**All locations are stored here*/
    public static ArrayList<Location> locations = new ArrayList<Location>();
    /**All save names are stored here*/
    public static ArrayList<String> saves = new ArrayList<String>();
    /**All enemies are stored here*/
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    /**All possible enemies are stored here*/
    public static ArrayList<Enemy> possibleEnemies = new ArrayList<Enemy>();
    /**All achievements are stored here*/
    public static ArrayList<Achievement> achievements = new ArrayList<Achievement>();
    /**All armors are stored here*/
    public static ArrayList<Armor> armors = new ArrayList<Armor>();
    /**All weapons are stored here*/
    public static ArrayList<Weapon> weapons = new ArrayList<Weapon>();
    /**All specialitems are stored here*/
    public static ArrayList<SpecialItem> specialItems = new ArrayList<SpecialItem>();
    /**All tools are stored here*/
    public static ArrayList<Tool> tools = new ArrayList<Tool>();

    //All custom variable arrays
    /**All player custom variables are copied from here to the new instance*/
    public static ArrayList<CustomVariable> playerCustomVariables = new ArrayList<CustomVariable>();
    /**All enemy custom variables are copied from here to the new instance*/
    public static ArrayList<CustomVariable> enemyCustomVariables = new ArrayList<CustomVariable>();
    /**All tool custom variables are copied from here to the new instance*/
    public static ArrayList<CustomVariable> toolCustomVariables = new ArrayList<CustomVariable>();
    /**All armor custom variables are copied from here to the new instance*/
    public static ArrayList<CustomVariable> armorCustomVariables = new ArrayList<CustomVariable>();
    /**All weapon custom variables are copied from here to the new instance*/
    public static ArrayList<CustomVariable> weaponCustomVariables = new ArrayList<CustomVariable>();
    /**All special item custom variables are copied from here to the new instance*/
    public static ArrayList<CustomVariable> specialitemCustomVariables = new ArrayList<CustomVariable>();

    /**Stores whether or not the game needs to be saved. The game needs to be saved if a value that is saved is changed*/
    public static boolean needsSaving = false;

    /**
     * Gets a weapon from the {@link #weapons} arraylist.
     * @param name  the name of the weapon that is to be found.
     * @return      If a weapon is found, then return it. Else, return null.
     */
    public static Weapon getWeaponByName(String name) { for(Weapon w : weapons) { if(w.getName().equals(name)) { return w; } } addToOutput("No such weapon '" + name + "'"); return null; }
    /**
     * Gets an armor from the {@link #armors} arraylist.
     * @param name  the name of the armor that is to be found.
     * @return      If a armor is found, then return it. Else, return null.
     */
    public static Armor getArmorByName(String name) { for(Armor a : armors) { if(a.getName().equals(name)) { return a; } } addToOutput("No such armor '" + name + "'"); return null; }
    /**
     * Gets a tool from the {@link #tools} arraylist.
     * @param name  the name of the tool that is to be found.
     * @return      If a tool is found, then return it. Else, return null.
     */
    public static Tool getToolByName(String name) { for(Tool t : tools) { if(t.getName().equals(name)) { return t; } } addToOutput("No such tool '" + name + "'"); return null; }
    /**
     * Gets a special item from the {@link #specialItems} arraylist.
     * @param name  the name of the special item that is to be found.
     * @return      If a special item is found, then return it. Else, return null.
     */
    public static SpecialItem getSpecialItemByName(String name) { for(SpecialItem sp : specialItems) { if(sp.getName().equals(name)) { addToOutput("No such specialitem '" + name + "'"); return sp; } } return null; }
    /**
     * Gets a enemy from the {@link #enemies} arraylist
     * @param name  the name of the enemy that is to be found
     * @return      If a enemy is found, then return it. Else, return null.
     */
    public static Enemy getEnemyByName(String name) { for(Enemy e : enemies) { if(e.getName().equals(name)) { return e; } } addToOutput("No such enemy '" + name + "'"); return null; }

    /**
     * Add a string to the {@link #output}.
     * <p>If the string given is null, then do not do anything.</p>
     * @param msg   The string that is to be added to the {@link #output}.
     */
    public static void addToOutput(String msg) { if(msg != null) { output+=msg + "\n";}  }

    //Things to get the current version
    /**
     * Get the version from the {@link #versionFile}.
     * @return      returns the version that is read. If no version was read, then returns "unknown".
     */
    public static String readVersionFromFile() {
        if(versionFile == null) { Display.displayWarning("Could not read the version from file"); return "Unknown"; }
        try (BufferedReader br = new BufferedReader(new FileReader(versionFile))) {
            String line = br.readLine();
            if(line != null) { return line; } else { return "Unknown"; }
        } catch(IOException e) { Display.displayWarning("Could not read the version from file");}
        return "Unknown";
    }

    /**
     * Get the version of the game from {@link #version}.
     * @return      The {@link #version}.
     */
    public static String getVersion() { return version; }

    /**
     * Defines all resource directories then loads all the resources needed for the game.
     * @return      True if successful. False is unsuccessful.
     */
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
        customVariablesDir = new File(resourcesDir.getPath() + "/customVariables");
        version = readVersionFromFile();
        //Load some things
        loadConfig();
        loadDefaultValues();
        loadCustomVariables();
        //loads the content
        //If any of the necessary content fails to load, send an error and exit the game
        if (!loadItems() || !loadParsingTags() || !loadInterfaces() || !loadLocations() || !loadEnemies() || !loadAchievements()) { return false; }
        if (!savesDir.exists()) {
            Display.displayWarning("The saves directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { Display.displayError("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    /**
     * Loads a singular method.
     * <p> Useful for loading a Method that is an argument that you wouldnt/cant pass a JSONArray to </p>
     * @param type          The type of the method.
     * @param obj           The JSONObject that the method is located in.
     * @param parentType    The class of the parent of the method.
     * @return              The new method that was loaded.
    */
    public static Object loadMethod(Class type, JSONObject obj, Class parentType) {
        ArrayList<Object> argumentsRaw = (JSONArray)obj.get("arguments");
        ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
        ArrayList<Class> argumentTypes = new ArrayList<Class>();
        ArrayList<Class> parameterArgumentTypes = new ArrayList<Class>(); //Used for getting methods that need Object as a parameter type
        if(type.equals(UiTag.class) && obj.get("tag") != null) { Display.displayPackMessage("Loading tag '" + (String)obj.get("tag") + "'"); }
        String methodString = (String)obj.get("method");
        Display.displayPackMessage("Loading method '" + methodString + "' of type '" + type.getSimpleName() + "'");
        Display.changePackTabbing(true);
        String rawClass = (String)obj.get("class");
        Object rawField = obj.get("field");
        if(rawField != null) {
            if(rawField.getClass().equals(JSONObject.class)) {
                rawField = loadMethod(FieldMethod.class, (JSONObject)rawField, type);
            }
        }
        String rawFieldClass = (String)obj.get("fieldclass");
        if(methodString == null || rawClass == null) { Display.displayPackError("This method has no class or method. Omitting..."); Display.changePackTabbing(false); return null; }
        if(!((argumentsRaw != null && argumentTypesString != null) && (argumentsRaw.size() == argumentTypesString.size()) || (argumentTypesString == null && argumentsRaw == null))) { Display.displayPackError("This method's does not have the same amount of arguments as argumentTypes. Omitting..."); Display.changePackTabbing(false); return null; }
        //Fields and fieldclasses can be null (Which just means the method does not act upon a field
        if(argumentTypesString != null && argumentTypesString.size() > 0) {
            //Translates the raw argument types to classes
            //0 is String, 1 is int, 2 is double, 3 is boolean, 4 is class
            for (int g=0; g<argumentTypesString.size(); g++) {
                String argType = argumentTypesString.get(g);
                if(argType == null) { Display.displayPackError("This methods has a null value for an argument. Omitting..."); }
                // Check to see if the method recieving these arguments wants an Object
                if(argType.length() > 1 && argType.substring(0,1) == "!") {
                    System.out.println("Object");
                    parameterArgumentTypes.set(g, Object.class);
                    argumentTypesString.set(g, argType.substring(1, argType.length()));
                }
                int num = Integer.parseInt(argumentTypesString.get(g));
                if(num == 0) {
                    argumentTypes.add(String.class);
                    if(parameterArgumentTypes.size() >= g) { parameterArgumentTypes.add(String.class); }
                } else if(num == 1) {
                    argumentTypes.add(int.class);
                    if(parameterArgumentTypes.size() >= g) { parameterArgumentTypes.add(int.class); }
                } else if(num == 2) {
                    argumentTypes.add(double.class);
                    if(parameterArgumentTypes.size() >= g) { parameterArgumentTypes.add(double.class); }
                } else if(num == 3) {
                    argumentTypes.add(boolean.class);
                    if(parameterArgumentTypes.size() >= g) { parameterArgumentTypes.add(boolean.class); }
                } else if(num == 4) {
                    argumentTypes.add(Class.class);
                    if(parameterArgumentTypes.size() >= g) { parameterArgumentTypes.add(Class.class); }
                } else {
                    Display.displayPackError("This method has arguments that are not String, int, double, boolean, or class. Omitting...");
                    Display.changePackTabbing(false);
                    continue;
                }
            }
        }

        //Gets the class
        Class clazz = null;
        try { clazz = Class.forName(rawClass); } catch (ClassNotFoundException e){ Display.displayPackError("This method has an invalid class. Omitting..."); Display.changePackTabbing(false); return null; }

        Class fieldclass = null;
        Object field = null;

        if(rawField != null && !rawField.getClass().equals(FieldMethod.class)) {
            //Gets the fieldclass
            if(rawFieldClass != null && rawField != null) { try { fieldclass = Class.forName((String)rawFieldClass); } catch (ClassNotFoundException e){ Display.displayPackError("This method has an invalid fieldclass. Omitting..."); Display.changePackTabbing(false); return null; } }

            //Gets the field from the class
            try {
                if(rawField != null && rawFieldClass != null) {
                    field = fieldclass.getField((String)rawField);
                }
            } catch (NoSuchFieldException | SecurityException e) { Display.displayPackError("This method has an invalid field. Omitting..."); Display.changePackTabbing(false); return null; }
        } else if (rawField != null){
            field = rawField;
        }

        //Gets the method from the class
        Method method = null;
        try {
            if(argumentTypes != null ) {
                method = clazz.getMethod(methodString, parameterArgumentTypes.toArray(new Class[parameterArgumentTypes.size()]));
            } else {
                method = clazz.getMethod(methodString);
            }
        } catch (NoSuchMethodException e){ Display.displayPackError("Method '" + methodString + "' of class '" + rawClass + "' with given arguments could not be found. Omitting..."); Display.changePackTabbing(false); return null; }

        //Makes the arguments the correct type (String, int, or boolean)
        ArrayList<Object> arguments = new ArrayList<Object>();
        if(argumentsRaw != null) {
            //Cast all of the arguments to the correct type
            for (int p=0; p<argumentTypes.size(); p++) {
                if(argumentsRaw.get(p).getClass().equals(JSONObject.class)) {
					arguments.add(loadMethod(TFMethod.class, (JSONObject)argumentsRaw.get(p), type));
                } else {
                    //The syntax for using a null value is "%null%"
                    if(((String)argumentsRaw.get(p)).equals("%null%")) { arguments.add(null); }
                    else if(argumentTypes.get(p).equals(int.class)) {
                        arguments.add(Integer.parseInt((String)argumentsRaw.get(p)));
                    } else if (argumentTypes.get(p).equals(String.class)) {
                        arguments.add((String)argumentsRaw.get(p));
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
        //Uses null here because if the type is not any of the following, then return null.

        //Creates the correct Method type
        if(type.equals(ChoiceMethod.class)) {
            o=new ChoiceMethod(method, arguments, argumentTypes, field, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), ChoiceMethod.class));
        } else if(type.equals(Requirement.class)) {
            boolean neededBoolean = Requirement.defaultNeededBoolean; if(obj.get("neededBoolean") != null) {neededBoolean=Boolean.parseBoolean((String)obj.get("neededBoolean"));}
            o=new Requirement(method, arguments, field, neededBoolean);
        } else if(type.equals(EnemyActionMethod.class)) {
            o=new EnemyActionMethod(method, arguments, field);
        } else if(type.equals(TFMethod.class)) {
            o=new TFMethod(method, arguments, argumentTypes, field, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), TFMethod.class));
        } else if(type.equals(Reward.class)) {
            if(parentType.equals(Enemy.class)) {
                int chance = Reward.defaultChance; if((String)obj.get("chance") != null){chance=Integer.parseInt((String)obj.get("chance"));}
                if(chance == 0) { Display.displayPackError("This reward have no chance. Omitting..."); return null; }
                o=new Reward(method, arguments, field, loadMethods(Reward.class, (JSONArray)obj.get("requirements"), Enemy.class), chance, (String)obj.get("rewarditem"));
            } else {
                //Achievement rewards always run (So no chance needed)
                o=new Reward(method, arguments, field, loadMethods(Reward.class, (JSONArray)obj.get("requirements"), Enemy.class), 100, (String)obj.get("rewarditem"));
            }
        } else if(type.equals(UiTag.class)) {
            String tag = (String)obj.get("tag");
            if(tag == null) { Display.displayPackError("This tag has no tagname. Omitting..."); Display.changePackTabbing(false); return null; }
            o=new UiTag(tag, method, arguments, field, loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), UiTag.class));
        } else if(type.equals(FieldMethod.class)) {
            o=new FieldMethod(method, arguments, argumentTypes, field);
        }
        Display.changePackTabbing(false);
        return o;
    }

    /**
     * Loads a JSONArray of methods
     * <p>Loads the methods in things like requirements, choicemethods, and others
     * Used if you have a JSONArray full of methods (And only methods) to parse</p>
     * @param type          The type of methods that are being loaded (ChoiceMethod, Requirement, etc.).
     * @param methodArray   The JSONArray that the methods are located in.
     * @param parentType    The class of the parent of the methods.
     * @return              An ArrayList of the type of methods loaded.
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

    /**
     * Loads the interfaces.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     * @return      True if successful, False if unsuccessful
     */
    public static boolean loadInterfaces() {
        try {
            Display.displayProgressMessage("Loading the interfaces...");
            Display.changePackTabbing(true);
            if(!interfaceDir.exists()) { Display.displayError("Could not find the default interfaces directory."); Display.changePackTabbing(false); return false;}
            ArrayList<String> usedNames = new ArrayList<String>(); // Used to override default interfaces with ones in packs
            File directory = interfaceDir;
            //Determine if there is a pack to be loaded and start loading from it if there is
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

            //Finds all names from omit.txt that are to be omitted from the default pack (Also omits from the modpack)
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
                        //Load the values from the file. If any value is null (That is required), then omit the interface
                        if(f.equals("tags.json")) { continue; }
                        if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                        JSONObject interfaceFile = null;
                        try { interfaceFile = (JSONObject)parser.parse(new FileReader(new File(directory.getPath() + "/" + f))); } catch(ParseException e) { Display.displayError("Having trouble parsing interface file '" + f + "'"); continue; }
                        if(interfaceFile == null) { continue; }
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
                //Go parse the default pack (If it already was, then it wont matter if this runs or not)
                directory = interfaceDir;
                parsingPack = false;
            }
            Display.displayProgressMessage("Interfaces loaded.");
            Display.changePackTabbing(false);
            return true;
        } catch (FileNotFoundException e) { Display.displayError("Could not find an interface file. It was likely deleted after the program got all files in the interfaces directory."); Display.changePackTabbing(false); return false; }
        catch (IOException e) { Display.displayError("IOException when attempting to load the interfaces. The permissions are likely set to be unreadable."); Display.changePackTabbing(false); return false;}
    }

    /**
     * Loads the locations.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     * @return      True if successful, false if unsuccessful.
     */
    public static boolean loadLocations() {
        Display.displayProgressMessage("Loading the locations...");
        Display.changePackTabbing(true);
        if(!locationDir.exists()) { Display.displayError("Could not find the default locations directory."); Display.changePackTabbing(false); return false;}
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
        //Determine which locations should be omitted from the default pack (And the mod pack)
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
        //Load them
        for(int num=0; num<2; num++) {
            if(!parsingPack) { num++; Display.displayPackMessage("Loading locations from the default pack."); }
            if(directory.list() != null) {
                for (String f : directory.list()) {
                    //Load the values, if any are null (other than values that are not required), then omit the location
                    if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                    JSONObject locationFile = null;
                    try { locationFile = (JSONObject)parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f))); } catch (IOException | ParseException e) { Display.displayPackError("Having trouble parsing from file '" + f + "'"); e.printStackTrace(); Display.changePackTabbing(false);}
                    if(locationFile == null) { continue; }
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
                    //If there is no choice interface, then the location should be omitted.
                    //Otherwise, the player would not know what choices they have
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
                            Choice c = new Choice(choicename, desc, usage, loadMethods(ChoiceMethod.class, (JSONArray)obj.get("methods"), Choice.class), loadMethods(Requirement.class, (JSONArray)obj.get("requirements"), Choice.class));
                            if(c.getMethods() != null && c.getMethods().size() > 0) { choices.add(c) ; }
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
                        ArrayList<ChoiceMethod> choiceMethods = new ArrayList<ChoiceMethod>(); choiceMethods.add(new ChoiceMethod(method, arguments, argumentTypes, null, null));
                        choices.add(new Choice("quit", "quits the game", "quit", choiceMethods, null));
                        Display.changePackTabbing(false);
                    }
                    //Adds the loaded location to the location ArrayList
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
        return true;
    }

    /**
     * Loads the enemies.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     * @return      True if successful, false if unsuccessful.
     */
    public static boolean loadEnemies() {
        Display.displayProgressMessage("Loading the enemies...");
        Display.changePackTabbing(true);
        if(!enemyDir.exists()) { Display.displayError("Could not find the default enemies directory."); Display.changePackTabbing(false); return false;}
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
                    JSONObject enemyFile = null;
                    try { enemyFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f))); } catch (IOException | ParseException e) { Display.displayPackError("Having trouble parsing from file '" + f + "'"); e.printStackTrace(); Display.changePackTabbing(false);}
                    if(enemyFile == null) { continue; }
                    //If a value is not specified in the file, it automatically goes to the default values
                    String name = Enemy.defaultName;                        if(enemyFile.get("name") != null){              name=(String)enemyFile.get("name");}
                    if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                    Display.displayPackMessage("Loading enemy '" + name + "'");
                    Display.changePackTabbing(true);
                    String description = Enemy.defaultDescription;          if(enemyFile.get("description") != null){       description=(String)enemyFile.get("description");}
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
                            EnemyAction a = new EnemyAction(loadMethods(EnemyActionMethod.class, (JSONArray)obj.get("methods"), EnemyAction.class), loadMethods(EnemyActionMethod.class, (JSONArray)obj.get("requirements"), Enemy.class));
                            if(a.getMethods() != null && a.getMethods().size() > 1) { enemyActions.add(a); }
                        }
                    }
                    //Add the enemy to the enemies arraylist
                    enemies.add(new Enemy(name, description, health, strength, levelRequirement, finalBoss,
                                loadMethods(Requirement.class, (JSONArray)enemyFile.get("requirements"), Enemy.class),
                                loadMethods(TFMethod.class, (JSONArray)enemyFile.get("preMethods"), Enemy.class),
                                loadMethods(TFMethod.class, (JSONArray)enemyFile.get("postMethods"), Enemy.class),
                                loadMethods(Reward.class, (JSONArray)enemyFile.get("rewardMethods"), Enemy.class),
                                enemyActions, enemyCustomVariables));
                    usedNames.add(name);
                    Display.changePackTabbing(false);
                }
            }
            directory=enemyDir;
            parsingPack=false;
        }
        sortEnemies();
        Display.displayProgressMessage("Enemies loaded.");
        Display.changePackTabbing(false);
        return true;
    }

    /**
     * Loads the parsing (interface) tags.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     * @return      True if successful, false if unsuccessful.
     */
    public static boolean loadParsingTags() {
        //Custom parsing tags are located in interface packs
        Display.displayProgressMessage("Loading the parsing tags...");
        Display.changePackTabbing(true);
        if(!tagFile.exists()) { Display.displayError("Could not find the default tags file."); Display.changePackTabbing(false); return false;}
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
        //Load them
        for(int num=0; num<2; num++) {
            if(!parsingPack) { num++; Display.displayPackMessage("Loading parsing tags from the default pack."); }
            JSONObject tagsFile = null;
            try { tagsFile = (JSONObject)parser.parse(new FileReader(file));  } catch (IOException | ParseException e) { Display.displayPackError("Having trouble parsing from tags file"); e.printStackTrace(); continue; }
            if(tagsFile == null) { continue; }
            JSONArray tagsArray = (JSONArray)tagsFile.get("tags");
            if(tagsArray == null) { continue; }
            //Load each of the tags
            for(UiTag t : (ArrayList<UiTag>)loadMethods(UiTag.class, (JSONArray)tagsFile.get("tags"), null)) {
                Display.interfaceTags.add(t);
            }
            parsingPack = false;
            file = tagFile;
            Display.displayProgressMessage("Parsing tags loaded.");
        }
        Display.changePackTabbing(false);
        return true;
    }

    /**
     * Loads theachievements.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     * @return      True if successful, false if unsuccessful.
     */
    public static boolean loadAchievements() {
        Display.displayProgressMessage("Loading the achievements...");
        Display.changePackTabbing(true);
        if(!achievementDir.exists()) { Display.displayError("Could not find the default achievements directory."); Display.changePackTabbing(false); return false;}
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
                    JSONObject achievementFile = null;
                    try { achievementFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f))); } catch (IOException | ParseException e) { Display.displayPackError("Having trouble parsing from file '" + f + "'"); e.printStackTrace(); continue; }
                    if(achievementFile == null) { continue; }
                    String name = (String)achievementFile.get("name");
                    if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.displayPackError("An achievement does not have a name"); Display.changePackTabbing(false); continue; }
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
        return true;
    }

    /**
     * Loads the items.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     * @return      True if successful, false if unsuccessful.
     */
    public static boolean loadItems() {
        Display.displayProgressMessage("Loading the items...");
        Display.changePackTabbing(true);
        if(!enemyDir.exists()) { Display.displayError("Could not find the default items directory."); Display.changePackTabbing(false); return false;}
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
                    //Get each directory and load the type of item that is stored inside
                    if(s.equals("weapons")) {
                        File weaponsDirectory = new File(directory.getPath() + "/weapons");
                        if(weaponsDirectory.exists() && weaponsDirectory.list() != null) {
                            Display.displayPackMessage("Loading weapons");
                            for(String f : weaponsDirectory.list()) {
                                Display.changePackTabbing(true);
                                JSONObject itemFile = null;
                                try{ itemFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/weapons/" + f))); } catch(ParseException | IOException e) { Display.displayError("Having problems with parsing weapon in file '" + f + "'"); continue; }
                                if(itemFile == null) { continue; }
                                String name = Weapon.defaultName;                           if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                Display.displayPackMessage("Loading item '" + name + "' of type 'weapon'");
                                int damage = Weapon.defaultDamage;                          if(itemFile.get("damage") != null) { damage = Integer.parseInt((String)itemFile.get("damage")); }
                                int critChance = Weapon.defaultCritChance;                  if(itemFile.get("critchance") != null) { critChance = Integer.parseInt((String)itemFile.get("critchance")); }
                                int missChance = Weapon.defaultMissChance;                  if(itemFile.get("misschance") != null) { missChance = Integer.parseInt((String)itemFile.get("misschance")); }
                                String description = Weapon.defaultDescription;             if(itemFile.get("description") != null) { description = (String)itemFile.get("description"); }
                                int durability = Weapon.defaultDurability;                  if(itemFile.get("durability") != null) { durability = Integer.parseInt((String)itemFile.get("durability")); }
                                //Add it
                                weapons.add(new Weapon(name, description, damage, critChance, missChance, weaponCustomVariables, durability));
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
                                JSONObject itemFile = null;
                                try{ itemFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/armor/" + f))); } catch(ParseException | IOException e) { Display.displayError("Having problems with parsing armor in file '" + f + "'"); continue;}
                                if(itemFile == null) { continue; }
                                String name = Armor.defaultName;                            if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                Display.displayPackMessage("Loading item '" + name + "' of type 'armor'");
                                double protectionamount = Armor.defaultProtectionAmount;    if(itemFile.get("protectionamount") != null) { protectionamount = Double.parseDouble((String)itemFile.get("protectionamount")); }
                                String description = Armor.defaultDescription;              if(itemFile.get("description") != null) { description = (String)itemFile.get("description"); }
                                //Add it
                                armors.add(new Armor(name, description, protectionamount, armorCustomVariables));
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
                                JSONObject itemFile = null;
                                try{ itemFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/tools/" + f)));  } catch(ParseException | IOException e) { Display.displayError("Having problems with parsing tool in file '" + f + "'"); continue; }
                                if(itemFile == null) { continue; }
                                String name = Tool.defaultName; if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                Display.displayPackMessage("Loading item '" + name + "' of type 'tool'");
                                String description = Tool.defaultDescription;               if(itemFile.get("description") != null) { description = (String)itemFile.get("description"); }
                                int durability = Tool.defaultDurability;                  if(itemFile.get("durability") != null) { durability = Integer.parseInt((String)itemFile.get("durability")); }
                                //Add it
                                tools.add(new Tool(name, description, toolCustomVariables, durability));
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
                                JSONObject itemFile = null;
                                try{ itemFile = (JSONObject) parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/specialitems/" + f))); } catch(ParseException | IOException e) { Display.displayError("Having problems with parsing special item in file '" + f + "'"); continue; }
                                if(itemFile == null) { continue; }
                                String name = SpecialItem.defaultName;                      if(itemFile.get("name") != null) { name = (String)itemFile.get("name");}
                                if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { Display.changePackTabbing(false); continue; }
                                Display.displayPackMessage("Loading item '" + name + "' of type 'specialitem'");
                                String description = SpecialItem.defaultDescription;        if(itemFile.get("description") != null) { name = (String)itemFile.get("description"); }
                                specialItems.add(new SpecialItem(name, description, specialitemCustomVariables));
                                //Add it
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
        return true;
    }

    /**
     * Loads the custom variables.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     */
    public static void loadCustomVariables() {
        Display.displayProgressMessage("Loading the custom variables...");
        Display.changePackTabbing(true);
        if(!enemyDir.exists()) { Display.displayError("Could not find the custom variables directory."); Display.changePackTabbing(false); return;}
        ArrayList<String> usedNames = new ArrayList<String>();
        File directory = customVariablesDir;
        //Determine if there is a pack to be loaded
        if(packUsed != null && packUsed.exists() && packUsed.isDirectory()) {
            for(String s : packUsed.list()) {
                if(s.equals("customVariables")) {
                    File pack = new File(packUsed.getPath() + "/customVariables");
                    if(pack.exists() && pack.isDirectory()) {
                        directory = pack;
                        Display.displayPackMessage("loading custom variables from pack '" + packUsed.getName() + "'");
                        parsingPack = true;
                    }
                }
            }
        }
        //Load them
        for(int num=0; num<2; num++) {
            if(!parsingPack) { num++; Display.displayPackMessage("Loading custom variables from the default pack."); }
            Display.changePackTabbing(true);
            if(directory.list() != null) {
                for(String s : directory.list()) {
                    ArrayList<CustomVariable> variables = new ArrayList<CustomVariable>();
                    Display.displayPackMessage("Loading custom variables from file '" + s + "'");
                    Display.changePackTabbing(true);
                    JSONObject itemFile = null;
                    try { itemFile = (JSONObject)parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + s))); } catch(IOException | ParseException e) { e.printStackTrace(); Display.displayPackError("Having trouble parsing custom variables from file '" + s + "'"); continue; }
                    JSONArray valuesArray = null; if(itemFile.get("values") != null) { valuesArray = (JSONArray)itemFile.get("values"); }
                    if(valuesArray != null) {
                        for (int i=0; i<valuesArray.size(); i++) {
                            JSONObject obj = (JSONObject)valuesArray.get(i);
                            String name = null;
                            if(obj.get("name") != null) {
                                name = (String)obj.get("name");
                            } else { Display.displayPackError("This custom variable does not have a name. Omitting..."); continue; }
                            if(usedNames.contains(name)) { Display.displayPackError("Found duplicate custom varaible '" + name + "'"); continue; };
                            Display.displayPackMessage("Loading custom variable '" + name + "'");
                            Object value = null;
                            if(obj.get("value") != null) {
                                value = obj.get("value");
                            } else { Display.displayPackError("This custom variable does not have a value. Omitting..."); continue; }
                            Class type = null;
                            int typeRaw = 0;
                            if(obj.get("type") != null) {
                                typeRaw = Integer.parseInt((String)obj.get("type"));
                            } else { Display.displayPackError("This custom variable does not have a type. Omitting..."); continue; }
                            boolean isSaved = false;
                            if(obj.get("isSaved") != null) {
                                isSaved = Boolean.parseBoolean((String)obj.get("isSaved"));
                            }
                            //Cast the values to the correct type
                            if(value.equals("%null%")) { value = null; }
                            else if(typeRaw == 0) {
                                value = Integer.parseInt((String)value);
                            } else if(typeRaw == 1) {
                                if(value.equals("%null%")) { value = null; }
                                else { value = (String)value; }
                            } else if(typeRaw == 2) {
                                value = Double.parseDouble((String)value);
                            } else if(typeRaw == 3) {
                                value = Boolean.parseBoolean((String)value);
                            } else if(typeRaw == 4) {
                                try { value = Class.forName((String)value); } catch(ClassNotFoundException e) { Display.displayPackError("The custom variable tried to get a class that does not exist. Omitting... "); Display.changePackTabbing(false); continue; }
                            } else {
                                Display.displayPackError("This method has arguments that are not String, int, double, boolean, or class. Omitting...");
                                continue;
                            }
                            //InOuptut is true by default
                            boolean inOutput = true;
                            if(obj.get("inoutput") != null) { inOutput = Boolean.parseBoolean((String)obj.get("inoutput")); }
                            variables.add(new CustomVariable(name, value, type, inOutput, isSaved));
                            usedNames.add(name);
                        }
                    }
                    //Add the customVariable to the correct arraylist
                    if(s.equals("player.json")) { playerCustomVariables = variables; }
                    else if(s.equals("enemy.json")) { enemyCustomVariables = variables; }
                    else if(s.equals("weapon.json")) { weaponCustomVariables = variables; }
                    else if(s.equals("armor.json")) { armorCustomVariables = variables; }
                    else if(s.equals("specialitem.json")) { specialitemCustomVariables = variables; }
                    else if(s.equals("tool.json")) { toolCustomVariables = variables; }
                    Display.changePackTabbing(false);
                }
                Display.changePackTabbing(false);
            }
            directory = customVariablesDir;
            parsingPack = false;
            Display.changePackTabbing(false);
        }
        Display.changePackTabbing(false);
    }

    /**
     * Loads the configured colors.
     * <p>If the config directory does not exist, then a new one is created</p>
     */
    public static void loadConfig() {
        Display.displayProgressMessage("Loading config...");
        if(configDir.exists()) {
            //Get the pack specified in the pack file
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

    /**
     * Loads the default calues.
     * <p>First loads from the pack (If one is specified in the config file), then loads from the default pack.
     */
    public static void loadDefaultValues() {
        //Custom parsing tags are located in interface packs
        Display.displayProgressMessage("Loading the default player/enemy/item values...");
        Display.changePackTabbing(true);
        File directory = defaultValuesDirectory;
        //Determine if there is a pack to be loaded
        if(packUsed != null && packUsed.exists() && packUsed.isDirectory() && packUsed.list() != null) {
            for(String s : packUsed.list()) {
                if(s.equals("defaultvalues")) {
                    File possibleDirectory = new File(packUsed.getPath() + "/defaultValues");
                    if(possibleDirectory.isDirectory()) {
                        directory = possibleDirectory;
                        Display.displayPackMessage("loading default player/enemy/item values from pack '" + packUsed.getName() + "'");
                        parsingPack = true;
                    }
                }
            }
        }
        if(directory.list() == null) { Display.changePackTabbing(false); return; }
        //Load them
        for(String s : directory.list()) {
            if(s.equals("player.json")) {
                File file = new File(directory.getPath() + "/player.json");
                if(!file.exists()) { continue; }
                Display.displayPackMessage("Loading player values");
                Display.changePackTabbing(true);
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
                    if(valuesFile.get("hpHealthPotionsGive") != null) {             Player.defaultHpHealthPotionsGive = Integer.parseInt((String)valuesFile.get("hpHealthPotionsGive")); }
                    if(valuesFile.get("turnsStrengthPotionsGive") != null) {        Player.defaultTurnsStrengthPotionsGive = Integer.parseInt((String)valuesFile.get("turnsStrengthPotionsGive")); }
                    if(valuesFile.get("turnsInvincibilityPotionsGive") != null) {   Player.defaultTurnsInvincibilityPotionsGive = Integer.parseInt((String)valuesFile.get("turnsInvincibilityPotionsGive")); }
                    if(valuesFile.get("turnsWithStrengthLeft") != null) {           Player.defaultTurnsWithStrengthLeft = Integer.parseInt((String)valuesFile.get("turnsWithStrengthLeft")); }
                    if(valuesFile.get("turnsWithInvincibilityLeft") != null) {      Player.defaultTurnsWithInvincibilityLeft = Integer.parseInt((String)valuesFile.get("turnsWithInvincibilityLeft")); }
                    if(valuesFile.get("strengthPotionMultiplier") != null) {        Player.defaultStrengthPotionMultiplier = Integer.parseInt((String)valuesFile.get("strengthPotionMultiplier")); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) { Display.changePackTabbing(false); continue; }
            } else if(s.equals("enemy.json")) {
                File file = new File(directory.getPath() + "/enemy.json");
                if(!file.exists()) { continue; }
                Display.displayPackMessage("Loading enemy values");
                Display.changePackTabbing(true);
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Enemy values
                    if(valuesFile.get("name") != null) {                        Enemy.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("health") != null) {                      Enemy.defaultHp = Integer.parseInt((String)valuesFile.get("health")); }
                    if(valuesFile.get("maxhp") != null) {                       Enemy.defaultMaxhp = Integer.parseInt((String)valuesFile.get("maxhp")); }
                    if(valuesFile.get("strength") != null) {                    Enemy.defaultStrength = Integer.parseInt((String)valuesFile.get("strength")); }
                    if(valuesFile.get("levelRequirement") != null) {            Enemy.defaultLevelRequirement = Integer.parseInt((String)valuesFile.get("levelRequirement")); }
                    if(valuesFile.get("turnsWithInvincibilityLeft") != null) {  Enemy.defaultTurnsWithInvincibiltyLeft = Integer.parseInt((String)valuesFile.get("turnsWithInvisibilityLeft")); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) {Display.changePackTabbing(false); continue; }
            } else if(s.equals("item.json")) {
                File file = new File(directory.getPath() + "/item.json");
                if(!file.exists()) { continue; }
                Display.displayPackMessage("Loading item values");
                Display.changePackTabbing(true);
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Item values
                    if(valuesFile.get("name") != null) {                        Item.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Item.defaultDescription = (String)valuesFile.get("description"); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) { Display.changePackTabbing(false); continue; }
            } else if(s.equals("weapon.json")) {
                File file = new File(directory.getPath() + "/weapon.json");
                if(!file.exists()) { continue; }
                Display.displayPackMessage("Loading weapon values");
                Display.changePackTabbing(true);
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Weapon values
                    if(valuesFile.get("name") != null) {                        Weapon.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Weapon.defaultDescription = (String)valuesFile.get("description"); }
                    if(valuesFile.get("damage") != null) {                      Weapon.defaultDamage = Integer.parseInt((String)valuesFile.get("damage")); }
                    if(valuesFile.get("critChance") != null) {                  Weapon.defaultCritChance = Integer.parseInt((String)valuesFile.get("critChance")); }
                    if(valuesFile.get("missChance") != null) {                  Weapon.defaultMissChance = Integer.parseInt((String)valuesFile.get("missChance")); }
                    if(valuesFile.get("durability") != null) {                  Weapon.defaultDurability = Integer.parseInt((String)valuesFile.get("durability")); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) { e.printStackTrace(); Display.changePackTabbing(false); continue; }
            } else if(s.equals("armor.json")) {
                File file = new File(directory.getPath() + "/armor.json");
                if(!file.exists()) { continue; }
                Display.displayPackMessage("Loading armor values");
                Display.changePackTabbing(true);
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Armor values
                    if(valuesFile.get("name") != null) {                        Armor.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Armor.defaultDescription = (String)valuesFile.get("description"); }
                    if(valuesFile.get("protectionAmount") != null) {            Armor.defaultName = (String)valuesFile.get("protectionAmount"); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) { Display.changePackTabbing(false); continue; }
            } else if(s.equals("tool.json")) {
                File file = new File(directory.getPath() + "/tool.json");
                if(!file.exists()) { continue; }
                Display.displayPackMessage("Loading tool values");
                Display.changePackTabbing(true);
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //Tool values
                    if(valuesFile.get("name") != null) {                        Tool.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 Tool.defaultDescription = (String)valuesFile.get("description"); }
                    if(valuesFile.get("durability") != null) {                  Tool.defaultDurability = Integer.parseInt((String)valuesFile.get("durability")); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) { Display.changePackTabbing(false); continue; }
            } else if(s.equals("specialitem.json")) {
                File file = new File(directory.getPath() + "/specialitem.json");
                if(!file.exists()) {continue; }
                Display.displayPackMessage("Loading specialitem values");
                Display.changePackTabbing(true);
                try {
                    JSONObject valuesFile = (JSONObject)parser.parse(new FileReader(file));
                    //SpecialItem values
                    if(valuesFile.get("name") != null) {                        SpecialItem.defaultName = (String)valuesFile.get("name"); }
                    if(valuesFile.get("description") != null) {                 SpecialItem.defaultDescription = (String)valuesFile.get("description"); }
                    Display.changePackTabbing(false);
                } catch (IOException | ParseException e) { Display.changePackTabbing(false); continue; }
            }
            parsingPack = false;
            directory = defaultValuesDirectory;
        }
        Display.changePackTabbing(false);
        Display.displayPackMessage("Default player/enemy/item values loaded.");
        return;
    }

    /**
     * Loads a save from the given file name.
     * <p>Loads all values from the file and creates a new player instance with those values.
     * @param saveName  The name of the save file.
     * @return          True if successful. False is unsuccessful.
     */
    public static boolean loadGame(String saveName) {

        if(!PackMethods.areThereAnySaves()){ addToOutput("There are no saves, create one."); return false;}

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
            boolean gameBeaten = false;                                                 if(stats.get("gameBeaten") != null)                 {gameBeaten = Boolean.parseBoolean((String)stats.get("gameBeaten"));}

            JSONArray inventory = (JSONArray)file.get("inventory");

            ArrayList<Item> newInventory = new ArrayList<Item>();

            //Inventory items
            if(inventory != null && inventory.size()>0) {
                for (int i=0; i<newInventory.size(); i++) {
                    JSONObject jsonobj = (JSONObject)inventory.get(i);
                    if(jsonobj.get("name") == null) { continue; }
                    if(jsonobj.get("itemtype") != null) {
                        if(jsonobj.get("itemtype").equals("armor")) {
                            Armor item = getArmorByName((String)jsonobj.get("name"));
                            if(item != null) { newInventory.add(item); }
                        } else if(jsonobj.get("itemtype").equals("weapon")) {
                            Weapon item = getWeaponByName((String)jsonobj.get("name"));
                            if(jsonobj.get("durability") != null) { item.setDurability(Integer.parseInt((String)jsonobj.get("durability"))); }
                            if(item != null) { newInventory.add(item); }
                        } else if(jsonobj.get("itemtype").equals("tool")) {
                            Tool item = getToolByName((String)jsonobj.get("name"));
                            if(jsonobj.get("durability") != null) { item.setDurability(Integer.parseInt((String)jsonobj.get("durability"))); }
                            if(item != null) { newInventory.add(item); }
                        } else if(jsonobj.get("itemtype").equals("specialitem")) {
                            SpecialItem item = getSpecialItemByName((String)jsonobj.get("name"));
                            if(item != null) { newInventory.add(item); }
                        }
                    }
                }
            }

            Weapon currentWeapon = null;
            String currentWeaponString = Player.defaultCurrentWeaponName;               if(stats.get("currentWeapon") != null)              {currentWeaponString = (String)stats.get("currentWeapon");}
            if(newInventory.contains(getWeaponByName(currentWeaponString))) {
                for(Weapon w : weapons) {
                    if(w.getName().equals(currentWeaponString)) {
                        currentWeapon = w;
                    }
                }
            }
            if(currentWeapon == null) {
                for(Weapon w : weapons) {
                    if(w.getName().equals(player.defaultCurrentWeaponName)) {
                        currentWeapon = w;
                    }
                }
            }

            ArrayList<Achievement> playerAchievements = new ArrayList<Achievement>();
            JSONArray achievementsArray = (JSONArray)file.get("achievements");

            // Adds the previously earned achievements to a new array
            if(achievementsArray != null  && achievementsArray.size()>0) {
                for(int i=0; i<achievementsArray.size(); i++) {
                    for(Achievement a : achievements) {
                        if(achievementsArray.get(i) == a.getName()) {
                            playerAchievements.add(a);
                        }
                    }
                }
            }

            ArrayList<CustomVariable> newPlayerCustomVariables = new ArrayList<CustomVariable>();
            JSONArray customVariables = (JSONArray)file.get("customVariables");

            ArrayList<CustomVariable> unusedCustomVariables = new ArrayList<CustomVariable>(playerCustomVariables);

            //loads the custom variables that were saved
            if((customVariables != null && customVariables.size()>0) && (playerCustomVariables != null && playerCustomVariables.size()>0)) {
                for(int i=0; i<customVariables.size(); i++) {
                    JSONObject obj = (JSONObject)customVariables.get(i);
                    if(obj.get("name") != null && obj.get("type") != null && obj.get("value") != null) {
                        String name = "";
                        int type = Integer.parseInt((String)obj.get("type"));
                        Object value = null;
                        //Null values are specified by "%null%"
                        if(((String)value).equals("%null%")) { value = null; }
                        else if(type == 0) {
                            value = (String)obj.get("value");
                        } else if(type == 1) {
                            value = Integer.parseInt((String)obj.get("value"));
                        } else if(type == 2) {
                            value = Double.parseDouble((String)obj.get("value"));
                        } else if(type == 3) {
                            value = Boolean.parseBoolean((String)obj.get("value"));
                        } else if(type == 4) {
                            try { value = Class.forName((String)obj.get("value")); } catch(ClassNotFoundException e) { continue; }
                        }

                        for(int p=0; p<unusedCustomVariables.size(); i++) {
                            CustomVariable cv = unusedCustomVariables.get(i);
                            if(cv.getName().equals(obj.get("name"))) {
                                cv.setValue(value);
                                newPlayerCustomVariables.add(cv);
                                unusedCustomVariables.remove(p);
                                break;
                            }
                        }
                    }
                }
                //Add any remaining custom variables
                for(CustomVariable cv : unusedCustomVariables) { newPlayerCustomVariables.add(cv); }
            }

            //Create a new player instance with the loaded values
            player = new Player(hp, maxhp, coins, magic, metalscraps, level, experience, score, healthPotions, strengthPotions, invincibilityPotions, currentWeapon, gameBeaten, newInventory, playerAchievements, playerCustomVariables);
            addToOutput("Loaded save '" + saveName + "'");

        } catch (IOException | ParseException e) { addToOutput("Unable to read the save"); e.printStackTrace(); return false; }

        return true;

    }

    /**
     * Creates a new save file.
     * <p>Fails if there is already a save with the name given.</p>
     * @param name  The name of the new save.
     */
    public static void newGame(String name) {

        // Tells the player if they would overwrite a game (And doesnt allow them to do so)
        if(PackMethods.getSaveFiles().contains(name)) {
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
        stats.put("currentWeapon", Player.defaultCurrentWeaponName);

        JSONArray inventory = new JSONArray();

        Weapon currentWeapon = null;
        if(currentWeapon == null) {
            for(Weapon w : weapons) {
                if(w.getName().equals(player.defaultCurrentWeaponName)) {
                    currentWeapon = w;
                    JSONObject weapon = new JSONObject();
                    weapon.put("name", w.getName());
                    weapon.put("itemtype", "weapon");
                    inventory.add(weapon);
                }
            }
        }

        //Initializes the game with a default player
        player = new Player(currentWeapon, playerCustomVariables);
        player.setLocation("saves");

        JSONArray customVariables = new JSONArray();
        //for all customvariables that are to be saved
        if(player.getCustomVariables() != null) {
            for(CustomVariable cv : player.getCustomVariables()) {
                if(cv.getIsSaved()) {
                    JSONObject obj = new JSONObject();
                    obj.put("name",cv.getName());
                    if(cv.getValueType().equals(String.class)) {
                        obj.put("type", "0");
                    } else if(cv.getValueType().equals(int.class)) {
                        obj.put("type", "1");
                    } else if(cv.getValueType().equals(double.class)) {
                        obj.put("type", "2");
                    } else if(cv.getValueType().equals(boolean.class)) {
                        obj.put("type", "3");
                    } else if(cv.getValueType().equals(Class.class)) {
                        obj.put("type", "4");
                    } else {
                        continue;
                    }
                    customVariables.add(obj);
                }
            }
        }

        base.put("customVariables", customVariables);
        base.put("inventory", inventory);
        base.put("stats", stats);
        base.put("name", name);

        addToOutput("Added new save '" + name + "'");

        //Writes it
        try (FileWriter w = new FileWriter(newGameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Saves the game.
     * <p>Rewrites the whole file.</p>
     * @return      True if successful, False if unsuccessful.
     */
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

        JSONArray inventory = new JSONArray();
        //For items in inventory
        if(player.getInventory() != null) {
            for(Item i : player.getInventory()) {
                JSONObject jsonarray = new JSONObject();
                jsonarray.put("name", i.getName());
                jsonarray.put("itemtype", i.getItemType());
                inventory.add(jsonarray);
            }
        }

        JSONArray earnedAchievements = new JSONArray();
        //For all achievements
        if(player.getAchievements() != null) {
            for(Achievement a : player.getAchievements()) {
                earnedAchievements.add(a.getName());
            }
        }

        JSONArray customVariables = new JSONArray();
        //for all customvariables that are to be saved
        if(player.getCustomVariables() != null) {
            for(CustomVariable cv : player.getCustomVariables()) {
                if(cv.getIsSaved()) {
                    JSONObject obj = new JSONObject();
                    obj.put("name",cv.getName());
                    //Saves the type with the correct number
                    if(cv.getValue() == null) { obj.put("value", "%null%"); }
                    else { obj.put("value", cv.toString()); }
                    if(cv.getValueType().equals(String.class)) {
                        obj.put("type", "0");
                    } else if(cv.getValueType().equals(int.class)) {
                        obj.put("type", "1");
                    } else if(cv.getValueType().equals(double.class)) {
                        obj.put("type", "2");
                    } else if(cv.getValueType().equals(boolean.class)) {
                        obj.put("type", "3");
                    } else if(cv.getValueType().equals(Class.class)) {
                        obj.put("type", "4");
                    } else {
                        continue;
                    }
                    customVariables.add(obj);
                }
            }
        }

        base.put("customVariables", customVariables);
        base.put("achievements", earnedAchievements);
        base.put("inventory", inventory);
        base.put("stats", stats);
        base.put("name", gameName);

        //Writes it
        try (FileWriter w = new FileWriter(currentSaveFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); return false; }

        return true;

    }

    /**
     * Adds a save to the {@link #saves} arraylist.
     * @param name  The name to be added.
     */
    public static void addSave(String name) { saves.add(name); }
    /**
     * Removes a save file.
     * <p>Removes the save from the {@link #saves} arraylist.<p>
     * @param name  The name of the save.
     */
    public static void removeSave(String name) {
        if(name == null) { return; }
        if(name.lastIndexOf(".") != -1) { name = name.substring(0,name.lastIndexOf(".")); }
        PackMethods.getSaveFiles();
        //Makes sure the player isnt attempting to delete the save that is currently being used and the player hasnt beaten the game
        //When the game has been beaten, saves are not deleted when the player dies
        if(currentSaveFile != null && ( ( (name+".json").equals(currentSaveFile.getName()) && player.getAlive()) || player.getGameBeaten())) {return; }
        boolean saveExists = false;
        //Find the save in the saves array
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

    /**
     * Sorts the enemies by difficulty from lowest to highest and sets {@link #enemies}.
     */
    public static void sortEnemies() {
        if(enemies.size() < 2) { return; } //There is no need to sort if there are no enemies or just one
        //Iterate through the enemies and find out which has the lowest difficulty in the array
        //Then add that enemy to the sorted array and remove it from the remaining array
        //The loop continues iterating only on the unsorted (remaining array) enemies to save resources
        ArrayList<Enemy> sorted = new ArrayList<Enemy>();
        ArrayList<Enemy> remaining = new ArrayList<Enemy>(enemies);
        while(remaining.size() != 0) {
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
        //Set the enemies to the new sorted array
        enemies = sorted;
    }

    /*** Sets the possible enemies arraylist in TextFighter with enemies that meet their requirements for the player to fight.*/
    public static void setPossibleEnemies() {
        ArrayList<Enemy> possible = new ArrayList<Enemy>();
        for(Enemy e : enemies) {
            boolean valid = true;
            if(e.getLevelRequirement() <= player.getLevel()) {
                if(e.getRequirements() != null) {
                    //Make sure the requirements are met for the player to be able to fight the enemy
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

    /**
     * Gets and handles player input.
     * @return      True if a valid choice. False if not.
     */
    public static boolean invokePlayerInput() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            //Display the prompt to the screen
            Display.promptUser();
            //Get the player's input
            String input = in.readLine();
            Display.resetColors();
            Display.previousCommandString = input;
            if(input.trim() != null) {
                boolean validChoice = false;
                //Iterate through all current possible choices
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
                if(validChoice) { //The choice does exist
                    ArrayList<String> inputArrayList = new ArrayList<String>(Arrays.asList(input.split(" ")));
                    String commandName = inputArrayList.get(0);
                    inputArrayList.remove(0);
                    for(Choice c : player.getLocation().getPossibleChoices()) {
                        if(c.getName().equals(commandName)) {
                            //Pass the input to the choice so that the choice can put the input where placeholders ("%ph%") are in the arguments
                            if(inputArrayList != null) {
                                return(c.invokeMethods(inputArrayList));
                            } else {
                                return(c.invokeMethods(new ArrayList<String>()));
                            }
                        }
                    }
                } else { //The choice doesnt exist
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
        }
        return false;
    }

    /**
     * Sets up a fight.
     * @param en    The name of the enemy to be fought.
     * @return      False if invalid enemy, else true.
     */
    public static boolean setUpFight(String en) {
        if(en == null) { addToOutput("No enemy name given to fight."); }
        boolean validEnemy = false;
        //Gets the enemy given
        for(Enemy enemy : possibleEnemies) {
            if(enemy.getName().equals(en)) {
                try {
                    Enemy newEnemy = (Enemy)enemy.clone();
                    currentEnemy = newEnemy;
                    validEnemy = true;
                } catch (CloneNotSupportedException e) { 
                    addToOutput("Could not create an enemy of that type! ('" + e + "')"); 
                    e.printStackTrace(); 
                    return false;
                }
            }
        }
        if(validEnemy) {
            player.setInFight(true);
            PackMethods.movePlayer("fight");
        } else {
            addToOutput("Invalid enemy: '" + en + "'");
        }
    }

    /**
     * Moves the player to the "win" location.
     */
    public static void playerWins() {
        PackMethods.movePlayer("Win");
    }

    //Exit game. The reason I need this method is because it wont let me use System.exit(code) in packs (Because Class.forName() claims the class doesn't exist)
    /**
     * Calls System.exit()
     * @param code  The exit code.
     */
    public static void exitGame(int code) { System.exit(code); }

    /**
     * Determine if a save is loaded.
     * @return      True if a game is loaded. False if not.
     */
    public static boolean gameLoaded() { return (currentSaveFile != null); }

    /**
     * Deals with enemy actions.
     */
    public static void fight() {
        player.decreaseTurnsWithStrengthLeft(1);
        player.decreaseTurnsWithInvincibilityLeft(1);
        if(currentEnemy.getHp() < 1) {
            player.setInFight(false);
            addToOutput("Your enemy has died!");
            currentEnemy.invokePostmethods();
            //Give the player their rewards
            ArrayList<String> rewards = currentEnemy.invokeRewardMethods();
            if(rewards != null && rewards.size() > 0) {
                addToOutput("Rewards:\n");
                for(String s : rewards) {
                    addToOutput(s + "\n");
                }
            }
            if(currentEnemy.getIsFinalBoss()) { playerWins(); }
            currentEnemy = null;
        } else {
            // Does enemy actions
            if(currentEnemy.getPossibleActions() != null && currentEnemy.getPossibleActions().size() > 0) {
                Random random = new Random();
                int number = 0;
                if(currentEnemy.getStrength() < 1) { //If the enemy has no attack, then dont allow the attack action to happen
                    number = (Integer)(random.nextInt(currentEnemy.getPossibleActions().size()))+1;
                } else {
                    number = (Integer)(random.nextInt(currentEnemy.getPossibleActions().size()*2))+1;
                }
                //Perform the action based on number index
                if(number > currentEnemy.getPossibleActions().size()) { //Attack the player
                    currentEnemy.attack(currentEnemy.getStrength(), null);
                } else { //Perform an action other than attack
                    currentEnemy.getPossibleActions().get( random.nextInt( currentEnemy.getPossibleActions().size() ) ).invokeMethods();
                }
            }
            player.setCanBeHurtThisTurn(true);
            currentEnemy.decreaseTurnsWithInvincibilityLeft(1);
        }
    }

    /**
     * Orchestrates player turns.
     */
    public static void playGame() {
        //Display the interface for the user
        Display.displayInterfaces(player.getLocation());
        boolean validInput = invokePlayerInput();
        //If the player inputted something valid and the player is in a fight, then do enemy action stuff
        if(validInput && player.getInFight() && currentEnemy != null) { fight(); }
        if(player.getHp() < 1) { player.setInFight(true); player.died(); }
        Display.clearScreen();
        Display.displayPreviousCommand();
        //Determine if any achievements should be recieved
        for(Achievement a : achievements) {
            if(!player.getAchievements().contains(a)) {
                boolean earned = true;
                //Make sure the requirements have been met
                for(Requirement r : a.getRequirements()) {
                    if(!r.invokeMethod() == r.getNeededBoolean()) {
                        earned = false;
                        break;
                    }
                } //Give the player the achievement if all requirements are mey
                if(earned) {
                    player.achievementEarned(a);
                }
            }
        }
        //Print the output of the previous player choice
        if(output != null) {
            Display.displayOutputMessage(output);
        }
        output="";
        if(needsSaving && currentSaveFile != null && (player.getAlive() || player.getGameBeaten())) { saveGame(); }
        needsSaving = false;
    }

    /**
     * Detemine if the game is in test mode, load resources, and main game loop
     * @param args  Command line input.
     */
    public static void main(String[] args) {
        //Determine if the game is run in pack test mode
        //When in pack test mode, the game just loads the resources and tells the user if there is anything wrong
        for(String a : args) {
            if(a.equals("test")) {
                testMode = true;
            }
        }
        if(!testMode) { Display.clearScreen(); }
        //Load all the resources and make sure they are loaded correctley
        if (!loadResources()) {
            Display.displayError("An error occured while trying to load the resources!\nMake sure they are in the correct directory.");
            System.exit(1);
        }
        player = new Player(playerCustomVariables);
        //If the game is in test mode, then the game should not run for the user (Only get modpack feedback)
        if(!testMode) {
            PackMethods.getSaveFiles();
            player.setLocation("start");
            while(player.getAlive() || player.getGameBeaten()) {
                playGame();
            }
        }
    }

    //Do nothing at all lmao
    /**
     * Does literally nothing.
     */
    public static void doNothing() { return; }
}
