package org.textfighter.item.tool;

import org.textfighter.item.Item;

public class Tool extends Item {

    protected String[] typeStrings = {"normal"};

    public Tool (int level, int experience, int type) {
        super(level, experience, type);
    }

}
