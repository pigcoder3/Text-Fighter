package org.textfighter.enemy;

import org.textfighter.enemy.*;
import org.textfighter.method.*;

import java.util.ArrayList;

public class EnemyAction {

    /**
     * Stores all the methods of this enemy action.
     * <p>Set to an empty ArrayList of EnemyActionMethods.</p>
     */
    private ArrayList<EnemyActionMethod> methods = new ArrayList<EnemyActionMethod>();
    /**
     * Stores all the requirements for this action to be performed.
     * <p>Set to an empty ArrayList of Requirements.</p>
     */
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    /**
     * Returns the {@link #methods}.
     * @return      {@link #methods}
     */
    public ArrayList<EnemyActionMethod> getMethods() { return methods; }
    /**
     * Returns the {@link #requirements}.
     * @return      {@link #requirements}
     */
    public ArrayList<Requirement> getRequirements() { return requirements; }

    /*** Invokes all the methods.*/
    public void invokeMethods() {
        //Invoke all of the methods of the enemy action
        for(int i=0; i<methods.size(); i++) {
            methods.get(i).invokeMethod();
        }
    }

    public EnemyAction(ArrayList<EnemyActionMethod> methods, ArrayList<Requirement> requirement) {
        this.requirements = requirements;
        this.methods = methods;
    }
}
