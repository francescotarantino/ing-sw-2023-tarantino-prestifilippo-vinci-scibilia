package it.polimi.ingsw.view.graphical.fx.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This dialog is used to display the pause message when a player is the only one left in the game.
 * When shown, it blocks the game UI until the game is resumed or the player is declared winner.
 */
public class PauseDialog extends Dialog<Void> {
    public PauseDialog() {
        super();

        setTitle("Game paused");
        setHeaderText("The game has been paused since you're the only player left in the game.");

        Label label = new Label("Please wait for other players to reconnect...");
        Button button = new Button("Quit");
        button.setOnAction(event -> System.exit(0));
        button.setAlignment(Pos.CENTER_RIGHT);

        VBox vBox = new VBox();
        vBox.getChildren().add(label);
        HBox hBox = new HBox();
        hBox.getChildren().add(button);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(hBox);

        getDialogPane().setContent(vBox);
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
