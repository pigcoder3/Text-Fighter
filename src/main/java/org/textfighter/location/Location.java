package org.textfighter.location;

import org.textfighter.location.*;
import org.textfighter.*;
import org.textfighter.display.*;
import org.textfighter.method.*;

import java.util.ArrayList;

public class Location {

    /***Stores the name of the location*/
    private String name;

    /**
     * Stores the interfaces of the location.
     * <p>The interfaces are in order of appearance.</p>
     * <p>Set to an empty ArrayList of UserInterfaces.
     */
    private ArrayList<UserInterface> interfaces = new ArrayList<UserInterface>();

    /**
     * Stores all choices of this location.
     * <p>Set to an empty ArrayList of Choices.</p>
     */
    private ArrayList<Choice> allChoices = new ArrayList<Choice>();
    /**
     * Stores all choices of this location that meet their own requirements.
     * <p>Set to an empty ArrayList of Choices.</p>
     */
    private ArrayList<Choice> possibleChoices = new ArrayList<Choice>();

    /**
     * Stores all premethods of this location.
     * <p>premethods are methods that are invoked when the player moves here.</p>
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> allPremethods = new ArrayList<TFMethod>();
    /**
     * Stores all premethods of this location that meet their own requirements.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> possiblePremethods = new ArrayList<TFMethod>();

    /**
     * Stores all postmethods of this location.
     * <p>premethods are methods that are invoked when the player moves away from here.</p>
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> allPostmethods = new ArrayList<TFMethod>();
    /**
     * Stores all postmethods of this location that meet their own requirements.
     * <p>Set to an empty ArrayList of TFMethods.</p>
     */
    private ArrayList<TFMethod> possiblePostmethods = new ArrayList<TFMethod>();

    /**
     * Returns the {@link #name}.
     * @return      {@link #name}
     */
    public String getName() { return name; }

    /**
     * Returns the {@link #interfaces}.
     * @return      {@link #interfaces}
     */
    public ArrayList<UserInterface> getInterfaces() { return interfaces; }

    /**
     * Returns the {@link #allChoices}.
     * @return      {@link #allChoices}
     */
    public ArrayList<Choice> getAllChoices() { return allChoices; }
    /**
     * Returns the {@link #possibleChoices}.
     * @return      {@link #possibleChoices}
     */
    public ArrayList<Choice> getPossibleChoices() { return possibleChoices; }

    /**
     * Returns the {@link #possiblePremethods}.
     * @return      {@link #possiblePremethods}
     */
    public ArrayList<TFMethod> getPremethods() { return possiblePremethods; }
    /**
     * Return the {@link #possiblePostmethods}.
     * @return      {@link #possiblePostmethods}
     */
    public ArrayList<TFMethod> getPostmethods() { return possiblePostmethods; }

    /**
     * Invokes all the {@link #possiblePremethods}.
     * <p>First calls {@link #filterPremethods}, then invokes them.</p>
     */
    public void invokePremethods() {
        filterPremethods();
        for(TFMethod pm : possiblePremethods) {
            pm.invokeMethod();
        }
    }
    /**
     * Loops over all premethods and adds all premethods that meet their own requirements to {@link #allPremethods}.
     * <p>Adds valid premethods to a new ArrayList that the {@link #possiblePremethods} is set to after.</p>
     */
    public void filterPremethods() {
        //Filter through all premethods that meet their requirements
        possiblePremethods.clear();
        if(allPremethods == null) { return; }
        for(TFMethod pm : allPremethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possiblePremethods.add(pm);
            }
        }
    }

    /**
     * Invokes all the {@link #possiblePostmethods}.
     * <p>First calls {@link #filterPostmethods}, then invokes them.</p>
     */
    public void invokePostmethods() {
        filterPostmethods();
        for(TFMethod pm : possiblePostmethods) {
            pm.invokeMethod();
        }
    }
    /**
     * Loops over all postmethods and adds all postmethods that meet their own requirements to {@link #possiblePostmethods}.
     * <p>Adds valid postmethods to a new ArrayList that the {@link #possiblePostmethods} is set to after.</p>
     */
    public void filterPostmethods() {
        //Filter through all postmethod that meet their requirements
        possiblePostmethods.clear();
        if(allPostmethods == null) { return; }
        for(TFMethod pm : allPostmethods) {
            boolean valid = true;
            for(Requirement r : pm.getRequirements()) {
                if(!r.invokeMethod()) {
                    valid = false;
                    break;
                }
            }
            if(valid) {
                possiblePostmethods.add(pm);
            }
        }
    }

    /**
     * Loops over all {@link #allChoices} and adds all choices that meet their own requirements to {@link #possibleChoices}.
     * <p>Adds valid choices to a new ArrayList that the {@link #possibleChoices} is set to after.</p>
     */
    public void filterPossibleChoices() {
        if(allChoices == null) { return; }
        possibleChoices.clear();
        for(Choice c : allChoices) {
			boolean valid = true;
            if(c.getRequirements() != null){
                for(Requirement r : c.getRequirements()) {
                    if(!r.invokeMethod()) {
                        valid = false;
                        break;
                    }
                }
            }
            if(valid) {
                possibleChoices.add(c);
            }
        }
    }

    public Location(String name, ArrayList<UserInterface> interfaces, ArrayList<Choice> choices, ArrayList<TFMethod> premethods, ArrayList<TFMethod> postmethods) {

        //Sets all variables
        this.name = name;
        this.interfaces = interfaces;
        this.allPremethods = premethods;
        this.allPostmethods = postmethods;

        //Filters out any invalid choices
        for(int i=0; i<choices.size(); i++) {
            if(choices.get(i).getValid()) { allChoices.add(choices.get(i)); }
        }
    }
}
