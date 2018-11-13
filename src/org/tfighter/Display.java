package tfighter;

import tfighter.UserInterface;

public class Display {

    public void displayInterface(UserInterface i) {
        i.parseInterface();
        System.out.println(i.parsedUI);
    }


}
