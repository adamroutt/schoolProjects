package gridgamesmodifier;

import java.util.*;

//created by Adam Routt
public class GridGamesModifier {
    public static void main(String[] args) {
        System.out.println("Welcome to the Grid Games Modifier! You can play games normally or experiment with changing their rules!");
        MainMenu optionsMenu = new MainMenu();
        optionsMenu.ChooseOptions();
    }
}
