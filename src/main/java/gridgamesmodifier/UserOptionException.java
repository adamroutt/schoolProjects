package gridgamesmodifier;

public class UserOptionException extends RuntimeException{
    public UserOptionException(String msg) {
        super(msg);
    }
    public UserOptionException() {
        super("Invalid input! Try again.");
    }
}
