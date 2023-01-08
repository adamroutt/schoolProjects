package gridgamesmodifier;

public class MainMenu extends OptionsMenu {
    @Override
    public void ChooseOptions(){
        int answer = AskQuestion(0,2,"type 0 to display INFO, 1 to play MINESWEEPER, or 2 to play CONNECT-X.");
        switch (answer) {
            case 0: //INFO
                System.out.println("*******************************");
                System.out.println("MINESWEEPER:"); 
                System.out.println("    clear the board without selecting any hidden mines to win."); 
                System.out.println("    Spaces will show a number showing how many mines they are directly touching. You may flag where you believe mines to be.");
                System.out.println("CONNECT-X:");
                System.out.println("    Games like Connect-4 or Tic-Tac-Toe have multiple players taking turns placing markers for their team. Chain a certain amount of markers to win.");
                System.out.println("    For Connect-4, gravity is turned on and 4 pieces must line up.");
                System.out.println("    For Tic-Tac-Toe, gravity is turned off and you must connect 3 pieces.");    
                System.out.println("*******************************");
                System.out.println();
                ChooseOptions();
                break;
            case 1: //MINESWEEPER
                MineSweeperMenu minesweeperMenu = new MineSweeperMenu();
                minesweeperMenu.ChooseOptions();
                break;
            case 2: //CONNECT-X
                ConnectXMenu connectXMenu = new ConnectXMenu();
                connectXMenu.ChooseOptions();
                break;
        }
    }
}
