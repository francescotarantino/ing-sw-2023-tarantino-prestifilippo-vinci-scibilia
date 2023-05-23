package it.polimi.ingsw.view.graphical.fx.dialogs;

/**
 * This alert is used to display an error message when a player tries to make an invalid move.
 */
public class MoveErrorAlert extends ErrorAlert {
    public MoveErrorAlert() {
        super("Taken tiles must form a straight line and have at least one free side.");

        setHeaderText("You can't take that tile!");
    }
}
