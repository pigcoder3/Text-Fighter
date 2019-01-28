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
        for(ChoiceMethod m : methods) {
            ArrayList<Object> methodArgs = new ArrayList<Object>();
            if(m.getArguments() == null || m.getArguments().size() < 1) { continue; }
            for(int i=0; i<m.getArgumentTypes().size(); i++) {
                if(m.getOriginalArguments().get(i) == null) { methodArgs.add(null); continue;}
                if(!m.getArguments().get(i).equals("%ph%") && !m.getArguments().get(i).getClass().equals(TFMethod.class)) { methodArgs.add(m.getArguments().get(i)); continue; }
                if(m.getArguments().get(i).getClass() == TFMethod.class) { ((TFMethod)m.getArguments().get(i)).putInputInArguments(inputArgs, inputArgsIndex); }
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
                    TextFighter.addToOutput("Usage: " + usage);
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
