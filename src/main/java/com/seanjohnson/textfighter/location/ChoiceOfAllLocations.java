package com.seanjohnson.textfighter.location;

import com.seanjohnson.textfighter.location.Choice;

import java.util.ArrayList;

public class ChoiceOfAllLocations {

    /***Stores the locations that are excluded from this*/
    private ArrayList<String> excludes = new ArrayList<String>();

    /***Stores the choice*/
    private Choice choice;

    /**
     * Returns the {@link #excludes}
     * @return  {@link #excludes}
     */
    public ArrayList<String> getExcludedLocations() { return excludes; }

    /**
     * Returns the {@link #choice}
     * @return  {@link #choice}
     */
    public Choice getChoice() { return choice; }

    public ChoiceOfAllLocations(ArrayList<String> excludes, Choice choice) {
        this.excludes = excludes;
        this.choice = choice;
    }

}
