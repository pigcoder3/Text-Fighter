package org.textfighter;

import org.textfighter.location.Location;

public class Display {

    public void displayInterface(Location i) {
        i.parseInterface();
        System.out.println(i.getParsedUI());
    }


}
