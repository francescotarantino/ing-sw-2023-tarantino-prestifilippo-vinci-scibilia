package it.polimi.ingsw.exception;

/**
 * Exception thrown when a player tries to perform an invalid move.
 */
public class InvalidMoveException extends GameException {
    public InvalidMoveException(String s) {
        super(s);
    }
}
