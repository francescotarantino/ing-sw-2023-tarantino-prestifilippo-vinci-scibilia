package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.view.graphical.fx.GameUIController;
import it.polimi.ingsw.view.graphical.fx.ImageCache;
import it.polimi.ingsw.viewmodel.GameView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        ENDED,
        PAUSED
    }
    private State state = State.NOT_MY_TURN;
    private final Object lock = new Object();
    
    private GameView lastGameView;

    @Override
    public void run() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
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

            stage.setOnShown(e -> {
                latch.countDown();

                stage.toFront();
                stage.requestFocus();

                controller.setNotMyTurn();
            });

            stage.show();
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
                case PAUSED -> {
                    //TODO Disables every component
                }
                case ENDED -> {
                    //TODO Show game result
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
            controller.printBookshelf(gameView.getBookshelfMatrix(), state);
            controller.printLivingRoomBoard(gameView.getLivingRoomBoardMatrix(), state);
        });
    }

    private void executeTurn(){
        Platform.runLater(() -> {
            this.controller.setMyTurn(this);
        });
    }

    public void turnExecuted(Point[] points, int column){
        notifyListeners(lst, x -> x.performTurn(column, points));
        Platform.runLater(() -> {
            this.controller.setNotMyTurn();
        });
    }

    @Override
    public void gameEnded(GameView gameView) {
        // TODO
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
