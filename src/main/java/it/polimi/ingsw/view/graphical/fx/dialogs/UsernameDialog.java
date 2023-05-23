package it.polimi.ingsw.view.graphical.fx.dialogs;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * This dialog asks the username to the user.
 * It cannot be closed until a valid username is inserted.
 * @see it.polimi.ingsw.view.graphical.GraphicalStartUI
 */
public class UsernameDialog extends Dialog<String> {
    public UsernameDialog() {
        super();

        setTitle("MyShelfie");
        setHeaderText("Insert your username: ");

        ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType);

        HBox pane = new HBox();

        TextField username = new TextField();
        pane.getChildren().add(username);

        Node confirmButton = getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(newValue.trim().isEmpty());
        });

        getDialogPane().setContent(pane);

        setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return username.getText();
            }
            return null;
        });
    }
}
