package gridgamesmodifier;
import java.util.*;

public abstract class OptionsMenu {
    Scanner m_input = new Scanner(System.in);
    int m_boardSizeX,m_boardSizeY,m_playerCount,m_gravity;
    
    //checks that a choice is valid and throws a custom error if it isn't.
    public boolean CheckValidChoice(String choice, int lowerLimit, int upperLimit){ //the choice is the user's input. On the options menu, all choices are numbers. The limit is the amount of choices possible.
        try{
            Integer.parseInt(choice);
        }
        catch (NumberFormatException err){
            throw new UserOptionException(); //throw an error if the answer is not an integer.
        }
        if (Integer.parseInt(choice) > upperLimit || Integer.parseInt(choice) < lowerLimit){
            throw new UserOptionException(); //if the answer is an integer, throw an error if it's out of bounds.
        }
        return true;
    }
    
    //handles asking a question and repeating it until a valid answer is given.
    public int AskQuestion(int lowerLimit, int upperLimit, String question){
        System.out.println(question);
        boolean m_validity = false;
        String answer= m_input.nextLine();
        while (!m_validity){
            try{
                m_validity = CheckValidChoice(answer,lowerLimit,upperLimit); //if the answer is valid, end while loop.
            }
            catch (UserOptionException err){
                System.out.println(err.getMessage());
                answer = m_input.nextLine();
            }
        }
        return Integer.parseInt(answer); //once you've ended the loop and gotten a real answer, return it!
    }
    
    public abstract void ChooseOptions();
}
