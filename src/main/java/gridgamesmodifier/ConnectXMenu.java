package gridgamesmodifier;

public class ConnectXMenu extends OptionsMenu {
    int m_chain,m_gravityVar,m_gravityCondition;
    public void ConnectXMenu(){
        m_gravityVar = 0;//unless the user wants gravity that shifts, these variables are unused.
        m_gravityCondition = 0;
    }
    
    @Override
    public void ChooseOptions(){
        int answer = AskQuestion(0,2,"Choose a gamemode! CUSTOM (0), CONNECT-4 (1), TIC-TAC-TOE (2)");
        
        switch (answer) {
            case 1://CONNECT-4                
                m_boardSizeX = 7;
                m_boardSizeY = 6;
                m_gravity = 1;
                m_chain = 4;
                m_playerCount = 2;
                StartGame();
                break;
            case 2://TIC-TAC-TOE
                m_boardSizeX = 3;
                m_boardSizeY = 3;
                m_gravity = 0;
                m_chain = 3;
                m_playerCount = 2;
                StartGame();
                break;
            case 0://Time to set up a custom map!                
                m_playerCount = AskQuestion(2,8,"How many players? [2-8]");
                m_boardSizeX = AskQuestion(1,30,"How many spaces across should the board be? [1-30]");
                m_boardSizeY = AskQuestion(1,30,"How many spaces tall should the board be? [1-30]");
                int chainMax;
                if (m_boardSizeX < m_boardSizeY){chainMax = m_boardSizeY;}
                else if (m_boardSizeX > m_boardSizeY){chainMax = m_boardSizeX;}
                else{chainMax = m_boardSizeX;}
                m_chain = AskQuestion(1,m_boardSizeX*m_boardSizeY,"How big of a chain is required to win? [1-"+chainMax+"]");
                answer = AskQuestion(0,1,"Is gravity enabled? [0: disabled, 1: enabled]");
                switch (answer) {
                    case 0://DISABLED                        
                        m_gravity = 0;
                        break;
                    case 1://ENABLED
                        m_gravity = (AskQuestion(0,2,"How should gravity work? [0: unchanging, 1: swap UP-AND-DOWN, 2: cycle ALL 4 DIRECTIONS"))+1; 
                        //gravity is set to something between 1 and 3. m_gravity=2 and m_gravity=3 require further questions.
                        if (m_gravity > 1){
                            m_gravityCondition = AskQuestion(0,2,"What changes gravity? [0: X turns pass, 1: Player connects X+ markers, 2: Player connects X (exact) markers]");
                            int maxX = 99;
                            if (m_gravityCondition > 0){ //if gravity changes based on markers connected, the maximum X value must be the maximum number of chained markers - 1, or it will never occur.
                                maxX = m_chain-1;
                            }
                            m_gravityVar = AskQuestion(0,maxX,"X=? [1-"+maxX+"]");
                        }
                        break;
                }
                StartGame();
                break;
        }
    }
    
    public void StartGame(){
        ConnectXGameBoard gameboard = new ConnectXGameBoard(m_boardSizeX,m_boardSizeY,m_playerCount,m_chain,m_gravity,m_gravityCondition,m_gravityVar);
    }
}