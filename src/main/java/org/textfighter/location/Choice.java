package org.textfighter.location;

import org.textfighter.method.*;

import org.textfighter.TextFighter;

import java.lang.reflect.*;

import java.util.*;

public class Choice {

    private String name;
    private String description;
    private String usage;
    private String output;

    private boolean valid = true;

    private ArrayList<ChoiceMethod> methods = new ArrayList<ChoiceMethod>();
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public String getOutput() { return output; }
    public void setOutput(String o) { output = o;}
    public boolean getValid() { return valid; }
    public ArrayList<ChoiceMethod> getMethods() { return methods; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean invokeMethods(ArrayList<String> inputArgs) {
        int inputArgsIndex = 0;
        for(ChoiceMethod cm : methods) {
            //Put the input in the arguments of all the ChoiceMethod
            inputArgsIndex = cm.putInputInArguments(inputArgs, inputArgsIndex);
            if(inputArgsIndex == -1) {
                for(ChoiceMethod m : methods) {
                    m.resetArguments();
                }
                TextFighter.addToOutput("Problem with parsing input.\nUsage: " + usage);
                return false;
            }
        }
        for(ChoiceMethod m : methods) {
            boolean valid = true;
            if(m.getRequirements() != null) {
                for(Requirement r : m.getRequirements()) {
                    if(!r.invokeMethod()) {
                        valid = false;
                        break;
                    }
                }
            }
            if(valid) {
                if(!m.invokeMethod()) {
                    TextFighter.addToOutput("Problem with invoking method with given arguments.\nUsage: " + usage);
                    return false;
                }
            }
        }
        return true;
    }

    public Choice(String name, String description, String usage, ArrayList<ChoiceMethod> methods, ArrayList<Requirement> requirements) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        if(methods == null) { valid = false; }
        this.methods = methods;
        this.requirements = requirements;
        this.output = name + "\n" +
                        "\tdesc  - " + description + "\n" +
                        "\tusage - " + usage;
    }
}
