package it.polimi.ingsw.exception;

/**
 * This is the superclass of all the exceptions that can be thrown before the user is playing.
 */
public abstract class PreGameException extends Exception {
    /**
     * This constructor is used to create a new StartGameException with the given error message.
     * @param s the message of the exception
     */
    public PreGameException(String s) {
        super(s);
    }
}
