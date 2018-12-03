package org.textfighter;

import org.textfighter.userinterface.UserInterface;

public class Display {

    public void displayInterface(UserInterface i) {
        i.parseInterface();
        System.out.println(i.getParsedUI());
    }


}
