package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.view.StartUI;
import it.polimi.ingsw.view.graphical.fx.FXApplication;
import it.polimi.ingsw.view.graphical.fx.ImageCache;
import it.polimi.ingsw.view.graphical.fx.StartUIController;
import it.polimi.ingsw.view.graphical.fx.dialogs.ErrorAlert;
import it.polimi.ingsw.view.graphical.fx.dialogs.UsernameDialog;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

/**
 * Main class for the graphical version of the StartUI.
 */
public class GraphicalStartUI extends StartUI {
    /**
     * The username inserted by the user.
     */
    private String username;
    /**
     * JavaFX FXML controller associated with the Graphical UI.
     */
    private StartUIController controller;
    /**
     * Main window of the StartUI.
     */
    private Stage stage;
    /**
     * This latch is used to wait until the UI is launched and ready.
     * Otherwise, if the UI is not ready and the client receives an update from the server, JavaFX could crash.
     */
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void run() {
        new Thread(() -> {
            Application.launch(FXApplication.class);
            notifyListeners(lst, StartUIListener::exit);
        }).start();

        try {
            FXApplication.waitUntilLaunch();
        } catch (InterruptedException ignored) {}

        System.out.println("Loading all assets...");
        ImageCache.loadImages();
        System.out.println("Assets loaded!");

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
                latch.countDown();
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
        UsernameDialog dialog = new UsernameDialog();

        dialog.showAndWait().ifPresentOrElse(x -> {
            this.controller.usernameField.textProperty().setValue(x);
            this.username = x;
        }, () -> notifyListeners(lst, StartUIListener::exit));
    }

    /**
     * This method hides the "create game" panel and shows the waiting for players panel.
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
        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        Platform.runLater(() -> {
            controller.gameListView.setItems(FXCollections.observableList(o));
        });
    }

    @Override
    public void showError(String err) {
        Platform.runLater(() -> {
            ErrorAlert alert = new ErrorAlert(err);
            alert.show();
        });

        setDefaultState();
    }

    @Override
    public void showPlayersList(List<String> o) {
        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        Platform.runLater(() -> {
            controller.waitingForPlayersList.setItems(FXCollections.observableList(o));
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
