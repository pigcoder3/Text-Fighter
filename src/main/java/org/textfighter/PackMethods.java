package org.textfighter;

import org.textfighter.*;
import org.textfighter.display.*;
import org.textfighter.enemy.*;
import org.textfighter.item.*;
import org.textfighter.location.*;
import org.textfighter.method.*;

import java.util.ArrayList;

//This class is intended to hold methods that are mainly used by packs
public class PackMethods {

    //Output methods
    public static String getAllItemSimpleOutputsFromInventory(String type) {
        //If type equals "all", then get all things
        String s = "";
        if(TextFighter.player.getInventory() == null) { return "Your inventory is empty"; }
        for(Item i : TextFighter.player.getInventory()) {
            //The items must be casted so that they can get their own getSimpleOutput() methods
            if(i.getItemType().equals(type) || type.equals("all")) {
                if(i.getItemType().equals("weapon")) { s=s+((Weapon)i).getSimpleOutput(); }
                else if(i.getItemType().equals("armor")) { s=s+((Armor)i).getSimpleOutput(); }
                else if(i.getItemType().equals("tool")) { s=s+((Tool)i).getSimpleOutput(); }
                else if(i.getItemType().equals("specialitem")) { s=s+((SpecialItem)i).getSimpleOutput(); }
            }
        }
        return s;
    }
    public static String getAllItemOutputsFromInventory(String type) {
        if(type == null) { type = "all";}
        //If type equals "all", then get all things
        String s = "";
        if(TextFighter.player.getInventory() == null) { return "Your inventory is empty"; }
        for(Item i : TextFighter.player.getInventory()) {
            //The items must be casted so that they can get their own getOutput() methods
            if(i.getItemType().equals(type) || type.equals("all")) {
                if(i.getItemType().equals("weapon")) { s=s+((Weapon)i).getOutput(); }
                else if(i.getItemType().equals("armor")) { s=s+((Armor)i).getOutput(); }
                else if(i.getItemType().equals("tool")) { s=s+((Tool)i).getOutput(); }
                else if(i.getItemType().equals("specialitem")) { s=s+((SpecialItem)i).getOutput(); }
            }
        }
        return s;
    }

    //These next two get the outputs of the items in the arrays that store all of them
    public static String getAllItemSimpleOutputs(String type) {
        if(type == null) { type = "all";}
        //If type equals "all", then get all things
        String s = "";
        if(type.equals("armor") || type.equals("all")) {
            for(Armor i : TextFighter.armors) {
                s=s+i.getSimpleOutput();
            }
        } if(type.equals("weapon") || type.equals("all")) {
            for(Weapon i : TextFighter.weapons) {
                s=s+i.getSimpleOutput();
            }
        } if(type.equals("tool") || type.equals("all")) {
            for(Tool i : TextFighter.tools) {
                s=s+i.getSimpleOutput();
            }
        } if(type.equals("specialitem") || type.equals("all")) {
            for(SpecialItem i : TextFighter.specialItems) {
                s=s+i.getSimpleOutput();
            }
        }
        return s;
    }
    public static String getAllItemOutputs(String type) {
        if(type == null) { type = "all";}
        //If type equals "all", then get all things
        String s = "";
        if(type.equals("armor") || type.equals("all")) {
            for(Armor i : TextFighter.armors) {
                s=s+i.getOutput();
            }
        } if(type.equals("weapon") || type.equals("all")) {
            for(Weapon i : TextFighter.weapons) {
                s=s+i.getOutput();
            }
        } if(type.equals("tool") || type.equals("all")) {
            for(Tool i : TextFighter.tools) {
                s=s+i.getOutput();
            }
        } if(type.equals("specialitem") || type.equals("all")) {
            for(SpecialItem i : TextFighter.specialItems) {
                s=s+i.getOutput();
            }
        }
        return s;
    }

    public static String getOutputByNameAndType(String name, String type) {
        if(name == null || type == null) { return ""; }
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

    public static ArrayList<String> getChoiceOutputs() {
        Location l = TextFighter.player.getLocation();
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



    //Save methods
    public static boolean areThereAnySaves() { return(getSaveFiles().size() > 0); }
    public static ArrayList<String> getSaveFiles() {
        ArrayList<String> filteredSaves = new ArrayList<String>();
        for(String s : TextFighter.savesDir.list()) {
            if(s.substring(s.lastIndexOf(".")).equals(".json")) {
                filteredSaves.add(s.substring(0,s.lastIndexOf(".")));
            }
        }
        TextFighter.saves = filteredSaves;
        return filteredSaves;
    }

    //Enemy methods
    public static void setPossibleEnemies() {
        ArrayList<Enemy> possible = new ArrayList<Enemy>();
        for(Enemy e : TextFighter.enemies) {
            boolean valid = true;
            if(e.getLevelRequirement() <= TextFighter.player.getLevel()) {
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
        TextFighter.possibleEnemies = possible;
    }
    public static ArrayList<String> getPossibleEnemyOutputs() {
        setPossibleEnemies();
        ArrayList<String> outputs = new ArrayList<String>();
        for(Enemy e : TextFighter.possibleEnemies) {
            outputs.add(e.getOutput());
        }
        return outputs;
    }

    //Moves the player
    public static boolean movePlayer(String location) {
        if(TextFighter.player.getLocation() != null && location.equals(TextFighter.player.getLocation())) { return true; }
        for(Location l : TextFighter.locations) {
            if(l.getName().equals(location)) {
                //Invokes the post methods of the previous location
                TextFighter.player.getLocation().invokePostmethods();
                TextFighter.player.setLocation(location);
                TextFighter.addToOutput("Moved to " + location);
                //Invokes the pre methods of the previous location
                l.invokePremethods();
                return true;
            }
        }
        return false;
    }


    //Calculation methods
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

    //comparison methods
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

}
