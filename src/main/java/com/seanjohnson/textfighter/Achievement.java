package com.seanjohnson.textfighter;

import java.lang.reflect.*;
import java.util.ArrayList;
import com.seanjohnson.textfighter.method.*;

import com.seanjohnson.textfighter.display.Display;

@SuppressWarnings("unchecked")

public class Achievement {

    /*** Stores the name of the achivement.*/
    private String name;
    /*** Stores the description of the achievement. Generally stores how to obtain it.*/
    private String description;
    /*** Stores the requirements for the player to earn the achivement.*/
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    /*** Stores the rewards earned from the achievement*/
    private ArrayList<Reward> rewards = new ArrayList<Reward>();

    /**
     * Gets the {@link #name} of the achievement.
     * @return      {@link #name}
     */
    public String getName() { return name; }
    /**
     * Gets the {@link #description} of the achievement.
     * @return      {@link #description}
     */
     public String getDescription() { return description; }
    /**
     * Gets the {@link #requirements}.
     * @return      {@link #requirements}
     */
    public ArrayList<Requirement> getRequirements() { return requirements; }
    /**
     * Gets the {@link #rewards}.
     * @return      {@link #rewards}
     */
    public ArrayList<Reward> getRewards() { return rewards; }

    /**
     * Loops through each of the {@link #rewards} and invokes them, then outputs their rewardstrings.
     * @return      All of the rewards.
     */
    public String invokeRewards() {
        String rewardsString = "";
        for(Reward r : rewards) {
            rewardsString += r.invokeMethod() + "\n";
        }
        if(rewardsString != null) {
            return "Rewards: \n" + rewardsString;
        } else {
            return "";
        }
    }

    public Achievement(String name, String description, ArrayList<Requirement> requirements, ArrayList<Reward> rewards) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.rewards = rewards;
    }
}
