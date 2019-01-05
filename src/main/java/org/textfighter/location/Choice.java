package org.textfighter.location;

import org.textfighter.Requirement;

import org.textfighter.TextFighter;

import java.lang.reflect.*;

import java.util.*;

public class Choice {

    private String name;
    private String description;
    private String usage;
    private String output;

    private ArrayList<ChoiceMethod> methods = new ArrayList<ChoiceMethod>();
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUsage() { return usage; }
    public String getOutput() { return output; }
    public void setOutput(String o) { output = o;}
    public ArrayList<ChoiceMethod> getMethods() { return methods; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

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
                if(inputArgsIndex <= inputArgs.size() - 1) {
                    if(m.getArgumentTypes().get(i).equals(int.class)) {
                        methodArgs.add(Integer.parseInt(inputArgs.get(inputArgsIndex)));
                    } else if(m.getArgumentTypes().get(i).equals(String.class)) {
                        methodArgs.add(inputArgs.get(inputArgsIndex));
                    } else if(m.getArgumentTypes().get(i).equals(Boolean.class)) {
                        methodArgs.add(Boolean.parseBoolean(inputArgs.get(inputArgsIndex)));
                    }
                    inputArgsIndex++;
                } else {
                    if(m.getArgumentTypes().size() != m.getMethod().getParameterTypes().length) {
                        TextFighter.addToOutput("Usage: " + usage);
                        return false;
                    }
                }
            }
            m.setArguments(methodArgs);
        }
        for(ChoiceMethod m : methods) {
            if(!m.invokeMethod()) {
                TextFighter.addToOutput("Usage: " + usage);
                return false;
            }
        }
        return true;
    }

    public Choice(String name, String description, String usage, ArrayList<ChoiceMethod> methods, ArrayList<Requirement> requirements) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        //Filters out invalid methods
        for(int i=0; i<methods.size(); i++) {
            if(!methods.get(i).getValid()) {
                this.methods.add(methods.get(i));
            }
        }
        //Filters out invalid requirements
        if(requirements != null) {
            for(int i=0; i<requirements.size(); i++) {
                if(!requirements.get(i).getValid()) {
                    this.requirements.add(requirements.get(i));
                }
            }
        } else {
            requirements = new ArrayList<Requirement>();
        }
        this.output = "- " + name + " \t:|: " + usage + " \t:|: " + description;
    }
}
