package org.textfighter;

import org.textfighter.location.Location;

public class Display {

    public static void displayInterface(Location i) {
        i.filterPossibleChoices();
        i.parseInterface();
        System.out.println(i.getParsedUI());
    }

}
