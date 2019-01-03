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

    public static boolean needsSaving = false;

    public static void addToOutput(String msg) {
        output+=msg + "\n";
    }

    public static boolean loadResources() {
        Display.displayProgressMessage("Loading the resources...");
        resourcesDir = new File("../res");
        tagFile = new File(resourcesDir.getPath() + "/tags/tags.json");
        interfaceDir = new File(resourcesDir.getPath() + "/userInterfaces/");
        locationDir = new File(resourcesDir.getPath() + "/locations");
        savesDir = new File("../../../saves");
        enemyDir = new File(resourcesDir + "/enemies");
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
        loadConfig();
        if (!loadParsingTags() || !loadInterfaces() || !loadLocations() || !loadEnemies()) { return false; }
        if (!savesDir.exists()) {
            Display.displayWarning("The saves directory does not exist!\nCreating a new saves directory...");
            if (!new File("../../../saves/").mkdirs()) { Display.displayError("Unable to create a new saves directory!"); return false; }
        }
        return true;
    }

    public static boolean loadInterfaces() {
        try {
            Display.displayProgressMessage("Loading the interfaces...");
            if(!interfaceDir.exists()) { Display.displayError("Could not find the default interfaces directory.") return false;}
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
                if(!parsingPack) { num++; } else { Display.displayProgressMessage("Loading interfaces from the default pack.");}
                if(directory.list() != null) {
                    for (String f : directory.list()) {
                        if(f.equals("tags.json")) { continue; }
                        if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                        JSONObject interfaceFile = (JSONObject)parser.parse(new FileReader(new File(directory.getPath() + "/" + f)));
                        JSONArray interfaceArray = (JSONArray)interfaceFile.get("interface");
                        String name = (String)interfaceFile.get("name");
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
                        if(name == null) { Display.displayPackError("An interface does not have a name. Omitting..."); continue; }
                        if(interfaceArray == null) { Display.displayPackError("Interface '" + name + "' does not have an interface array. Omitting..."); continue; }
                        String uiString = "";
                        for (int i = 0; i < interfaceArray.size(); i++) { uiString += interfaceArray.get(i) + "\n"; }
                        Display.interfaces.add(new UserInterface(name, uiString));
                        usedNames.add(name);
                    }
                }
                directory = interfaceDir;
                parsingPack = false;
            }
            Display.displayProgressMessage("Interfaces loaded.");
            return true;
        } catch (FileNotFoundException e) { Display.displayError("Could not find an interface file. It was likely deleted after the program got all files in the interfaces directory."); return false; }
        catch (IOException e) { Display.displayError("IOException when attempting to load the interfaces. The permissions are likely set to be unreadable."); return false;}
        catch (ParseException e) { Display.displayError("Having trouble parsing the interface files. Will continue as expected though."); return false; }
    }
    public static boolean loadLocations() {
        try {
            Display.displayProgressMessage("Loading the locations...");
            if(!locationDir.exists()) { Display.displayError("Could not find the default locations directory.") return false;}
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
                if(!parsingPack) { num++; } else { Display.displayProgressMessage("Loading locations from the default pack."); }
                if(directory.list() != null) {
                    for (String f : directory.list()) {
                        if(!f.substring(f.lastIndexOf(".")).equals(".json")) { continue; }
                        JSONObject locationFile = (JSONObject)parser.parse(new FileReader(new File(directory.getAbsolutePath() + "/" + f)));
                        JSONArray interfaceJArray = (JSONArray)locationFile.get("interfaces");
                        String name = (String)locationFile.get("name");
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
                        if(name == null) { Display.displayPackError("A location does not have a name."); continue; }
                        if(interfaceJArray == null) { Display.displayPackError("Location '" + name + "' does not have any interfaces."); continue; }
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
                            Display.displayWarning("The location '" + name + "' does not have a choices interface. Omitting...");
                            continue;
                        }
                        JSONArray choiceArray = (JSONArray)locationFile.get("choices");
                        ArrayList<Choice> choices = new ArrayList<Choice>();
                        //Loads the choices from the file
                        if(choiceArray != null && choiceArray.size() > 0) {
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
                                } else { Display.displayPackError("The choice '" + choicename + "' in location '" + name + "' does not have any methods. Omitting..."); continue; }
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
                                        requirements.add(new Requirement(choicename, Choice.class, method, arguments, argumentTypes, clazz, field));
                                    }
                                }
                                choices.add(new Choice(choicename, desc, usage, methods, requirements));
                            }
                        } else { Display.displayPackError("The location '" + name + "' does not have any choices. Omitting..."); }
                        //Premethods - Run when the player enters the location
                        JSONArray premethodJArray = (JSONArray)locationFile.get("premethods");
                        ArrayList<Premethod> premethods = new ArrayList<Premethod>();
                        if(premethodJArray != null && premethodJArray.size() > 0) {
                            for(int p=0; p<premethodJArray.size(); p++) {
                                JSONObject obj = (JSONObject)premethodJArray.get(p);
                                ArrayList<String> arguments = (JSONArray)obj.get("arguments");
                                ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
                                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                                String method = (String)obj.get("method");
                                String clazz = (String)obj.get("class");
                                String field = (String)obj.get("field");
                                if(method == null || clazz == null) { Display.displayPackError("A premethod in location '" + name + "' has no class or method. Omitting..."); continue; }
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
                                //Gets the requirements of the premethods
                                JSONArray requirementsJArray = (JSONArray)obj.get("requirements");
                                ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                                if(requirementsJArray != null && requirementsJArray.size() > 0) {
                                    for(int g=0; g<requirementsJArray.size(); g++) {
                                        JSONObject ro = (JSONObject)requirementsJArray.get(g);
                                        ArrayList<String> requirementArguments = (JSONArray)ro.get("requirementArgs");
                                        ArrayList<String> requirementArgumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                                        ArrayList<Class> requirementArgumentTypes = new ArrayList<Class>();
                                        String requirementMethod = (String)ro.get("method");
                                        String requirementClazz = (String)ro.get("class");
                                        String requirementField = (String)ro.get("field");
                                        if(requirementMethod == null || requirementClazz == null) { Display.displayPackError("A requirement of a premethod in location '" + name + "' has no class or method. Omitting..."); continue; }
                                        //Fields can be null (Which just means the method does not act upon a field)
                                        if(argumentTypesString != null && requirementArgumentTypesString.size() > 0) {
                                            for (int h=0; h<requirementArgumentTypesString.size(); h++) {
                                                if(Integer.parseInt(requirementArgumentTypesString.get(g)) == 1) {
                                                    requirementArgumentTypes.add(int.class);
                                                } else if(Integer.parseInt(requirementArgumentTypesString.get(g)) == 1) {
                                                    requirementArgumentTypes.add(String.class);
                                                }
                                            }
                                        }
                                        requirements.add(new Requirement(null, Premethod.class, requirementMethod, requirementArguments, requirementArgumentTypes, requirementClazz, requirementField));
                                    }
                                }
                                premethods.add(new Premethod(method, arguments, argumentTypes, clazz, field, requirements));
                            }
                        }
                        locations.add(new Location(name, interfaces, choices, premethods));
                        usedNames.add(name);
                        directory = locationDir;
                        parsingPack = false;
                    }
                }
                Display.displayProgressMessage("Locations loaded.");
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }
    public static boolean loadEnemies() {
        try {
            Display.displayProgressMessage("Loading the enemies...");
            if(!enemyDir.exists()) { Display.displayError("Could not find the default enemies directory.") return false;}
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
                } catch (IOException e) { Display.displayWarning("IOException when attempting to read the enemypack's file (The file does exist). Falling back to default locations."); }
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
                if(!parsingPack) { num++; } else { Display.displayProgressMessage("Loading enemies from the default pack."); }
                if(directory.list() != null) {
                    for(String f : directory.list()) {
                        JSONObject enemyFile = (JSONObject) parser.parse(new FileReader(new File(enemyDir.getAbsolutePath() + "/" + f)));
                        JSONArray requirementsJArray = (JSONArray)enemyFile.get("requirements");
                        ArrayList<Requirement> requirements = new ArrayList<Requirement>();
                        String name = (String)enemyFile.get("name");
                        int health = Integer.parseInt((String)enemyFile.get("health"));
                        int strength = Integer.parseInt((String)enemyFile.get("strength"));
                        int levelRequirement = Integer.parseInt((String)enemyFile.get("levelRequirement"));
                        boolean finalBoss = Boolean.getBoolean((String)enemyFile.get("finalBoss"));
                        if(name == null) { Display.displayPackError("An enemy does not have a name. Omitting..."); continue; }
                        if(usedNames.contains(name) || namesToBeOmitted.contains(name)) { continue; }
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
                            requirements.add(new Requirement(name, Enemy.class, method, arguments, argumentTypes, clazz, field));
                            }
                        }
                        //postmethods - Run when the enemy dies
                        JSONArray postMethodsJArray = (JSONArray)enemyFile.get("postMethods");
                        ArrayList<Postmethod> postMethods = new ArrayList<Postmethod>();
                        if(postMethodsJArray != null && postMethodsJArray.size() > 0) {
                            for(int p=0; p<postMethodsJArray.size(); p++) {
                                JSONObject obj = (JSONObject)postMethodsJArray.get(p);
                                ArrayList<String> arguments = (JSONArray)obj.get("arguments");
                                ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
                                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                                String method = (String)obj.get("method");
                                String clazz = (String)obj.get("class");
                                String field = (String)obj.get("field");
                                if(method == null || clazz == null) { Display.displayPackError("A postmethod in enemy '" + name + "' has no class or method. Omitting..."); continue; }
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
                                //Gets the requirements of the postMethods
                                JSONArray postRequirementsJArray = (JSONArray)obj.get("requirements");
                                ArrayList<Requirement> postRequirements = new ArrayList<Requirement>();
                                if(postRequirementsJArray != null && postRequirementsJArray.size() > 0) {
                                    for(int g=0; g<postRequirementsJArray.size(); g++) {
                                        JSONObject ro = (JSONObject)requirementsJArray.get(g);
                                        ArrayList<String> requirementArguments = (JSONArray)ro.get("requirementArgs");
                                        ArrayList<String> requirementArgumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                                        ArrayList<Class> requirementArgumentTypes = new ArrayList<Class>();
                                        String requirementMethod = (String)ro.get("method");
                                        String requirementClazz = (String)ro.get("class");
                                        String requirementField = (String)ro.get("field");
                                        if(requirementMethod == null || requirementClazz == null) { Display.displayPackError("A requirement of a postmethod in enemy '" + name + "' has no class or method. Omitting..."); continue; }
                                        //Fields can be null (Which just means the method does not act upon a field)
                                        if(argumentTypesString != null && requirementArgumentTypesString.size() > 0) {
                                            for (int h=0; h<requirementArgumentTypesString.size(); h++) {
                                                if(Integer.parseInt(requirementArgumentTypesString.get(g)) == 1) {
                                                    requirementArgumentTypes.add(int.class);
                                                } else if(Integer.parseInt(requirementArgumentTypesString.get(g)) == 1) {
                                                    requirementArgumentTypes.add(String.class);
                                                }
                                            }
                                        }
                                        postRequirements.add(new Requirement(null, Premethod.class, requirementMethod, requirementArguments, requirementArgumentTypes, requirementClazz, requirementField));
                                    }
                                }
                                postMethods.add(new Postmethod(method, arguments, argumentTypes, clazz, field, requirements));
                            }
                        }
                        JSONArray rewardMethodsJArray = (JSONArray)enemyFile.get("rewardMethods");
                        ArrayList<Reward> rewardMethods = new ArrayList<Reward>();
                        if(rewardMethodsJArray != null && rewardMethodsJArray.size() > 0) {
                            for(int p=0; p<rewardMethodsJArray.size(); p++) {
                                JSONObject obj = (JSONObject)rewardMethodsJArray.get(p);
                                ArrayList<String> arguments = (JSONArray)obj.get("arguments");
                                ArrayList<String> argumentTypesString = (JSONArray)obj.get("argumentTypes");
                                ArrayList<Class> argumentTypes = new ArrayList<Class>();
                                String method = (String)obj.get("method");
                                String clazz = (String)obj.get("class");
                                String field = (String)obj.get("field");
                                String fieldclass = (String)obj.get("fieldclass");
                                int chance = Integer.parseInt((String)obj.get("chance"));
                                String rewardItem = (String)obj.get("rewardItem");
                                if(method == null || clazz == null) { Display.displayPackError("A rewardmethod in enemy '" + name + "' has no class or method. Omitting..."); continue; }
                                if(chance == 0) { Display.displayPackError("A chance in a rewardmethod of enemy '" + name + "' is 0. Omitting..."); continue; }
                                if(rewardItem == null) { Display.displayPackError("A rewardItem of enemy '" + name + "' is null. Omitting...."); continue; }
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
                                //Gets the requirements of the rewardMethods
                                JSONArray rewardRequirementsJArray = (JSONArray)obj.get("rewards");
                                ArrayList<Requirement> rewardRequirements = new ArrayList<Requirement>();
                                if(rewardRequirementsJArray != null && rewardRequirementsJArray.size() > 0) {
                                    for(int g=0; g<rewardRequirementsJArray.size(); g++) {
                                        JSONObject ro = (JSONObject)requirementsJArray.get(g);
                                        ArrayList<String> requirementArguments = (JSONArray)ro.get("requirementArgs");
                                        ArrayList<String> requirementArgumentTypesString = (JSONArray)ro.get("requirementArgTypes");
                                        ArrayList<Class> requirementArgumentTypes = new ArrayList<Class>();
                                        String requirementMethod = (String)ro.get("method");
                                        String requirementClazz = (String)ro.get("class");
                                        String requirementField = (String)ro.get("field");
                                        if(requirementMethod == null || requirementClazz == null) { Display.displayPackError("A requirement of a rewardmethod in enemy '" + name + "' has no class or method. Omitting..."); continue; }
                                        //Fields can be null (Which just means the method does not act upon a field)
                                        if(argumentTypesString != null && requirementArgumentTypesString.size() > 0) {
                                            for (int h=0; h<requirementArgumentTypesString.size(); h++) {
                                                if(Integer.parseInt(requirementArgumentTypesString.get(g)) == 1) {
                                                    requirementArgumentTypes.add(int.class);
                                                } else if(Integer.parseInt(requirementArgumentTypesString.get(g)) == 1) {
                                                    requirementArgumentTypes.add(String.class);
                                                }
                                            }
                                        }
                                        requirements.add(new Requirement(null, Postmethod.class, requirementMethod, requirementArguments, requirementArgumentTypes, requirementClazz, requirementField));
                                    }
                                }
                                rewardMethods.add(new Reward(method, arguments, argumentTypes, clazz, field, fieldclass, requirements, chance, rewardItem));
                            }
                        }
                        JSONArray enemyActionArray = (JSONArray)enemyFile.get("actions");
                        ArrayList<EnemyAction> enemyActions = new ArrayList<EnemyAction>();
                        if(enemyActionArray != null && enemyActionArray.size() > 0) {
                            for (int i=0; i<enemyActionArray.size(); i++) {
                                JSONObject obj = (JSONObject)enemyActionArray.get(i);
                                JSONArray methodJSONArray = (JSONArray)obj.get("methods");
                                ArrayList<EnemyActionMethod> methods = new ArrayList<EnemyActionMethod>();
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
                                        methods.add(new EnemyActionMethod(method, arguments, argumentTypes, clazz, field));
                                    }
                                } else { Display.displayPackError("The choice '" + choicename + "' in location '" + name + "' does not have any methods. Omitting..."); continue; }
                                //Gets requirements if there is any
                                JSONArray enemyActionRequirementsJArray = (JSONArray)obj.get("requirements");
                                ArrayList<Requirement> enemyActionRequirements = new ArrayList<Requirement>();
                                if(requirementsJArray != null && enemyActionRequirementsJArray.size() > 0) {
                                    for(int p=0; p<enemyActionRequirementsJArray.size(); p++) {
                                        JSONObject ro = (JSONObject)enemyActionRequirementsJArray.get(p);
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
                                        requirements.add(new Requirement(choicename, Choice.class, method, arguments, argumentTypes, clazz, field));
                                    }
                                }
                                enemyActions.add(new EnemyAction(methods, requirements));
                            }
                        }
                        enemies.add(new Enemy(name, health, strength, levelRequirement, requirements, finalBoss, postMethods, rewardMethods, enemyActions));
                        usedNames.add(name);
                        directory=enemyDir;
                        parsingPack=false;
                    }
                }
                Display.displayProgressMessage("Enemies loaded.");
            }
        } catch (IOException | ParseException e) { e.printStackTrace(); return false; }
        return true;
    }
    public static boolean loadParsingTags() {
        try {
            Display.displayProgressMessage("Loading the parsing tags...");
            if(!tagFile.exists()) { Display.displayError("Could not find the default tags file.") return false;}
            ArrayList<String> usedNames = new ArrayList<String>();
            boolean parsingPack = false;
            File directory = intpackDir;
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
                if(!parsingPack) { num++; } else { Display.displayProgressMessage("Loading tags from the default pack."); }
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
                    String fieldclassname = (String)obj.get("fieldname");
                    String fieldname = (String)obj.get("field");
                    if(tag == null) { Display.displayPackError("A tag does not have a name. Omitting tag..."); continue; }
                    if(usedNames.contains(tag)) { continue; }
                    if(classname == null || methodname == null) { Display.displayPackError("Tag '" + tag + "' does not have a class or method. Omitting tag..."); continue; }
                    Method method;
                    Class clazz;
                    Class fieldclass;
                    Field field;
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
                    if(fieldname != null && fieldclassname != null) {
                        try { fieldclass = Class.forName(fieldclassname); } catch (ClassNotFoundException e) { Display.displayPackError("No such class '" + fieldclassname + "'. Omitting..."); continue;}
                        try { field = fieldclass.getField(fieldname); } catch (NoSuchFieldException e) { Display.displayPackError("No such field '" + fieldname + "' in class '" + classname + "'. Omitting..."); continue;}
                    }
                    try { method = clazz.getMethod(methodname, argumentTypes.toArray(new Class[argumentTypes.size()])); } catch (NoSuchMethodException e){ Display.displayWarning("Method '" + methodname + "' of class '" + classname + "' does not exist. Omitting tag '" + tag + "'..."); e.printStackTrace(); continue;}
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
                Display.displayProgressMessage("Interface tags loaded.");
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

            player = new Player(hp, maxhp, coins, magic, level, experience, score, gameBeaten, newInventory);
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

        player = new Player(Player.defaulthp, Player.defaulthp, 0, 0, 1, 0, 0, false, newInventory);

        addToOutput("Added new save '" + name + "'");

        try (FileWriter w = new FileWriter(newGameFile);) {
            w.write(base.toJSONString());
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static boolean saveGame() {
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
                for(Premethod m : l.getPremethods()) {
                    boolean validMethod = true;
                    for(Requirement r : m.getRequirements()) {
                        if(!r.invokeMethod()) {
                            validMethod = false;
                            break;
                        }
                    }
                    if(validMethod) {
                        m.invokeMethod();
                    }
                }
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
                //Make them fight
                Random random = new Random(currentEnemy.getPossibleActions().size());
                currentEnemy.getPossibleActions().get(random.nextInt(currentEnemy.getPossibleActions().size())).invokeMethods();
                player.setCanBeHurtThisTurn(true);
                if(player.getHp() < 1) {
                    player.setAlive(false);
                    player.setInFight(false);
                } else if(currentEnemy.getHp() < 1) {
                    player.setInFight(false);
                    addToOutput("You defeated the '" + currentEnemy.getName() + "'!");
                    currentEnemy.invokePostMethods();
                    currentEnemy.invokeRewardMethods();
                    addToOutput("Rewards:");
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
        Display.clearScreen();
        Display.displayPreviousCommand();
        if(output != null) {
            Display.displayOutputMessage(output);
        }
        output="";
        if(needsSaving && currentSaveFile != null) { System.out.println("e");saveGame(); }
        needsSaving = false;
    }

    public static void main(String[] args) {
        Display.clearScreen();
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
