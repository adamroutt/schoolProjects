package gridgamesmodifier;

import java.util.*;

public class MineSweeperGameBoard extends GameBoard{
    int m_mineCount,m_progress,m_flags;
    boolean m_success;//keeps track of whether or not you win or lose.
    
    MineSweeperGameBoard(int x, int y, int mines){
        m_boardSizeX = x;
        m_boardSizeY = y;
        m_mineCount = mines;
        m_flags = m_mineCount;
        m_progress = 0; //number of spaces found
        PlayGame();
    }

    @Override
    public void CreateBoard(){
        m_nodeBoard = new ArrayList<>(m_boardSizeX*m_boardSizeY);
        
        //create a list of 0s and 9s to represent where the safe(0) spots and mines(9) are.
        String mineTable = "0";
        mineTable = mineTable.repeat(m_boardSizeX*m_boardSizeY);
        int minesPlaced = 0;
        while (minesPlaced < m_mineCount){
            int randomValue = (int) ((Math.random() * (m_boardSizeX*m_boardSizeY)));
            if (Character.getNumericValue(mineTable.charAt(randomValue)) != 9){
                //there's not already a mine here. replace the 0 with a 9.
                mineTable = mineTable.substring(0, randomValue) + "9" + mineTable.substring(randomValue + 1);
                minesPlaced++;
            }
        }
        
        //place all of the nodes.
        for (int i = 0; i < m_boardSizeX*m_boardSizeY; i++){
            m_nodeBoard.add(new Node(Character.getNumericValue(mineTable.charAt(i))));
            //add a node with a value pulled from the mine table of 0s and 9s.
        }
        ConnectNodes();
        DetermineMines();
    }
    
    public void DetermineMines(){
        for (Node node : m_nodeBoard){
            node.DetermineMineAdjacency();
        }
    }
    
    @Override
    public void DoGameRules(){
        m_returned = ChooseNode(true, true);
        //ask whether placing flag or discovering spot
        System.out.println("Toggle flag on this node [F]? or Discover it [ENTER]? "+m_flags+" flags left.");
        String answer = m_input.nextLine();
        if ("F".equals(answer)){//player wants to toggle the flag on this node!
            if ((m_nodeBoard.get(m_returned)).m_flagged){m_flags++;}
            else{m_flags--;}
            m_returned = (m_nodeBoard.get(m_returned)).SelectNode(true);
        }
        else{//player wants to discover this node!
            if ((m_nodeBoard.get(m_returned)).m_flagged){
                m_flags--;
                (m_nodeBoard.get(m_returned)).m_flagged = false;
            }
            m_returned = (m_nodeBoard.get(m_returned)).SelectNode(false);
        }
        
        if (m_returned==9){//you hit a mine..
            m_success = false;
            EndGame();
            return;
        }
        else{
            //count the number of discovered nodes.
            m_progress = 0;
            for (Node i : m_nodeBoard){
                if (i.m_shown){m_progress++;}
            }
            if (m_progress == (m_boardSizeX*m_boardSizeY)-m_mineCount){//all safe nodes have been found!
                m_success = true;
                EndGame();
            }
        }
    }
    
    @Override
    public void EndGame(){
        gameRunning = false;
        String filler = "*";
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println("GAME OVER!");
        System.out.println();
        if (m_success){System.out.println("YOU WIN!");}
        else{System.out.println("YOU LOST!");}
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println(filler.repeat(m_boardSizeX*2));
        System.out.println(filler.repeat(m_boardSizeX*2));
        //reveal all nodes
        for (Node i : m_nodeBoard){
                i.m_flagged = false;
                i.m_shown = true;
            }
    }
}
