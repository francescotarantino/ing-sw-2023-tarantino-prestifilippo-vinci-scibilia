package it.polimi.ingsw.exception;

/**
 * This exception is thrown when the user tries to join a game that is already full.
 */
public class NoFreeBookshelfException extends PreGameException {
    public NoFreeBookshelfException() {
        super("There are no free bookshelves available.");
    }
}
