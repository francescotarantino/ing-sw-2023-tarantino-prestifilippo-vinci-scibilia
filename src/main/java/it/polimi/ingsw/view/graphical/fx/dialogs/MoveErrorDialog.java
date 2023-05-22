package it.polimi.ingsw.view.graphical.fx.dialogs;

import javafx.scene.control.Alert;

public class MoveErrorDialog extends Alert {
    public MoveErrorDialog() {
        super(AlertType.ERROR);

        setTitle("Error");

        setHeaderText("You can't take that tile!");
        setContentText("Taken tiles must form a straight line and have at least one free side.");
    }
}
