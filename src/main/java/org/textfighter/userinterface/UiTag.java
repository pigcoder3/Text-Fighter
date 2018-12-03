package org.textfighter.userinterface;

import org.textfighter.Player;

import java.lang.reflect.Method;

public class UiTag {

    private String tag;
    private Method function;

    public UiTag(String tag, String function) {
        this.tag = tag;
        try {
            this.function = Player.class.getMethod(function, (Class<?>[]) null);
        } catch (NoSuchMethodException e) { e.printStackTrace(); }
    }
}
