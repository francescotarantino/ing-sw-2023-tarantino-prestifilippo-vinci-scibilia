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
    public void gameFinished(GameView gameView) {
        // TODO
    }
}
