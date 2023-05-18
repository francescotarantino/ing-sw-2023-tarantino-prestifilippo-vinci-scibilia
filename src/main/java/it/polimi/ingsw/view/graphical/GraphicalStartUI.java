package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.view.StartUI;
import it.polimi.ingsw.view.graphical.fx.FXApplication;
import it.polimi.ingsw.view.graphical.fx.ImageCache;
import it.polimi.ingsw.view.graphical.fx.StartUIController;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

public class GraphicalStartUI extends StartUI {
    private String username;
    private StartUIController controller;
    /**
     * Main window of the StartUI.
     */
    private Stage stage;

    @Override
    public void run() {
        System.out.println("Loading all assets...");
        ImageCache.loadImages();
        System.out.println("Assets loaded!");

        new Thread(() -> {
            Application.launch(FXApplication.class);
            notifyListeners(lst, StartUIListener::exit);
        }).start();

        try {
            FXApplication.waitUntilLaunch();
        } catch (InterruptedException ignored) {}

        Platform.runLater(() -> {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startUI.fxml"));

            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            controller = loader.getController();

            Scene scene = new Scene(root);

            stage.setTitle("MyShelfie");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.centerOnScreen();
            stage.getIcons().add(ImageCache.getImage("/images/icons/icon.png"));

            stage.setOnShown(e -> {
                stage.toFront();
                FXApplication.execute(() -> {
                    notifyListeners(lst, StartUIListener::refreshStartUI);
                });
            });

            controller.gameListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(GameDetailsView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        StringBuilder s = new StringBuilder();
                        s.append("Game ").append(item.gameID()).append(" - ").append(item.playersInfo().size()).append("/").append(item.numberOfPlayers()).append(" players\t");
                        if(item.isStarted())
                            s.append("STARTED\n");
                        else
                            s.append("NOT STARTED\n");
                        s.append("Players: ");
                        item.playersInfo().forEach(playerInfo -> s.append(" ").append(playerInfo.username()));

                        setText(s.toString());

                        setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getClickCount() == 2) {
                                setWaitingForPlayersState();

                                FXApplication.execute(() -> {
                                    notifyListeners(lst, l-> l.joinGame(item.gameID(), username));
                                });
                            }
                        });
                    }
                }
            });

            controller.startButton.setOnAction(e -> {
                setWaitingForPlayersState();

                RadioButton rbNumberOfPlayers = (RadioButton) controller.numberOfPlayers.getSelectedToggle();
                int numberOfPlayers = Integer.parseInt(rbNumberOfPlayers.getText());

                RadioButton rbNumberOfCGCs = (RadioButton) controller.numberOfCGCs.getSelectedToggle();
                int numberOfCGCs = Integer.parseInt(rbNumberOfCGCs.getText());

                FXApplication.execute(() -> {
                    notifyListeners(lst, startUIListener -> startUIListener.createGame(numberOfPlayers, numberOfCGCs, username));
                });
            });

            controller.usernameField.textProperty().addListener(((obs, oldVal, newVal) -> {
                this.username = newVal;
                if(newVal.isEmpty()){
                    Platform.runLater(this::showUsernameDialog);
                }
            }));

            stage.show();

            showUsernameDialog();
        });
    }

    /**
     * This method shows a dialog asking the user to insert his username.
     * If the dialog is closed without inserting a username, it will be shown again.
     */
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

        dialog.showAndWait().ifPresentOrElse(x -> {
            this.controller.usernameField.textProperty().setValue(x);
            this.username = x;
        }, () -> {
            Platform.runLater(this::showUsernameDialog);
        });
    }

    /**
     * This method hides the create game panel and shows the waiting for players panel.
     * It also disables the game list view and the username field.
     * It should be called when the user creates a game or joins an existing one.
     */
    private void setWaitingForPlayersState() {
        Platform.runLater(() -> {
            controller.createGamePanel.setVisible(false);
            controller.gameListView.setDisable(true);
            controller.usernameField.setDisable(true);
            controller.waitingForPlayersPanel.setVisible(true);
        });
    }

    /**
     * This method sets the default state of the StartUI.
     */
    private void setDefaultState() {
        Platform.runLater(() -> {
            controller.waitingForPlayersPanel.setVisible(false);
            controller.createGamePanel.setVisible(true);
            controller.gameListView.setDisable(false);
            controller.usernameField.setDisable(false);
        });
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
            controller.gameListView.setItems(list);
        });
    }

    @Override
    public void showError(String err) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred.");
            alert.setContentText(err);
            alert.show();
        });

        setDefaultState();
    }

    @Override
    public void showPlayersList(List<String> o) {
        ObservableList<String> list = new ObservableListBase<>() {
            @Override
            public String get(int index) {
                return o.get(index);
            }

            @Override
            public int size() {
                return o.size();
            }
        };

        Platform.runLater(() -> {
            controller.waitingForPlayersList.setItems(list);
        });
    }

    /**
     * This method closes the startUI window.
     */
    @Override
    public void close() {
        Platform.runLater(() -> {
            stage.close();
        });
    }
}
