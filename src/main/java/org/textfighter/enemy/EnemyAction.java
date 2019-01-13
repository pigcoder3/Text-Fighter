package org.textfighter.enemy;

import org.textfighter.enemy.*;
import org.textfighter.method.*;

import java.util.ArrayList;

public class EnemyAction {

    private ArrayList<EnemyActionMethod> methods = new ArrayList<EnemyActionMethod>();
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    private boolean valid = true;

    public ArrayList<EnemyActionMethod> getMethods() { return methods; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

    public boolean getValid() { return valid; }

    public void invokeMethods() {
        for(int i=0; i<methods.size(); i++) {
            if(!methods.get(i).getValid()) {
                methods.remove(i);
                continue;
            }
            methods.get(i).invokeMethod();
        }
    }

    public EnemyAction(ArrayList<EnemyActionMethod> methods, ArrayList<Requirement> requirement) {
        this.requirements = requirements;
        if(methods == null) { valid = false; }
        this.methods = methods;
    }
}
