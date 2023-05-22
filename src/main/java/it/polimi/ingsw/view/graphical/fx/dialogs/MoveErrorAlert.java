package it.polimi.ingsw.view.graphical.fx.dialogs;

public class MoveErrorAlert extends ErrorAlert {
    public MoveErrorAlert() {
        super("Taken tiles must form a straight line and have at least one free side.");

        setHeaderText("You can't take that tile!");
    }
}
