package gridgamesmodifier;

import java.util.*;

public abstract class GameBoard {
    Scanner m_input = new Scanner(System.in);
    ArrayList<Node> m_nodeBoard;
    static String boardValuesY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$";
    //this string has characters for 1-30, so board locations can be represented.
    //ex: #2 is 29 spaces down, 2 across.
    
    int m_returned,m_boardSizeX,m_boardSizeY,m_playerCount,m_gravity,m_player,m_turn,m_gravityDir;
    boolean gameRunning; //is false when the game ends
    
    public void PlayGame(){
        m_turn = 0;
        gameRunning = true;
        CreateBoard();
        DisplayBoard();
        m_player = 1;
        while (gameRunning){
            m_turn++;
            System.out.println("TURN #"+m_turn);
            System.out.println();
            DoGameRules();
            DisplayBoard();
        }
    }
    
    public abstract void EndGame();
    public abstract void CreateBoard();
    public abstract void DoGameRules();
    
    //displays the board.
    public void DisplayBoard(){
        String filler = "***";
        System.out.println(filler.repeat(m_boardSizeX+1));
        String board = "";
        String row = "   ";
        for (Node node : m_nodeBoard){
            if (node.m_flagged){//flagged node
                board += "F";
            }
            else if (!node.m_shown){//undiscovered node{
                board += "?";
            }
            else if (node.m_value==9){//minesweeper mine
                board += "X";
            }
            else if (node.m_value != 0){//normal non-zero node
                board += node.m_value;
            }
            else{//zero node
                board += " ";
            }
        }
        for (int i = 0; i < m_boardSizeX; i++){//print location indicators on the top of the board
            row += String.format("%3s", String.valueOf(i+1)); //prints numbers of diff. sizes equidistant
        }
        filler = " --";
        System.out.println(row);
        System.out.println("   "+filler.repeat(m_boardSizeX));
        
        //display the table
        for (int i = 0; i < m_boardSizeY; i++){//columns
            row = String.valueOf(boardValuesY.charAt(i))+" |"; //location indicators on left side of the table
            for (int j = 0; j < m_boardSizeX; j++){//rows
                row += String.format("%3s", board.charAt(j+(i*m_boardSizeX)));
            }
            row += "|";
            System.out.println(row); 
        }
        System.out.println("   "+filler.repeat(m_boardSizeX));
    }
    
    public void ConnectNodes(){
        int index = 0;
        int testValue;
        for (Node node : m_nodeBoard){            
            for (int i = 0; i < 9; i++){
                testValue = index+(((i/3)-1)*m_boardSizeX)+((i%3)-1); 
                //grabs the index of the adjacent nodes.
                //this might be outside the bounds of the arraylist, which means the main node is on the edge of the board.
                
                try{
                    node.SetAdjacentNode(m_nodeBoard.get(testValue),i);
                }
                catch (IndexOutOfBoundsException err){
                    node.SetAdjacentNode(null,i); //adjacent nodes are set to NULL if it's a boundary of the gameboard.
                }              
            }
            index++;
        }
        //the nodes on the left and right sides do not point to the edges correctly, as it doesn't realize it's supposed to be the edge of the board.
        //This code simply goes back in and manually sets those incorrect neighbors to null.
        for (int i = 0; i<m_boardSizeY; i++){
            for (int j = 0; j<3; j++){
                (m_nodeBoard.get(i*m_boardSizeX)).SetAdjacentNode(null,j*3);
                (m_nodeBoard.get(((i+1)*m_boardSizeX)-1)).SetAdjacentNode(null,(j*3)+2);
            }
        }
    }
    
    //input: Whose turn is it?
    //output: What node is chosen?
    public int ChooseNode(boolean Xneeded, boolean Yneeded){ //AT LEAST one boolean will always be true.
        int testX = 1;
        int testY = 0;
        int chosenNode = 0;
        
        //determine the player's options.
        System.out.print("Choose a point on the grid! [");
        if (Xneeded&&Yneeded){
            System.out.println("A1-"+boardValuesY.charAt(m_boardSizeY-1)+m_boardSizeX+"]");
        }
        else if (Yneeded){
            System.out.println("A-"+boardValuesY.charAt(m_boardSizeY-1)+"]");
        }
        else{ //Xneeded = true
            System.out.println("1-"+m_boardSizeX+"]");
        }
        
        String answer;
        //do not return a node index until you have an acceptable answer.
        boolean m_validity = false;
        while (!m_validity){
            answer = m_input.nextLine();
            
            try{
                if (Xneeded){
                    try{
                        if (Yneeded){
                            if (answer.length()<2){
                                throw new UserOptionException("Answer not long enough!");
                            }
                            else{
                                testX = Integer.parseInt(answer.substring(1));
                            }
                        }
                        else{
                            testX = Integer.parseInt(answer);
                        }                        
                    }
                    catch (NumberFormatException err){
                        throw new UserOptionException("Answer should be a number!"); //throw an error if the answer is not an integer.
                    }
                    if (testX>m_boardSizeX || testX<1){
                        throw new UserOptionException("Answer is out of bounds!"); //if the answer is an integer, throw an error if it's out of bounds.
                    }
                    m_validity = true; //after this point, we set m_validity to false when we catch an error, just to be safe.
                }
                if (Yneeded){
                    testY = boardValuesY.indexOf(answer.charAt(0));
                    if (testY<0 || testY>m_boardSizeY){ //indexOf returns -1 if the letter typed is not found in our list.
                        m_validity = false;
                        throw new UserOptionException("Answer is out of bounds!");
                    }
                    m_validity = true;
                }
                //convert the answer into a useable node index.
                //keep in mind, A1 is not X=1,Y=1, it's X=1,Y=0.
                //this will depend on gravity, as it can change which side you drop a chip in from during connect-x.
                if (m_gravity>0){//gravity is on
                    if (m_gravityDir==1){//gravity is facing left. drop chips in from the right side instead.
                        chosenNode = (m_boardSizeX*(testY+1))-1;
                    }
                    else if (m_gravityDir==2){//gravity is facing up. drop chips in from the bottom side instead.
                        chosenNode = ((m_boardSizeY-1)*m_boardSizeX)-1+testX;
                    }
                    else{chosenNode = (m_boardSizeX*(testY))+(testX-1);}//gravity is facing either down or right.
                }
                else{//gravity is not a factor.
                    chosenNode = (m_boardSizeX*(testY))+(testX-1);
                }
                
                if (m_nodeBoard.get(chosenNode).m_value!=0 && m_playerCount>1){ //trying to claim an already claimed node in connect-x!
                    m_validity = false;
                    throw new UserOptionException("This spot is already claimed!");
                }
                m_validity = true;
            }
            catch (UserOptionException err){
                m_validity = false;
                System.out.println(err.getMessage());
            }
        }
        //you now have a useable answer!
        return chosenNode;
    }
}
