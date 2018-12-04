package org.textfighter.userinterface;

import org.textfighter.Player;

import java.lang.reflect.Method;

public class UiTag {

    private String tag;
    private Class methodClass;
    private Method function;

    public UiTag(String tag, String function, String className) {
        this.tag = tag;
        try {
            this.methodClass = Class.forName(className);
            this.function = methodClass.getMethod(function, (Class<?>[]) null);
        } catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
    }
}
