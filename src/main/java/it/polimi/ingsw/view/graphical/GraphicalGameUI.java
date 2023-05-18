package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.view.graphical.fx.GameUIController;
import it.polimi.ingsw.viewmodel.GameView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GraphicalGameUI extends GameUI {
    private GameUIController controller;

    /**
     * This latch is used to wait until the UI is launched and ready.
     * Otherwise, if the UI is not ready and the client receives an update from the server, JavaFX could crash.
     */
    private final CountDownLatch latch = new CountDownLatch(1);

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
            });

            stage.show();
        });
    }

    @Override
    public void update(GameView gameView) {
        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        Platform.runLater(() -> {
            // TODO show other things
            controller.printBookshelf(gameView.getBookshelfMatrix());
            controller.printLivingRoomBoard(gameView.getLivingRoomBoardMatrix());
        });
    }

    @Override
    public void gameEnded(GameView gameView) {
        // TODO
    }
}
