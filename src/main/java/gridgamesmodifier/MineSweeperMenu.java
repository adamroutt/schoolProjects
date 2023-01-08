package gridgamesmodifier;

public class MineSweeperMenu extends OptionsMenu {
    int m_mineCount;
    public void MineSweeperMenu(){
        m_playerCount = 1; //minesweeper is singleplayer.
        m_gravity = 0; //gravity does not affect minesweeper.
    }
    
    @Override
    public void ChooseOptions(){
        int answer = AskQuestion(0,3,"Choose a gamemode! CUSTOM (0), BEGINNER (1), INTERMEDIATE (2), EXPERT(3)");
        switch (answer) {
            case 1://BEGINNER                
                m_boardSizeX = 9;
                m_boardSizeY = 9;
                m_mineCount = 10;
                StartGame();
                break;
            case 2://INTERMEDIATE                
                m_boardSizeX = 16;
                m_boardSizeY = 16;
                m_mineCount = 40;
                StartGame();
                break;
            case 3://EXPERT                
                m_boardSizeX = 30;
                m_boardSizeY = 16;
                m_mineCount = 99;
                StartGame();
                break;
            case 0://Time to set up a custom map!                
                m_boardSizeX = AskQuestion(1,30,"How many spaces across should the board be? [1-30]");
                m_boardSizeY = AskQuestion(1,30,"How many spaces tall should the board be? [1-30]");
                m_mineCount = AskQuestion(1,m_boardSizeX*m_boardSizeY,"How many mines should there be? [1-"+m_boardSizeX*m_boardSizeY+"]");
                StartGame();
                break;
        }
    }
    
    public void StartGame(){
        MineSweeperGameBoard gameboard = new MineSweeperGameBoard(m_boardSizeX,m_boardSizeY,m_mineCount);
    }
}
