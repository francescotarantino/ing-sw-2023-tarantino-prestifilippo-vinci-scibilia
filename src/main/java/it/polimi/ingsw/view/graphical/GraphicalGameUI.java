package it.polimi.ingsw.view.graphical;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.view.GameUI;
import it.polimi.ingsw.view.graphical.fx.GameUIController;
import it.polimi.ingsw.viewmodel.GameView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GraphicalGameUI extends GameUI {
    private GameUIController controller;

    private final Object lock = new Object();
    private boolean gui = false;

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
            stage.setOnCloseRequest(e -> {
                System.exit(0);
            });

            stage.show();

            synchronized (lock){
                gui = true;
                lock.notifyAll();
            }
        });
    }

    @Override
    public void update(GameView gameView) {
        synchronized (lock){
            while(!gui) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }

            // TODO: save reference to each child
            for(int j = Constants.livingRoomBoardY - 1; j >= 0; j--){
                for(int i = 0; i < Constants.livingRoomBoardX; i++){
                    Tile tile = gameView.getLivingRoomBoardMatrix()[i][j];
                    if(tile != null && !tile.isPlaceholder()){
                        int finalI = i;
                        int finalJ = Constants.livingRoomBoardY - 1 - j;
                        Platform.runLater(() -> {
                            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/tiles/" + tile.getType().toString().toLowerCase() + "_" + (tile.getVariant() + 1) + ".png")));
                            ImageView tileImage = new ImageView(image);
                            tileImage.fitWidthProperty().bind(Bindings.min(controller.mainGrid.widthProperty().multiply(0.5/(Constants.livingRoomBoardX + 2)), controller.mainGrid.heightProperty().divide(Constants.livingRoomBoardY + 2)).multiply(0.95));
                            tileImage.setPreserveRatio(true);
                            controller.livingRoomBoardGridPane.add(tileImage, finalI + 1, finalJ + 1);
                        });
                    }
                }
            }
        }
    }

    @Override
    public void gameFinished(GameView gameView) {
        // TODO
    }
}
