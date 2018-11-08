package tfighter;

import tfighter.UserInterface;

public class Display {

    public void displayInterface(UserInterface i) {
        i.parseInterface();
        for(String line : i.parsedUI) {
            System.out.println(line);
        }
    }


}
