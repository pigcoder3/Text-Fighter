package org.textfighter.location;

import org.textfighter.location.ChoiceRequirement;

import java.lang.reflect.*;

import java.util.*;

public class Choice {

    private String name;
    private String description;
    private String usage;
    private String output;

    private ArrayList<ChoiceMethod> methods;

    private ArrayList<ChoiceRequirement> requirements;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public String getOutput() { return output; }
    public ArrayList<ChoiceMethod> getMethods() { return methods; }
    public ArrayList<ChoiceRequirement> getRequirements() { return requirements; }

    public boolean invokeMethods(ArrayList<String> inputArgs) {
        int inputArgsIndex = 0;
        for(ChoiceMethod m : methods) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            int startingIndex = 0;
            if(m.getArguments() != null) {
                methodArgs = m.getArguments();
                startingIndex=m.getArguments().size();
            }
            for(int i=startingIndex; i<m.getArgumentTypes().size(); i++) {
                if(inputArgsIndex < inputArgs.size()) {
                    if(m.getArgumentTypes().get(i).equals(int.class)) {
                        methodArgs.add(Integer.parseInt(inputArgs.get(inputArgsIndex)));
                    } else if(m.getArgumentTypes().get(i).equals(String.class)) {
                        methodArgs.add(inputArgs.get(inputArgsIndex));
                    }
                    inputArgsIndex++;
                } else {
                    if(m.getArgumentTypes().size() != m.getArgumentTypes().size()) {
                        System.out.println(usage);
                        return false;
                    }
                }
            }
            m.setArguments(methodArgs);
        }
        for(ChoiceMethod m : methods) {
            if(!m.invokeMethod()) {
                System.out.println(usage);
                return false;
            }
        }
        return true;
    }

    public Choice(String name, String description, String usage, ArrayList<ChoiceMethod> methods, ArrayList<ChoiceRequirement> requirements) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.methods = methods;
        this.requirements = requirements;
        this.output = "- " + name + "   \t:|:   " + usage + "   \t:|:   " + description;
    }
}
