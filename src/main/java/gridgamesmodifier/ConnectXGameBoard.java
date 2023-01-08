package gridgamesmodifier;

import java.util.*;

public class ConnectXGameBoard extends GameBoard{
    int m_winningChain,m_gravCondition,m_gravityVar;
    boolean m_winners[]; //array that keeps track of how many players win on this turn (in case of a tie)
    
    ConnectXGameBoard(int x, int y, int players, int chain, int gravity, int gravCondition, int gravVar){
        m_boardSizeX = x;
        m_boardSizeY = y;
        m_playerCount = players;
        m_gravity = gravity; //0: NO GRAVITY, 1: GRAVITY, 2: UP-AND-DOWN switching gravity, 3: ALL 4 DIRECTIONS switching gravity
        m_winningChain = chain;
        m_gravCondition = gravCondition; //0: X turns pass, 1: X+ connected pieces, 2: X connected pieces
        m_gravityVar = gravVar;
        
        m_winners = new boolean[m_playerCount];
        for (boolean bool : m_winners){ //initialize the array
            bool = false;
        }
        
        m_gravityDir = 0; //0: DOWN, 1: LEFT, 2: UP; 3: RIGHT
        PlayGame();
    }
    
    @Override
    public void CreateBoard(){
        m_nodeBoard = new ArrayList<>(m_boardSizeX*m_boardSizeY);
        
        //place all of the nodes.
        for (int i = 0; i < m_boardSizeX*m_boardSizeY; i++){
            m_nodeBoard.add(new Node());
            //add a node with a value pulled from the mine table of 0s and 9s.
        }
        ConnectNodes();
    }
    
    @Override
    public void DoGameRules(){
        System.out.println("Player "+m_player+"'s turn!");
        if (m_gravity>0){
            if (m_gravityDir%2 == 0){//UP or DOWN
                m_returned = ChooseNode(true,false);
            }
            else{//LEFT or RIGHT
                m_returned = ChooseNode(false,true);
            }
        }
        else{//NO GRAVITY, choose X AND Y
            m_returned = ChooseNode(true,true);
        }
        
        //let the node know it's been selected, cause it to fall (if gravity is on), then determine if it's going to cause anything.
        (m_nodeBoard.get(m_returned)).SelectNode(m_player);
        Node tempNode = (m_nodeBoard.get(m_returned));
        if (m_gravity>0){
            tempNode = tempNode.Gravity(m_gravityDir);
        }
        int returnValue = tempNode.DetermineOutcome(m_winningChain,m_gravityVar,m_gravCondition);
        boolean gravityAffected = false;
        if (returnValue>10){ //this value is returned if this move wins the game for the player.
            m_winners[(returnValue-1)%10] = true;//we subtract one because of arrays indexing at 0. player 1 is element 0, player 2 is element 1, etc.
            EndGame(); //this will return the value of the player that won.
            return;
        }
        if (returnValue == 1){ //this value is returned if the player satisfies a condition for gravity to switch.
            gravityAffected = true;
        }
        if (m_gravity>1){//gravity is allowed to shift
            if (m_turn%m_gravityVar==0 && m_gravCondition==0){//the amount of turns required to switch gravity have passed, if you turned this option on.
                gravityAffected = true;
            }
        }
        
        //if gravity has been affected, switch to the next direction!
        if (gravityAffected){
            String filler = "***";
            String directionalFillers[] = {"vvv","<<<","^^^",">>>"};
            switch (m_gravity){
                case 2: //UP AND DOWN
                    DisplayBoard();
                    System.out.println(filler.repeat(m_boardSizeX+1));
                    m_gravityDir = 2-m_gravityDir;
                    filler = directionalFillers[m_gravityDir];
                    System.out.println(filler.repeat(m_boardSizeX+1));
                    System.out.println("GRAVITY IS SWITCHING!");
                    System.out.println(filler.repeat(m_boardSizeX+1));
                    break;
                case 3: //ALL 4 DIRECTIONS
                    DisplayBoard();
                    System.out.println(filler.repeat(m_boardSizeX+1)); 
                    if (m_gravityDir < 3){
                        m_gravityDir++;
                    }
                    else{
                        m_gravityDir = 0;
                    }
                    filler = directionalFillers[m_gravityDir];
                    System.out.println(filler.repeat(m_boardSizeX+1));
                    System.out.println("GRAVITY IS SWITCHING!");
                    System.out.println(filler.repeat(m_boardSizeX+1));
                    break;
            }
        }
        if (m_gravity>0){CauseGravity(m_gravityDir);}
        
        //next player's turn!
        if (m_player < m_playerCount){
            m_player++;
        }
        else{
            m_player = 1;
        }
    }
    
    //shift pieces on the board in the direction gravity pulls them!
    //0: DOWN, 1: LEFT, 2: UP, 3: RIGHT
    public void CauseGravity(int direction){
        //inflict gravity on each node, one at a time. the order depends on the direction, as you have to move the nodes at the "bottom of the pile" first.
        switch (direction){
            case 0: //vvvDOWNvvv
                for (int i = m_boardSizeY-1; i >= 0; i--){//columns
                    for (int j = 0; j < m_boardSizeX; j++){//rows
                        (m_nodeBoard.get((m_boardSizeX*i)+j)).Gravity(direction);
                    }
                }
                break;
            case 1: //<<<LEFT<<<
                for (int j = 0; j < m_boardSizeX; j++){//rows
                    for (int i = 0; i < m_boardSizeY; i++){//columns
                        (m_nodeBoard.get((m_boardSizeX*i)+j)).Gravity(direction);
                    }
                }
                break;
            case 2: //^^^UP^^^
                for (int i = 0; i < m_boardSizeY; i++){//columns
                    for (int j = 0; j < m_boardSizeX; j++){//rows
                        (m_nodeBoard.get((m_boardSizeX*i)+j)).Gravity(direction);
                    }
                }
                break;
            case 3: //>>>RIGHT>>>
                for (int j = m_boardSizeX-1; j >= 0; j--){//rows
                    for (int i = 0; i < m_boardSizeY; i++){//columns
                        (m_nodeBoard.get((m_boardSizeX*i)+j)).Gravity(direction);
                    }
                }
                break;
        }
        
        int temp;
        boolean winFlag = false;
        //after everything has moved, determine outcomes for every piece.
        for (int i = 0; i < m_boardSizeY; i++){//columns
            for (int j = 0; j < m_boardSizeX; j++){//rows
                temp = (m_nodeBoard.get((m_boardSizeX*i)+j)).DetermineOutcome(m_winningChain,0,0); //if these 2 parameters are not zero, the board could potentially chain gravity swaps forever
                if (temp > 10){
                    m_winners[(temp-1)%10] = true;
                    winFlag = true;
                }
            }
        }
        //award the winning players! (if there are any)
        if (winFlag && gameRunning){EndGame();}
    }
    
    @Override
    public void EndGame(){
        gameRunning = false;
        String filler = "***";
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println("GAME OVER!");
        System.out.println();
        System.out.println("RESULTS:");
        System.out.println(filler.repeat(m_boardSizeX+1));
        int i = 1;
        for (boolean bool : m_winners){
            if (bool){
                System.out.println("PLAYER "+i+" WINS!");
            }
            else{
                System.out.println("PLAYER "+i);
            }
            i++;
        }
    }
}
