package it.polimi.ingsw.view.graphical.fx.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * This dialog is used to display the pause message when a player is the only one left in the game.
 * When shown, it blocks the game UI until the game is resumed or the player is declared winner.
 */
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
