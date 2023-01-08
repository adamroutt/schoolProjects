package gridgamesmodifier;
import java.util.*;

public class Node {
    int m_value;
    boolean m_flagged;
    boolean m_shown;
    List<Node> m_neighbors = Arrays.asList(new Node[9]); //points to adjacent nodes
    
    Node(int mineFlag){//a node is only created with an integer if the gamemode is minesweeper.
        m_value = mineFlag; //this value will either be 0 for safe or 9 for mine.
        m_flagged = false;
        m_shown = false;
    }
    
    Node(){//a node is only created without an integer if the gamemode is connect-x.
        m_value = 0; //this value will represent which player owns this node.
        m_flagged = false;
        m_shown = true;
    }
    
    //for building a board of nodes. This connects a node to one touching it, and is ran 8 times per node.
    public void SetAdjacentNode(Node neighbor, int direction){
        m_neighbors.set(direction, neighbor);
    }
    
    public void DetermineMineAdjacency(){
        if (m_value < 9){//if this node is a mine, you don't need to find how many nearby nodes are mines!
            for (Node i : m_neighbors){
                if (i != null){
                    if (i.m_value == 9){
                        m_value++; //let m_value reflect the number of adjacent mines.
                    }
                }
            }
        }
    }
    
    //minesweeper node. Returns the value of this node (only important if it's a 9).
    //this method will only be called if it's determined that this node has not yet been shown.
    public int SelectNode(boolean flag){
        if (flag){
            m_flagged = !m_flagged;
            return 0;
        }
        else{
            if (m_value!=9){//this node is safe!
                DiscoverNeighbors();//cascade!
            }
            m_shown = true;
            return m_value;
        }  
    }
    
    //for cascading in minesweeper.
    //if you find a node (either connected or the node itself) with 0 connected mines, cascade!
    public void DiscoverNeighbors(){
        for (Node i : m_neighbors){
            if (i != null){
                if (i.m_value==0 && !i.m_shown){
                    i.m_shown = true;
                    m_shown = true;
                    i.DiscoverNeighbors();//cascade if there's a connection to an open field!
                }
                else if (m_value==0){
                    i.m_shown = true;
                }
            }
        }
    }
    
    //polymorphism: this selectnode() only occurs in connect-x games. Lets this node know its new owner.
    public void SelectNode(int player){
        m_value = player;
    }
    
    //in connect-x, DetermineOutcome() returns a value important for determining if the player has caused a win or change in gravity.
    //      0: nothing
    //      1: gravity switch
    //      10+player: win condition met
    //pass in an int specifying the player claiming the node.
    //pass in an int specifying the size of chain required to win the game.
    //pass an int specifying the chain size that affects gravity (input 0 if gravity does not change in this game).
    //pass an int specifying whether chain size must be AT LEAST(1) or EXACT(2) when determining whether to affect gravity (again, 0 if gravity is not meant to change).
    public int DetermineOutcome(int winningChain, int gravityX, int gravityCondition){
        int returnValue;
        int directions[] = new int[9]; //this array keeps track of how far the chain goes in each direction.
        
        //the arraylist m_neighbors represents adjacent nodes. 0 is top left, 1 is top, 2 is top right, 3 is left, etc.
        //when the numbers add to 8, it means the nodes are in the same path. (ex: top left and bottom right are 0 and 8)
        //NOTE: 4 is the space that the node actually uses, and is irrelevent. It does however make the code easier when included.
        //the following code finds the chain lengths in each of the 8 directions, then adds the parallel paths together.
        int i = 0;
        boolean keepGoing;
        Node testingNode;
        for (Node node : m_neighbors){
            keepGoing = true;
            testingNode = node;
            while (keepGoing){
                if (node != m_neighbors.get(4)){//this node is you!!! you don't need to check on you!!
                    if (testingNode != null){//ensure you haven't hit the edge of the board.
                        if (m_value == testingNode.m_value){//there's another matching node on the chain!
                            directions[i]++;
                            testingNode = testingNode.m_neighbors.get(i); //move in the same direction across the board to even farther adjacent nodes.
                        }
                        else{keepGoing = false;}
                    }
                    else{keepGoing = false;}
                }
                else{keepGoing = false;}
            }
            i++;
        }
        //now we have the chain size in every direction, but need to add parallel directions together.
        //for each direction, we must also test if the chain meets either the win or gravity condition.
        for (int j = 0; j < 4; j++){
            returnValue = directions[j]+directions[8-j]+1;
            if (returnValue >= winningChain){return 10+m_value;} //you connected enough to win!
            else if (returnValue>=gravityX && gravityCondition==1){return 1;} //you connected at least enough pieces to switch gravity!
            else if (returnValue==gravityX && gravityCondition==2){return 1;} //you connected the exact amount of pieces needed to switch gravity!
        }
        return 0;//if this is the return value being sent back, the player has not won or affected gravity.
    }
    
    //returns the new node at a possibly new position
    public Node Gravity(int direction){
        //convert the direction into the associated node neighbor. 0->7, 1->3, 2->1, 3->5
        int converter[] = {7,3,1,5};
        int neighbor = converter[direction];
        
        //see how many spaces you can move in this direction!
        boolean keepGoing;
        Node endPoint = m_neighbors.get(4);//default value is 4, which means staying in the same place.
        if(m_value != 0){
            Node testingNode = m_neighbors.get(neighbor);
            keepGoing = true;
            while (keepGoing){
                if (testingNode != null){//ensure you haven't hit the edge of the board.
                    if (testingNode.m_value == 0){//there's space to move!
                        endPoint = testingNode;
                        testingNode = testingNode.m_neighbors.get(neighbor); //move in the same direction across the board to even farther adjacent nodes.
                    }
                    else{keepGoing = false;}
                }
                else{keepGoing = false;}
            }
        }
        
        //remove this node's value and move it to the ending point.
        int tempValue = m_value;
        m_value = 0;
        endPoint.m_value = tempValue;
        return endPoint;
    }
}
