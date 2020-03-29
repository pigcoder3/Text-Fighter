package com.seanjohnson.textfighter;

import com.seanjohnson.textfighter.*;
import com.seanjohnson.textfighter.display.*;
import com.seanjohnson.textfighter.enemy.*;
import com.seanjohnson.textfighter.item.*;
import com.seanjohnson.textfighter.location.*;
import com.seanjohnson.textfighter.method.*;

import java.util.ArrayList;

//This class is intended to hold methods that are mainly used by packs
public class PackMethods {

    //Output methods
    /**
     * Returns all of the simple outputs from all items of the given type in the player's inventory.
     * <p>If the type is null or "all", then the method returns simple outputs of all items in the player's inventory.</p>
     * @param type  The type of the item. If null is given, then searches for all types.
     * @return      All of the item simple outputs of the given type in the player's inventory all put into one ArrayList.
     */
    public static ArrayList<String> getAllItemSimpleOutputsFromInventory(String type) {
        if(type == null) { type = "all"; }
        //If type equals "all", then get all things
        ArrayList<String> outputs = new ArrayList<String>();
        if(TextFighter.player.getInventory() == null) { return new ArrayList<String>(); }
        for(Item i : TextFighter.player.getInventory()) {
            //The items must be casted so that they can get their own getSimpleOutput() methods
            if(i.getItemType().equals(type) || type.equals("all")) {
                if(i.getItemType().equals("weapon")) { outputs.add(((Weapon)i).getSimpleOutput()); }
                else if(i.getItemType().equals("armor")) { outputs.add(((Armor)i).getSimpleOutput()); }
                else if(i.getItemType().equals("tool")) { outputs.add(((Tool)i).getSimpleOutput()); }
                else if(i.getItemType().equals("specialitem")) { outputs.add(((SpecialItem)i).getSimpleOutput()); }
            }
        }
        return outputs;
    }
    /**
     * Returns all of the outputs from all items of the given type in the player's inventory.
     * <p>If the type is null or "all", then the method returns outputs of all items in the player's inventory.</p>
     * @param type  The type of the item. If null is given, then searches for all types.
     * @return      All of the item outputs of the given type in the player's inventory all put into one ArrayList.
     */
    public static ArrayList<String> getAllItemOutputsFromInventory(String type) {
        if(type == null) { type = "all";}
        //If type equals "all", then get all things
        ArrayList<String> outputs = new ArrayList<String>();
        if(TextFighter.player.getInventory() == null) { return new ArrayList<String>(); }
        for(Item i : TextFighter.player.getInventory()) {
            //The items must be casted so that they can get their own getOutput() methods
            if(i.getItemType().equals(type) || type.equals("all")) {
                if(i.getItemType().equals("weapon")) { outputs.add(((Weapon)i).getOutput()); }
                else if(i.getItemType().equals("armor")) { outputs.add(((Armor)i).getOutput()); }
                else if(i.getItemType().equals("tool")) { outputs.add(((Tool)i).getOutput()); }
                else if(i.getItemType().equals("specialitem")) { outputs.add(((SpecialItem)i).getOutput()); }
            }
        }
        return outputs;
    }

    //These next two get the outputs of the items in the arrays that store all of them
    /** Returns all of the outputs of all items in the TextFighter item arraylists.
     * <p>If the type is null or "all", then the method returns the outputs of all items.</p>
     * @param type  The type of the item. If null is given, then searches for all types.
     * @return      all of the outputs of all items in the TextFighter item arraylists.
     */
    public static ArrayList<String> getAllItemSimpleOutputs(String type) {
        if(type == null) { type = "all";}
        //If type equals "all", then get all things
        ArrayList<String> outputs = new ArrayList<String>();
        if(type.equals("armor") || type.equals("all")) {
            for(Armor i : TextFighter.armors) {
                outputs.add(i.getSimpleOutput());
            }
        } if(type.equals("weapon") || type.equals("all")) {
            for(Weapon i : TextFighter.weapons) {
                outputs.add(i.getSimpleOutput());
            }
        } if(type.equals("tool") || type.equals("all")) {
            for(Tool i : TextFighter.tools) {
                outputs.add(i.getSimpleOutput());
            }
        } if(type.equals("specialitem") || type.equals("all")) {
            for(SpecialItem i : TextFighter.specialItems) {
                outputs.add(i.getSimpleOutput());
            }
        }
        return outputs;
    }
    //These next two get the outputs of the items in the arrays that store all of them
    /** Returns all of the outputs of all items in the TextFighter item arraylists.
     * <p>If the type is null or "all", then the method returns the outputs of all items.</p>
     * @param type  The type of the item. If null is given, then searches for all types.
     * @return      all of the outputs of all items in the TextFighter item arraylists.
     */
    public static ArrayList<String> getAllItemOutputs(String type) {
        if(type == null) { type = "all";}
        //If type equals "all", then get all things
        ArrayList<String> outputs = new ArrayList<String>();
        if(type.equals("armor") || type.equals("all")) {
            for(Armor i : TextFighter.armors) {
                outputs.add(i.getOutput());
            }
        } if(type.equals("weapon") || type.equals("all")) {
            for(Weapon i : TextFighter.weapons) {
                outputs.add(i.getOutput());
            }
        } if(type.equals("tool") || type.equals("all")) {
            for(Tool i : TextFighter.tools) {
                outputs.add(i.getOutput());
            }
        } if(type.equals("specialitem") || type.equals("all")) {
            for(SpecialItem i : TextFighter.specialItems) {
                outputs.add(i.getOutput());
            }
        }
        return outputs;
    }

    /**
     * Returns the output of an item with the given name and type from TextFighter's item arraylists.
     * <p> If name or type is null, return null. </p>
     * <p>Note that there is no equivalent method for getting from the inventory because item values do not change after they are cloned to it.</p>
     * @param name  The name of the item. If null is given, then returns null.
     * @param type  The type of the item. If null is given, then returns null.
     * @return      output of an item with the given name and type from TextFighter's item arraylists. If name or type is null, return null.
     */
    public static String getOutputByNameAndType(String name, String type) {
        if(name == null || type == null) { return ""; }
        //Note that getting from the inventory is uneccessary because items values do not change after they are cloned to it.
        if(type.equals("armor")) {
            for(Armor i : TextFighter.armors) {
                if(i.getName().equals(name)) {
                    return i.getOutput();
                }
            }
        } else if(type.equals("weapon")) {
            for(Weapon i : TextFighter.weapons) {
                if(i.getName().equals(name)) {
                    return i.getOutput();
                }
            }
        } else if(type.equals("tool")) {
            for(Tool i : TextFighter.tools) {
                if(i.getName().equals(name)) {
                    return i.getOutput();
                }
            }
        } else if(type.equals("specialitem")) {
            for(SpecialItem i : TextFighter.specialItems) {
                if(i.getName().equals(name)) {
                    return i.getOutput();
                }
            }
        }
        return "";
    }

    //Save methods
    /**
     * Returns whether or not the save directory has any json files in it.
     * @return      If any saves are in the save directory.
     */
    public static boolean areThereAnySaves() { return(getSaveFiles().size() > 0); }
    /**
     * Returns an arraylist of strings containing all of the names of the saves in the saves directory.
     * @return      An arraylist of strings containing all of the names of the saves in the saves directory.
     */
    public static ArrayList<String> getSaveFiles() {
        ArrayList<String> filteredSaves = new ArrayList<String>();
        for(String s : TextFighter.savesDir.list()) {
            //Find out if the file is a json file
            if(s.substring(s.lastIndexOf(".")).equals(".json")) {
                filteredSaves.add(s.substring(0,s.lastIndexOf(".")));
            }
        }
        TextFighter.saves = filteredSaves;
        return filteredSaves;
    }

    /**
     * Returns an ArrayList of Strings containing the outputs of all enemies in TextFighter's possibleEnemies arraylist.
     * @return      An ArrayList of Strings containing the outputs of all enemies in TextFighter's possibleEnemies arraylist.
     */
    public static ArrayList<String> getPossibleEnemyOutputs() {
        TextFighter.setPossibleEnemies();
        ArrayList<String> outputs = new ArrayList<String>();
        for(Enemy e : TextFighter.possibleEnemies) {
            outputs.add(e.getOutput());
        }
        return outputs;
    }

    //Moves the player
    /**
     * moves the player to a new location.
     * <p> If the given location name is null, then return false (signaling failure).
     * If a location with the name given does not exist, return false (signaling failure).</p>
     * <p>the game invokes all possible postmethods of the previous location, sets the new location,
     * then invokes the possible premethods of the new location.</p>
     * @param location  The name of the location.
     * @return          Whether or not the move was successful.
     */
    public static boolean movePlayer(String location) {
        if(location == null) { return false; }
        if(TextFighter.player.getLocation() != null && location.equals(TextFighter.player.getLocation())) { return true; }
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(location)) {
                TextFighter.player.setLastLocation(TextFighter.player.getLocation().getName());
                //Invokes the post methods of the previous location assuming there was a previous one
				if(TextFighter.player.getLocation() != null) { TextFighter.player.getLocation().invokePostmethods(); }
                TextFighter.player.setLocation(location);
                TextFighter.addToOutput("Moved to " + location);
                //Invokes the pre methods of the previous location
                l.invokePremethods();
                return true;
            }
        }
        TextFighter.addToOutput("No such location '" + location + "'");
         return false;
    }


    //Calculation methods
    /**
     * Does calculations on the integers given using the given operator. Only takes ints.
     * <p>Calculations should be "+","-","*","/". If none of these are given, then 0 is returned.</p>
     * @param value1        The first integer.
     * @param calculation   The operator (+,-,*,/).
     * @param value2        The second integer.
     * @return              The value calculated from the two integers.
     */
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

    /**
     * Does calculations on the integers given using the given operator. Only takes doubless.
     * <p>Calculations should be "+","-","*","/". If none of these are given, then 0 is returned.</p>
     * @param value1        The first double.
     * @param comparison   The operator (+,-,*,/).
     * @param value2        The second double.
     * @return              The value calculated from the two doubles.
     */
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

    //comparison methods
    /**
     * Compares the two String given.
     * <p>If either string given is null, then false is returned.</p>
     * @param value1        The first string.
     * @param value2        The second string.
     * @return              Whether or not the two string are equal.
     */
    public static boolean variableComparison(String value1, String value2) { if(value1 == null || value2 == null) { return false; } return(value1.equals(value2)); } //No comparison operator necessary because you can only do '=' on strings

    /**
     * Compares the doubless given using the given operator. Only takes doubles.
     * <p>comparison should be equals, less than, more than, less than or equal, more than or equal. If none of these are given, then false is returned.</p>
     * @param value1        The first double.
     * @param comparison   The comparison operator.
     * @param value2        The second double.
     * @return              whether or not the comparison returned true.
     */
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

    /**
     * Compares the ints given using the given operator. Only takes ints.
     * <p>comparison should be equals, less than, more than, less than or equal, more than or equal. If none of these are given, then false is returned.</p>
     * @param value1        The first integer.
     * @param comparison   The comparison operator.
     * @param value2        The second integer.
     * @return              whether or not the comparison returned true.
     */
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

    /**
     * Compares two booleans to see if they are both true.
     * @param b1        The first boolean.
     * @param b2        The second boolean.
     * @return          If they are both true.
     */
    public static boolean boolAndBool(boolean b1, boolean b2) { return b1 && b2; }
    /**
     * Compares two booleans to see if one of them is true.
     * @param b1        The first boolean.
     * @param b2        The second boolean.
     * @return          If one of them is true.
     */
    public static boolean boolOrBool(boolean b1, boolean b2) { return b1 || b2; }
}
