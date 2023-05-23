package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.view.graphical.fx.FXApplication;
import it.polimi.ingsw.view.graphical.fx.GameUIController;
import it.polimi.ingsw.view.graphical.fx.ImageCache;
import it.polimi.ingsw.view.graphical.fx.dialogs.PauseDialog;
import it.polimi.ingsw.view.graphical.fx.dialogs.ResultsAlert;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

public class GraphicalGameUI extends GameUI {
    private GameUIController controller;

    /**
     * This latch is used to wait until the UI is launched and ready.
     * Otherwise, if the UI is not ready and the client receives an update from the server, JavaFX could crash.
     */
    private final CountDownLatch latch = new CountDownLatch(1);

    public enum State {
        MY_TURN,
        NOT_MY_TURN,
        PAUSED
    }
    private State state = State.NOT_MY_TURN;
    private final Object lock = new Object();

    private Stage stage;
    private PauseDialog pauseDialog;

    private GameView lastGameView;

    @Override
    public void run() {
        Platform.runLater(() -> {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameUI.fxml"));

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
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(450);
            stage.setMaximized(true);
            stage.getIcons().add(ImageCache.getImage("/images/icons/icon.png"));

            stage.setOnShown(e -> {
                latch.countDown();

                stage.toFront();
                stage.requestFocus();
            });

            stage.show();

            pauseDialog = new PauseDialog();

            controller.playersList.setCellFactory(lv -> new ListCell<>(){
                @Override
                protected void updateItem(PlayerInfo playerInfo, boolean b) {
                    super.updateItem(playerInfo, b);
                    if(b) {
                        setText(null);
                    } else {
                        StringBuilder s = new StringBuilder();
                        s.append(playerInfo.username()).append(":");
                        if(playerInfo.isConnected())
                            s.append(" (CONNECTED)");
                        else
                            s.append(" (DISCONNECTED)");
                        if(playerInfo.username().equals(lastGameView.getFirstPlayerUsername()))
                            s.append(" | (FIRST)");
                        if(playerInfo.username().equals(lastGameView.getCurrentPlayerUsername()))
                            s.append(" | (CURRENT)");
                        if(lastGameView.getFinalPlayerUsername() != null && lastGameView.getFinalPlayerUsername().equals(playerInfo.username()))
                            s.append(" | (LAST)");
                        setText(s.toString());

                        StringBuilder t = new StringBuilder();
                        t.append("Last move: ");
                        if(playerInfo.lastMovePoints() != null){
                            t.append("took ");
                            t.append(String.join(", ", Arrays.stream(playerInfo.lastMoveTiles()).map(Tile::toString).toArray(String[]::new)));
                            t.append(" from ");
                            t.append(String.join(", ", Arrays.stream(playerInfo.lastMovePoints()).map(Point::toString).toArray(String[]::new)));
                            t.append(".");
                        } else {
                            t.append("hasn't played yet!");
                        }
                        t.append("\n");
                        if(playerInfo.tokens().isEmpty()) {
                            t.append("No tokens yet.");
                        } else {
                            t.append(playerInfo.getTokensString());
                        }
                        Tooltip lastmoveTooltip = new Tooltip(t.toString());
                        lastmoveTooltip.setShowDelay(Duration.millis(100));
                        setTooltip(lastmoveTooltip);
                    }
                }
            });
        });

        while (true){
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {
                    break;
                }
            }

            switch (getState()){
                case MY_TURN -> Platform.runLater(() -> {
                    pauseDialog.closeIfShown();

                    this.controller.setMyTurn(this);
                });
                case NOT_MY_TURN -> Platform.runLater(() -> {
                    pauseDialog.closeIfShown();

                    controller.setNotMyTurn(lastGameView.getCurrentPlayerUsername());
                });
                case PAUSED -> Platform.runLater(pauseDialog::showIfNotShown);
            }
        }
    }

    @Override
    public void update(GameView gameView) {
        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        this.lastGameView = gameView;
        if(gameView.isGamePaused()){
            setState(State.PAUSED);
            return;
        }

        if (gameView.getCurrentPlayerIndex() == gameView.getMyIndex()) {
            setState(State.MY_TURN);
        } else {
            setState(State.NOT_MY_TURN);
        }

        Platform.runLater(() -> {
            controller.clearCardsArea();

            controller.playersList.setItems(FXCollections.observableList(gameView.getPlayerInfo()));

            if(gameView.getFinalPlayerUsername() != null){
                BackgroundImage livingRoomBoardBackground = new BackgroundImage(
                        ImageCache.getImage("/images/livingRoomBoardNoToken.png"),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
                );

                controller.livingRoomBoardGridPane.setBackground(new Background(livingRoomBoardBackground));
            }

            controller.printTokens(gameView.getPlayerInfo().get(gameView.getMyIndex()).tokens());
            controller.printBookshelf(gameView.getBookshelfMatrix(), state);
            controller.printLivingRoomBoard(gameView.getLivingRoomBoardMatrix(), state);
            controller.printCards(gameView.getPersonalGoalCardImagePath(), gameView.getCGCData());
        });
    }

    public void turnExecuted(Point[] points, int column){
        FXApplication.execute(() -> notifyListeners(lst, x -> x.performTurn(column, points)));
    }

    @Override
    public void gameEnded(GameView gameView) {
        update(gameView);
        Platform.runLater(() -> {
            pauseDialog.closeIfShown();

            ResultsAlert dialog = new ResultsAlert(gameView);
            dialog.showAndWait().ifPresent(x -> stage.close());
        });
    }

    /**
     * This method is used to get the current state of the player.
     * @return the current state
     */
    private State getState() {
        synchronized (lock) {
            return this.state;
        }
    }

    /**
     * This method is used to set the current state of the player.
     * @param state  the state to set the player to
     */
    private void setState(State state) {
        synchronized (lock) {
            this.state = state;
            lock.notifyAll();
        }
    }
}
