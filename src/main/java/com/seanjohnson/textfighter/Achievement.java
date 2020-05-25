package com.seanjohnson.textfighter;

import java.lang.reflect.*;
import java.util.ArrayList;
import com.seanjohnson.textfighter.method.*;

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
     * Stores all rewards (methods that are invoked when the achievement is earned) of this achievement that meet their own requirements.
     * <p>Set to an empty ArrayList of Rewards.</p>
     */
    private ArrayList<Reward> possibleRewards = new ArrayList<Reward>();

    //reward methods
    /**
     * Invokes all the {@link #possibleRewards}.
     * <p>First calls {@link #filterRewardMethods}, then invokes them.</p>
     */
    public void invokeRewardMethods() {
        filterRewardMethods();
        ArrayList<String> rewardStrings = new ArrayList<String>();
        //Give the player the rewards
        if(possibleRewards != null) {
            for(Reward r : possibleRewards) {
                String output = r.invokeMethod();
                if(output != null) { rewardStrings.add(output); }
            }
        }
        //Print out all of the rewards the player recieved
        if(rewardStrings.size() > 0) {
            TextFighter.addToOutput("Rewards:");
            for(int i=0; i<rewardStrings.size(); i++) {
                String s = rewardStrings.get(i);
                if(i != rewardStrings.size()-1) {
                    s+=","; //If this is the last reward do not put a comma
                }
                TextFighter.addToOutput(s);
            }
        }
    }
    /**
     * Loops over all rewardMethods and adds all that meet their own requirements to {@link #rewards}.
     * <p>Adds valid rewardMethods to a new ArrayList that the {@link #possibleRewards} is set to after.</p>
     */
    public void filterRewardMethods() {
        //Filter out any rewards that do not meet the requirements
        possibleRewards.clear();
        if(rewards != null){
            for(Reward r : rewards) {
                boolean valid = true;
                if(r.getRequirements() != null) {
                    System.out.println(r.getRequirements().getClass());
                    for(Requirement rq : r.getRequirements()) {
                        if(!rq.invokeMethod()) {
                            valid = false;
                            break;
                        }
                    }
                    if(valid) {
                        possibleRewards.add(r);
                    }
                }
                else {
                    possibleRewards.add(r);
                }
            }
        }
    }

    public Achievement(String name, String description, ArrayList<Requirement> requirements, ArrayList<Reward> rewards) {
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.rewards = rewards;
    }
}
