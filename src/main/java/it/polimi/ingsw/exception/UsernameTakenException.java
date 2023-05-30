package it.polimi.ingsw.exception;

/**
 * This exception is thrown when the user tries to join a game, but the username is already taken.
 */
public class UsernameTakenException extends PreGameException {
    public UsernameTakenException() {
        super("The username is already taken in this game.");
    }
}
