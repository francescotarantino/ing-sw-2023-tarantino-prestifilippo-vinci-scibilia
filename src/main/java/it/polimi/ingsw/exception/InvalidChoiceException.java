package it.polimi.ingsw.exception;

/**
 * This exception is thrown when the user makes an invalid choice by selecting an invalid option during the
 * creation of a game or when joining a game.
 */
public class InvalidChoiceException extends PreGameException {
    /**
     * @param s the error message to show
     */
    public InvalidChoiceException(String s) {
        super(s);
    }
}
