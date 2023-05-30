package it.polimi.ingsw.view.graphical.fx;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.view.graphical.GraphicalGameUI;
import it.polimi.ingsw.viewmodel.CGCData;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

/**
 * JavaFX FXML controller for gameUI.fxml.
 */
public class GameUIController implements Initializable {
    @FXML
    public GridPane mainGrid;
    @FXML
    public VBox bookshelfArea;
    @FXML
    public StackPane bookshelfStackPane;
    @FXML
    public Text turnText;
    @FXML
    public Button confirmMoveButton;
    @FXML
    public Button resetMoveButton;
    @FXML
    public VBox cardsArea;
    @FXML
    public ListView<PlayerInfo> playersList;

    public GridPane livingRoomBoardGridPane;
    public GridPane bookshelfGridPane;

    /**
     * The size of a single living room board tile.
     * Each tile is a square.
     */
    private NumberBinding livingRoomBoardSize;
    /**
     * The width of the bookshelf.
     */
    private NumberBinding bookshelfWidth;
    /**
     * This ratio is used to fit the bookshelf tiles in the bookshelf grid.
     */
    private static final double bookshelfTileRatio = 98.0/113.0;
    /**
     * Coordinates of the tiles in the living room board that are being moved.
     */
    public final ArrayList<Point> moveCoordinates = new ArrayList<>();
    /**
     * The column of the living room board where the tiles are being moved.
     */
    public int moveColumn = -1;

    /**
     * Reference to the bookshelf tiles ImageViews.
     */
    private final ImageView[][] bookshelfTileImageViews = new ImageView[Constants.bookshelfX][Constants.bookshelfY];
    /**
     * Reference to the living room board tiles ImageViews.
     */
    private final ImageView[][] lrbTileImageViews = new ImageView[Constants.livingRoomBoardX][Constants.livingRoomBoardY];
    /**
     * Reference to the bookshelf greyTiles ImageViews.
     * These tiles are part of the drag-and-drop mechanism.
     * They are used to show the position where the tiles can be dropped.
     */
    private final List<ImageView> greyTiles = new ArrayList<>();
    /**
     * Reference to the bookshelf temporary tiles ImageViews.
     * These tiles are part of the drag-and-drop mechanism.
     * They are used to show the tiles that are being moved, before the move is confirmed.
     */
    private final List<ImageView> temporaryBookshelfTiles = new ArrayList<>();

    /**
     * Initializes the game's components in the Graphical GameUI.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Calculate sizes
        livingRoomBoardSize = Bindings.min(mainGrid.widthProperty().multiply(0.5/(Constants.livingRoomBoardX + 2)), mainGrid.heightProperty().divide(Constants.livingRoomBoardY + 2));
        bookshelfWidth = Bindings.min(mainGrid.widthProperty().multiply(0.3), mainGrid.heightProperty().multiply(0.5));

        // Set window background image
        BackgroundImage backgroundImage = new BackgroundImage(
                ImageCache.getImage("/images/background.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        mainGrid.setBackground(new Background(backgroundImage));

        // Setup of the living room board grid
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

        // Columns and rows constraints for the living room board grid
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

        ImageView bookshelfImage = new ImageView(ImageCache.getImage("/images/bookshelf.png"));
        bookshelfImage.fitWidthProperty().bind(bookshelfWidth);
        bookshelfImage.setPreserveRatio(true);

        bookshelfGridPane = new GridPane();
        bookshelfGridPane.setAlignment(Pos.CENTER);
        bookshelfGridPane.setGridLinesVisible(false);
        bookshelfStackPane.getChildren().add(bookshelfGridPane);
        bookshelfStackPane.getChildren().add(bookshelfImage);

        // Columns and rows constraints for the bookshelf grid
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

        resetMoveButton.setOnAction(e -> this.resetMove());

        GridPane.setRowSpan(cardsArea, 2);

        playersList.maxWidthProperty().bind(bookshelfWidth.multiply(0.85));
    }

    /**
     * Shows the Living Room Board on the screen.
     * @param matrix Data about the contents of the Living Room Board.
     * @param state The current state of the player associated with the UI.
     */
    public void printLivingRoomBoard(Tile[][] matrix, GraphicalGameUI.State state) {
        livingRoomBoardGridPane.getChildren().clear();

        for(int j = Constants.livingRoomBoardY - 1; j >= 0; j--){
            for(int i = 0; i < Constants.livingRoomBoardX; i++){
                lrbTileImageViews[i][j] = null;

                Tile tile = matrix[i][j];
                if(tile != null && !tile.isPlaceholder()){
                    Image image = ImageCache.getImage(tile.getImagePath());
                    ImageView tileImage = new ImageView(image);
                    lrbTileImageViews[i][j] = tileImage;
                    tileImage.fitWidthProperty().bind(livingRoomBoardSize.multiply(0.95));
                    tileImage.setPreserveRatio(true);
                    Tooltip tooltip = new Tooltip("[" + i + ", " + j + "]");
                    Tooltip.install(tileImage, tooltip);

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

    /**
     * Shows the bookshelf on the screen.
     * @param matrix Data about the contents of the Bookshelf.
     * @param state The current state of the player associated with the UI.
     */
    public void printBookshelf(Tile[][] matrix, GraphicalGameUI.State state){
        bookshelfGridPane.getChildren().clear();

        for(int j = Constants.bookshelfY - 1; j >= 0; j--){
            for(int i = Constants.bookshelfX - 1; i >= 0; i--){
                bookshelfTileImageViews[i][j] = null;

                Tile tile = matrix[i][j];
                if(tile != null && !tile.isPlaceholder()){
                    Image image = ImageCache.getImage(tile.getImagePath());
                    ImageView tileImage = new ImageView(image);
                    tileImage.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
                    tileImage.setPreserveRatio(true);
                    bookshelfGridPane.add(tileImage, i + 1, Constants.bookshelfY - j);
                    bookshelfTileImageViews[i][j] = tileImage;
                }
            }
        }
        // Creating grey tiles to show where tiles can be put
        if(state == GraphicalGameUI.State.MY_TURN){
            this.createGreyTiles();
        }
    }

    /**
     * Clears the Cards Area of any previously shown content.
     */
    public void clearCardsArea(){
        cardsArea.getChildren().clear();
    }

    /**
     * Shows all goal cards on the screen.
     * @param pgcImagePath A path leading to the correct asset to show the Personal Goal Card.
     * @param cgcData Contains information about the Common Goal Cards to show.
     */
    public void printCards(String pgcImagePath, List<CGCData> cgcData){
        for(CGCData data : cgcData){
            StackPane cardStack = new StackPane();

            ImageView card = new ImageView(ImageCache.getImage(data.image_path()));
            card.fitWidthProperty().bind(mainGrid.widthProperty().multiply(0.15));
            card.fitHeightProperty().bind(mainGrid.heightProperty().multiply(0.5 / cgcData.size()));
            card.setPreserveRatio(true);

            cardStack.getChildren().add(card);

            if(data.tokens().length > 0) {
                ImageView token = new ImageView(ImageCache.getImage("/images/commonGoalCards/tokens/scoring_" + Arrays.stream(data.tokens()).max().getAsInt() + ".png"));
                token.fitWidthProperty().bind(mainGrid.widthProperty().multiply(0.15));
                token.fitHeightProperty().bind(mainGrid.heightProperty().multiply(0.5 / cgcData.size()));
                token.setPreserveRatio(true);
                cardStack.getChildren().add(token);
            }

            Tooltip tooltip = new Tooltip(data.description());
            tooltip.setShowDelay(Duration.millis(1));
            Tooltip.install(cardStack, tooltip);

            cardsArea.getChildren().add(cardStack);
        }

        ImageView personalGoalCard = new ImageView(ImageCache.getImage(pgcImagePath));
        personalGoalCard.fitWidthProperty().bind(mainGrid.widthProperty().multiply(0.15));
        personalGoalCard.fitHeightProperty().bind(mainGrid.heightProperty().multiply(0.5));
        personalGoalCard.setPreserveRatio(true);
        cardsArea.getChildren().add(personalGoalCard);
    }

    /**
     * Shows the scoring tokens on the screen.
     * @param tokens Tokens owned by the player, represented as a list of integers.
     */
    public void printTokens(List<Integer> tokens){
        FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(5);
        cardsArea.getChildren().add(flowPane);
        for(int token : tokens) {
            ImageView tokenImage = new ImageView(ImageCache.getImage("/images/scoringTokens/scoring_" + token + ".jpg"));
            tokenImage.setPreserveRatio(true);
            tokenImage.fitWidthProperty().bind(Bindings.min(mainGrid.widthProperty().multiply(0.03), mainGrid.heightProperty().multiply(0.07)));
            flowPane.getChildren().add(tokenImage);
        }
    }

    /**
     * Shows grey tiles in each column of the Bookshelf which still has at least one free space.
     * The grey tiles are meant to tell the player where they can currently put tiles.
     */
    private void createGreyTiles() {
        greyTiles.clear();
        for(int column = 0; column < Constants.bookshelfX; column++){
            for(int row = 0; row < Constants.bookshelfY; row++){
                if(bookshelfTileImageViews[column][row] == null){
                    Image image = ImageCache.getImage("/images/tiles/grey.gif");
                    ImageView greyTile = new ImageView(image);
                    greyTile.setOpacity(0.7);
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

    /**
     * Sets the behaviour of a tile in relation to a mouse Drop action.
     * @param point The coordinates of the tile.
     * @param tileImage The tile to which to attribute the specified behaviour.
     */
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
            move(point, pf, tl);
        });
    }

    /**
     * This method is called when the player drops a tile on a grey tile.
     * @param bookshelfPoint coordinates of the bookshelf where the player is currently moving tiles.
     * @param lrbPoint coordinates of the living room board tile chosen by the player.
     * @param tile the tile chosen by the player
     */
    private void move(Point bookshelfPoint, Point lrbPoint, Tile tile){
        if(this.moveCoordinates.size() < Constants.maxPick){
            this.moveCoordinates.add(lrbPoint);
            this.moveColumn = bookshelfPoint.x();

            bookshelfGridPane.getChildren().removeAll(greyTiles);
            greyTiles.clear();

            ImageView temporaryTile = new ImageView(ImageCache.getImage(tile.getImagePath()));
            temporaryTile.setOpacity(0.5);
            temporaryTile.setPreserveRatio(true);
            temporaryTile.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
            bookshelfGridPane.add(temporaryTile, bookshelfPoint.x() + 1, Constants.bookshelfY - bookshelfPoint.y());
            temporaryBookshelfTiles.add(temporaryTile);

            if(bookshelfPoint.y() < Constants.bookshelfY - 1 && this.moveCoordinates.size() < Constants.maxPick){
                ImageView greyTile = new ImageView(ImageCache.getImage("/images/tiles/grey.gif"));
                greyTile.setOpacity(0.7);
                greyTile.fitHeightProperty().bind(bookshelfWidth.divide(Constants.bookshelfX + 2).multiply(bookshelfTileRatio));
                greyTile.setPreserveRatio(true);
                setTileOnDragDropped(new Point(bookshelfPoint.x(), bookshelfPoint.y() + 1), greyTile);
                bookshelfGridPane.add(greyTile, bookshelfPoint.x() + 1, Constants.bookshelfY - bookshelfPoint.y() - 1);
                greyTiles.add(greyTile);
            }

            lrbTileImageViews[lrbPoint.x()][lrbPoint.y()].setOpacity(0.3);
            lrbTileImageViews[lrbPoint.x()][lrbPoint.y()].setDisable(true);
        }
    }

    /**
     * Resets the move being currently played.
     */
    public void resetMove(){
        bookshelfGridPane.getChildren().removeAll(greyTiles);
        bookshelfGridPane.getChildren().removeAll(temporaryBookshelfTiles);
        greyTiles.clear();
        temporaryBookshelfTiles.clear();

        this.moveCoordinates.forEach(c -> {
            lrbTileImageViews[c.x()][c.y()].setOpacity(1);
            lrbTileImageViews[c.x()][c.y()].setDisable(false);
        });

        this.moveColumn = -1;
        this.moveCoordinates.clear();
        this.createGreyTiles();
    }

    /**
     * Sets the current state to "My turn".
     */
    public void setMyTurn(){
        this.moveCoordinates.clear();
        turnText.setText("YOUR TURN!");
        turnText.setFill(Color.color(0,0.75,0.14));
        this.confirmMoveButton.setDisable(false);
        this.resetMoveButton.setDisable(false);
    }

    /**
     * Sets the current state to "Not my turn".
     * @param currentPlayer A string containing the name of the current player in the game.
     */
    public void setNotMyTurn(String currentPlayer){
        turnText.setText("PLAYER " + currentPlayer + "'s TURN!");
        turnText.setFill(Color.color(0.7,0,0));
        this.confirmMoveButton.setDisable(true);
        this.resetMoveButton.setDisable(true);
    }
}