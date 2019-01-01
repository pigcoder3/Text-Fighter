package org.textfighter.enemy;

import org.textfighter.enemy.*;
import org.textfighter.Requirement;

import java.util.ArrayList;

public class EnemyAction {

    private ArrayList<EnemyActionMethod> methods = new ArrayList<EnemyActionMethod>();
    private ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    public ArrayList<EnemyActionMethod> getMethods() { return methods; }
    public ArrayList<Requirement> getRequirements() { return requirements; }

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
        //Filters out invalid methods
        for(int i=0; i<methods.size(); i++) {
            if(!methods.get(i).getValid()) {
                this.methods.add(methods.get(i));
            }
        }
        //Filters out invalid requirements
        for(int i=0; i<requirements.size(); i++) {
            if(!requirements.get(i).getValid()) {
                this.requirements.add(requirements.get(i));
            }
        }
    }
}
