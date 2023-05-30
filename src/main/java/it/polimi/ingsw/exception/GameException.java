package it.polimi.ingsw.exception;

/**
 * This is the superclass of all the exceptions that can be thrown during the game.
 */
public abstract class GameException extends Exception {
    public GameException(String s) {
        super(s);
    }
}
