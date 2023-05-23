package it.polimi.ingsw.view.graphical.fx.dialogs;

import javafx.scene.control.Alert;

/**
 * JavaFX alert dialog used to display an error message.
 */
public class ErrorAlert extends Alert {
    public ErrorAlert(String err) {
        super(AlertType.ERROR);

        setTitle("Error");
        setHeaderText("An error occurred.");
        setContentText(err);
    }
}
