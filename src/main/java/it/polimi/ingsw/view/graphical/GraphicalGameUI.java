package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.view.graphical.fx.GameUIController;
import it.polimi.ingsw.view.graphical.fx.ImageCache;
import it.polimi.ingsw.viewmodel.GameView;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
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

            stage.setTitle("GameUI");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(450);
            stage.getIcons().add(ImageCache.getImage("/images/icons/icon.png"));

            stage.setOnShown(e -> {
                latch.countDown();

                stage.toFront();
                stage.requestFocus();
            });

            stage.show();

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
                    }
                }
            });
        });

        while (true){
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Error while waiting for turn.");
                }
            }

            switch (getState()){
                case MY_TURN -> {
                    this.executeTurn();
                }
                case NOT_MY_TURN -> {
                    Platform.runLater(() -> {
                        controller.setNotMyTurn(lastGameView.getCurrentPlayerUsername());
                    });
                }
                case PAUSED -> {
                    //TODO Disables every component
                }
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
            // TODO show other things
            controller.clearCardsArea();

            ObservableList<PlayerInfo> list = new ObservableListBase<>() {
                @Override
                public PlayerInfo get(int index) {
                    return gameView.getPlayerInfo().get(index);
                }

                @Override
                public int size() {
                    return gameView.getPlayerInfo().size();
                }
            };
            controller.playersList.setItems(list);


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

    private void executeTurn(){
        Platform.runLater(() -> {
            this.controller.setMyTurn(this);
        });
    }

    public void turnExecuted(Point[] points, int column){
        notifyListeners(lst, x -> x.performTurn(column, points));
    }

    @Override
    public void gameEnded(GameView gameView) {
        update(gameView);
        String result, scores;
        if (!gameView.isWalkover()) {
            result = "Game has ended. Final points:";
            StringBuilder s = new StringBuilder();
            gameView.getPlayerInfo().forEach(x -> s.append(x).append("\n"));
            s.append("The winner is: ").append(gameView.getPlayerInfo().get(0).username()).append("!");
            scores = s.toString();
        } else {
            result = "Game has ended. You were the only player left in the game. You win!";
            scores = "";
        }
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(result);

            TextArea textArea = new TextArea(scores);
            textArea.setPrefColumnCount(40);
            textArea.setPrefRowCount(6);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            dialog.getDialogPane().setContent(textArea);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait().ifPresent(x -> {
                stage.close();
            });
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
