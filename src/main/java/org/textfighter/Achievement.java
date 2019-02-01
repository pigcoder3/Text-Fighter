package org.textfighter;

import java.lang.reflect.*;
import java.util.ArrayList;
import org.textfighter.method.*;

import org.textfighter.display.Display;

@SuppressWarnings("unchecked")

public class Achievement {

    private String name;
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();
    private ArrayList<Reward> rewards = new ArrayList<Reward>();

    private boolean valid = true;

    public String getName() { return name; }
    public ArrayList<Requirement> getRequirements() { return requirements; }
    public ArrayList<Reward> getRewards() { return rewards; }

    public boolean getValid() { return valid; }

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

    public Achievement(String name, ArrayList<Requirement> requirements, ArrayList<Reward> rewards) {
        this.name = name;
        this.requirements = requirements;
        this.rewards = rewards;
    }
}
