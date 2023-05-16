package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.viewmodel.GameView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicalGameUI extends GameUI {
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
            Scene scene = new Scene(root);

            stage.setTitle("GameUI");
            stage.setScene(scene);

            // TODO: wip

            stage.show();
        });
    }

    @Override
    public void update(GameView gameView) {
        // TODO
    }

    @Override
    public void gameFinished(GameView gameView) {
        // TODO
    }
}
