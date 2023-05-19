package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.view.graphical.GraphicalGameUI;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class GameUIController implements Initializable {
    @FXML
    public GridPane mainGrid;
    @FXML
    public VBox bookshelfArea;
    @FXML
    public Pane bookshelfPane;
    @FXML
    public Text turnText;
    @FXML
    public Button confirmMoveButton;
    @FXML
    public Button resetMoveButton;

    public GridPane livingRoomBoardGridPane;
    public GridPane bookshelfGridPane;
    public StackPane bookshelfStackPane;

    private NumberBinding livingRoomBoardSize;
    private NumberBinding bookshelfWidth;
    private final double bookshelfTileRatio = 98.0/113.0;
    private final ArrayList<Point> moveCoordinates = new ArrayList<>();
    private int moveColumn = -1;
    private Tile[][] lastLivingRoomBoard;
    private Tile[][] lastBookshelf;
    private GraphicalGameUI ui;

    private final ImageView[][] lvbTileImageViews = new ImageView[Constants.livingRoomBoardX][Constants.livingRoomBoardY];
    private final List<ImageView> greyTiles = new ArrayList<>();
    private final List<ImageView> temporaryBookshelfTiles = new ArrayList<>();

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
        GridPane.setRowSpan(livingRoomBoardGridPane, 2);
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
        bookshelfPane.getChildren().add(bookshelfStackPane);

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

        confirmMoveButton.setOnAction(e -> this.confirmMove());
        resetMoveButton.setOnAction(e -> this.resetMove());
    }

    public void printLivingRoomBoard(Tile[][] matrix, GraphicalGameUI.State state) {
        livingRoomBoardGridPane.getChildren().clear();
        this.lastLivingRoomBoard = matrix;
        for(int j = Constants.livingRoomBoardY - 1; j >= 0; j--){
            for(int i = 0; i < Constants.livingRoomBoardX; i++){
                Tile tile = matrix[i][j];
                if(tile != null && !tile.isPlaceholder()){
                    Image image = ImageCache.getImage(tile.getImagePath());
                    ImageView tileImage = new ImageView(image);
                    lvbTileImageViews[i][j] = tileImage;
                    tileImage.fitWidthProperty().bind(livingRoomBoardSize.multiply(0.95));
                    tileImage.setPreserveRatio(true);

                    if(state == GraphicalGameUI.State.MY_TURN){
                        int finalI = i;
                        int finalJ = j;
                        tileImage.setOnDragDetected(e -> {
                            Dragboard db = tileImage.startDragAndDrop(TransferMode.MOVE);

                            db.setDragView(tileImage.snapshot(null, null));
                            ClipboardContent content = new ClipboardContent();
                            content.put(Point.pointFormat, new Point(finalI, finalJ));
                            content.put(Tile.tileFormat, tile);
                            db.setContent(content);

                            e.consume();
                        });

                        tileImage.setOnDragEntered(event -> {
                            tileImage.setCursor(Cursor.CLOSED_HAND);
                            event.getDragboard().setDragViewOffsetY(-10);
                        });

                        tileImage.setCursor(Cursor.OPEN_HAND);
                    }

                    livingRoomBoardGridPane.add(tileImage, i + 1, Constants.livingRoomBoardY - j);
                }
            }
        }
    }

    public void printBookshelf(Tile[][] matrix, GraphicalGameUI.State state){
        bookshelfGridPane.getChildren().clear();
        this.lastBookshelf = matrix;

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
        // Creating grey tiles to show where tiles can be put
        if(state == GraphicalGameUI.State.MY_TURN){
            this.createGreyTiles(lastBookshelf);
        }
    }

    private void createGreyTiles(Tile[][] matrix) {
        greyTiles.clear();
        for(int column = 0; column < Constants.bookshelfX; column++){
            for(int row = 0; row < Constants.bookshelfY; row++){
                if(matrix[column][row] == null){
                    Image image = ImageCache.getImage("/images/tiles/grey.gif");
                    ImageView greyTile = new ImageView(image);
                    greyTile.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
                    greyTile.setPreserveRatio(true);
                    setTileOnDragDropped(new Point(column, row), greyTile);
                    bookshelfGridPane.add(greyTile, column + 1, Constants.bookshelfY - row);
                    greyTiles.add(greyTile);
                    break;
                }
            }
        }
    }

    private void setTileOnDragDropped(Point point, ImageView tileImage) {
        tileImage.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(Point.pointFormat) && db.hasContent(Tile.tileFormat)){
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        tileImage.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            Point pf = (Point) db.getContent(Point.pointFormat);
            Tile tl = (Tile) db.getContent(Tile.tileFormat);
            if(move(point, pf, tl)){
                lvbTileImageViews[pf.getX()][pf.getY()].setOpacity(0.3);
                lvbTileImageViews[pf.getX()][pf.getY()].setDisable(true);
            }
            //TODO cambia l'opacità di tutto
        });
    }

    private boolean move(Point bookshelf, Point coordinate, Tile tile){
        Point[] points = new Point[this.moveCoordinates.size() + 1];
        for(int i = 0; i < points.length - 1; i++){
            points[i] = this.moveCoordinates.get(i);
        }
        points[points.length - 1] = coordinate;

        if(points.length <= Constants.maxPick || Utils.checkIfTilesCanBeTaken(this.lastLivingRoomBoard, points)){
            this.moveCoordinates.add(coordinate);
            this.moveColumn = bookshelf.getX();

            bookshelfGridPane.getChildren().removeAll(greyTiles);
            greyTiles.clear();

            ImageView temporaryTile = new ImageView(ImageCache.getImage(tile.getImagePath()));
            temporaryTile.setOpacity(0.5);
            temporaryTile.setPreserveRatio(true);
            temporaryTile.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
            bookshelfGridPane.add(temporaryTile, bookshelf.getX() + 1, Constants.bookshelfY - bookshelf.getY());
            temporaryBookshelfTiles.add(temporaryTile);

            if(bookshelf.getY() < Constants.bookshelfY - 1 && points.length < Constants.maxPick){
                ImageView greyTile = new ImageView(ImageCache.getImage("/images/tiles/grey.gif"));
                greyTile.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
                greyTile.setPreserveRatio(true);
                setTileOnDragDropped(new Point(bookshelf.getX(), bookshelf.getY() + 1), greyTile);
                bookshelfGridPane.add(greyTile, bookshelf.getX() + 1, Constants.bookshelfY - bookshelf.getY() - 1);
                greyTiles.add(greyTile);
            }

            return true;
        }

        return false;
    }

    private void moveErrorMessage(){
        Platform.runLater(() -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Error");

            TextArea textArea = new TextArea("You can't take that tile!\n- Taken tiles must form a straight line and have at least one free side.");
            textArea.setPrefColumnCount(40);
            textArea.setPrefRowCount(3);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            dialog.getDialogPane().setContent(textArea);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
        });
    }

    private void confirmMove(){
        if(Utils.checkIfTilesCanBeTaken(this.lastLivingRoomBoard, this.moveCoordinates.toArray(Point[]::new))){
            this.ui.turnExecuted(this.moveCoordinates.toArray(new Point[0]), this.moveColumn);
            this.confirmMoveButton.setDisable(true);
            this.resetMoveButton.setDisable(true);
        } else {
            this.moveErrorMessage();
            this.resetMove();
        }
    }

    private void resetMove(){
        bookshelfGridPane.getChildren().removeAll(greyTiles);
        bookshelfGridPane.getChildren().removeAll(temporaryBookshelfTiles);
        greyTiles.clear();
        temporaryBookshelfTiles.clear();

        this.moveCoordinates.forEach(c -> {
            lvbTileImageViews[c.getX()][c.getY()].setOpacity(1);
            lvbTileImageViews[c.getX()][c.getY()].setDisable(false);
        });

        this.moveColumn = -1;
        this.moveCoordinates.clear();
        this.createGreyTiles(lastBookshelf);
    }

    public void setMyTurn(GraphicalGameUI ui){
        this.ui = ui;
        this.moveCoordinates.clear();
        turnText.setText("YOUR TURN!");
        turnText.setFill(Color.color(0,0.75,0.14));
        this.confirmMoveButton.setDisable(false);
        this.resetMoveButton.setDisable(false);
    }

    public void setNotMyTurn() {
        turnText.setText("NOT YOUR TURN!");
        turnText.setFill(Color.color(0.7,0,0));
        this.confirmMoveButton.setDisable(true);
        this.resetMoveButton.setDisable(true);
    }
}
