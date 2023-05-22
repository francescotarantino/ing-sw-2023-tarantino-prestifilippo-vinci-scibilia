package it.polimi.ingsw.view.graphical.fx.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class PauseDialog extends Dialog<Void> {
    public PauseDialog() {
        super();

        setTitle("Game paused");
        setHeaderText("The game has been paused since you're the only player left in the game.");
        setContentText("Please wait for other players to reconnect...");
    }

    public void showIfNotShown() {
        if (!isShowing()){
            show();
        }
    }

    public void closeIfShown() {
        if (isShowing()){
            getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            close();
            getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
        }
    }
}
