package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GameUIController implements Initializable {
    @FXML
    public GridPane mainGrid;
    @FXML
    public VBox bookshelfArea;

    public GridPane livingRoomBoardGridPane;
    public GridPane bookshelfGridPane;
    public StackPane bookshelfStackPane;

    private NumberBinding livingRoomBoardSize;
    private NumberBinding bookshelfWidth;
    private final double bookshelfTileRatio = 98.0/113.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        livingRoomBoardSize = Bindings.min(mainGrid.widthProperty().multiply(0.5/(Constants.livingRoomBoardX + 2)), mainGrid.heightProperty().divide(Constants.livingRoomBoardY + 2));
        bookshelfWidth = mainGrid.widthProperty().multiply(0.3);

        BackgroundImage backgroundImage = new BackgroundImage(
                ImageCache.getImage("/images/background.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        mainGrid.setBackground(new Background(backgroundImage));

        livingRoomBoardGridPane = new GridPane();
        BackgroundImage livingRoomBoardBackground = new BackgroundImage(
                ImageCache.getImage("/images/livingRoomBoard.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        );

        livingRoomBoardGridPane.setBackground(new Background(livingRoomBoardBackground));
        livingRoomBoardGridPane.setGridLinesVisible(false);
        livingRoomBoardGridPane.setAlignment(Pos.CENTER);
        mainGrid.add(livingRoomBoardGridPane, 0, 0);

        for(int i = 0; i < Constants.livingRoomBoardX + 2; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.prefWidthProperty().bind(livingRoomBoardSize);
            c.setHalignment(HPos.CENTER);
            livingRoomBoardGridPane.getColumnConstraints().add(c);
        }

        for(int i = 0; i < Constants.livingRoomBoardY + 2; i++) {
            RowConstraints r = new RowConstraints();
            r.prefHeightProperty().bind(livingRoomBoardSize);
            r.setValignment(VPos.CENTER);
            livingRoomBoardGridPane.getRowConstraints().add(r);
        }

        bookshelfStackPane = new StackPane();
        bookshelfArea.getChildren().add(bookshelfStackPane);

        ImageView bookshelfImage = new ImageView(ImageCache.getImage("/images/bookshelf.png"));
        bookshelfImage.fitWidthProperty().bind(bookshelfWidth);
        bookshelfImage.setPreserveRatio(true);

        bookshelfGridPane = new GridPane();
        bookshelfGridPane.setAlignment(Pos.CENTER);
        bookshelfGridPane.setGridLinesVisible(false);
        bookshelfStackPane.getChildren().add(bookshelfGridPane);
        bookshelfStackPane.getChildren().add(bookshelfImage);

        for(int i = 0; i < Constants.bookshelfX + 2; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.prefWidthProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2));
            c.setHalignment(HPos.CENTER);
            bookshelfGridPane.getColumnConstraints().add(c);
        }

        for(int i = 0; i < Constants.bookshelfY + 2; i++) {
            RowConstraints r = new RowConstraints();
            r.prefHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
            r.setValignment(VPos.CENTER);
            bookshelfGridPane.getRowConstraints().add(r);
        }
    }

    public void printLivingRoomBoard(Tile[][] matrix) {
        livingRoomBoardGridPane.getChildren().clear();

        for(int j = Constants.livingRoomBoardY - 1; j >= 0; j--){
            for(int i = 0; i < Constants.livingRoomBoardX; i++){
                Tile tile = matrix[i][j];
                if(tile != null && !tile.isPlaceholder()){
                    Image image = ImageCache.getImage(tile.getImagePath());
                    ImageView tileImage = new ImageView(image);
                    tileImage.fitWidthProperty().bind(livingRoomBoardSize.multiply(0.95));
                    tileImage.setPreserveRatio(true);
                    livingRoomBoardGridPane.add(tileImage, i + 1, Constants.livingRoomBoardY - j);
                }
            }
        }
    }

    public void printBookshelf(Tile[][] matrix){
        bookshelfGridPane.getChildren().clear();

        for(int j = Constants.bookshelfY - 1; j >= 0; j--){
            for(int i = Constants.bookshelfX - 1; i >= 0; i--){
                Tile tile = matrix[i][j];
                if(tile != null && !tile.isPlaceholder()){
                    Image image = ImageCache.getImage(tile.getImagePath());
                    ImageView tileImage = new ImageView(image);
                    tileImage.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
                    tileImage.setPreserveRatio(true);
                    bookshelfGridPane.add(tileImage, i + 1, Constants.bookshelfY - j);
                }
            }
        }
    }
}
