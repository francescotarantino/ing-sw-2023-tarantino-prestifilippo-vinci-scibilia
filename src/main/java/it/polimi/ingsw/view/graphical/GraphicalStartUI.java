package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.view.StartUI;
import it.polimi.ingsw.view.graphical.fx.MainApplication;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

public class GraphicalStartUI extends StartUI {
    private String username;
    private MainApplication mainApplication;

    @Override
    public void run() {
        mainApplication = MainApplication.getInstance();

        Platform.runLater(this::showUsernameDialog);

        notifyListeners(lst, StartUIListener::refreshStartUI);

        mainApplication.controller.gameListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(GameDetailsView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());

                    setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getClickCount() == 2) {
                            System.out.println("Joining game "  + item.gameID() + "...");
                            notifyListeners(lst, l-> l.joinGame(item.gameID(), username));
                        }
                    });
                }
            }
        });
    }

    private void showUsernameDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("MyShelfie");
        dialog.setHeaderText("Insert your username: ");

        ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType);

        HBox pane = new HBox();

        TextField username = new TextField();
        pane.getChildren().add(username);

        Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(pane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return username.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresentOrElse(x -> this.username = x, () -> System.exit(0));
    }

    @Override
    public void showGamesList(List<GameDetailsView> o) {
        ObservableList<GameDetailsView> list = new ObservableListBase<>() {
            @Override
            public GameDetailsView get(int index) {
                return o.get(index);
            }

            @Override
            public int size() {
                return o.size();
            }
        };

        Platform.runLater(() -> {
            mainApplication.controller.gameListView.setItems(list);
        });
    }

    @Override
    public void showError(String err) {
        // TODO
    }

    @Override
    public void showPlayersList(List<String> o) {
        // TODO
    }
}
