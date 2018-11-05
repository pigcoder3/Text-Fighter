

public class Display {

    public void displayInterface(Interface i) {
        i.parseInterface();
        for(String line : i.parsedUI) {
            System.out.println(line);
        }
    }


}
