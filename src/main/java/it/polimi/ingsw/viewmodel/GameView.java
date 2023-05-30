package it.polimi.ingsw.viewmodel;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Tile;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * This class is used to send the current relevant data from the model of the game to the client.
 */
public class GameView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The index of the player who is receiving this GameView.
     */
    private final int myIndex;
    /**
     * The matrix of the actual living room board.
     */
    private final Tile[][] livingRoomBoardMatrix;
    /**
     * The matrix of the player's bookshelf.
     */
    private final Tile[][] bookshelfMatrix;
    /**
     * The matrix of the player's personal goal card.
     */
    private final Constants.TileType[][] personalGoalCardMatrix;
    /**
     * The path of the player's personal goal card image.
     */
    private final String personalGoalCardImagePath;
    /**
     * The index of the current player who's playing.
     */
    private final int currentPlayerIndex;

    /**
     * The current player's username.
     */
    private final String currentPlayerUsername;
    /**
     * The username of the player who played first.
     */
    private final String firstPlayerUsername;
    /**
     * The descriptions and tokens of the common goal cards in the game.
     */
    private final String finalPlayerUsername;
    private final List<CGCData> cgcData;
    /**
     * True if the game is ended, false otherwise.
     */
    private final boolean gameEnded;
    /**
     * True if the game is paused (there is only one player connected), false otherwise.
     */
    private final boolean gamePaused;
    /**
     * True if a walkover has occurred, false otherwise.
     */
    private final boolean walkover;
    /**
     * Data about players in the game. Contains the scores of the players in the game.
     * The map is sorted in descending order, so the first entry is the winner.
     * The key is the score, the value is the username. Always contains scoring tokens in possession of each player.
     */
    private final List<PlayerInfo> playersData;
    /**
     * This string contains the latest error message that occurred in the game.
     */
    private final String errorMessage;

    public GameView(Game game, int receivingPlayerIndex){
        this.myIndex = receivingPlayerIndex;

        this.livingRoomBoardMatrix = game.getLivingRoomBoard().getMatrix();
        this.bookshelfMatrix = game.getBookshelves()[this.myIndex].getMatrix();
        this.personalGoalCardMatrix = game.getBookshelves()[this.myIndex].getPersonalGoalCard().getMatrix();
        this.personalGoalCardImagePath = game.getBookshelves()[this.myIndex].getPersonalGoalCard().getImagePath();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.currentPlayerUsername = game.getCurrentPlayer().getUsername();
        this.firstPlayerUsername = game.getBookshelves()[game.getFirstPlayerIndex()].getPlayer().getUsername();

        if(game.getFinalPlayerIndex() == -1) {
            this.finalPlayerUsername = null;
        } else {
            // The last player has the index of 1 less than the one who filled the bookshelf first
            this.finalPlayerUsername = game.getPlayers().get(
                    (game.getFinalPlayerIndex() - 1) >= 0 ? (game.getFinalPlayerIndex() - 1) : (game.getTotalPlayersNumber() - 1)
            ).getUsername();
        }

        this.gameEnded = game.isEnded();
        this.gamePaused = game.isPaused();
        this.walkover = game.isWalkover();

        this.cgcData = Arrays.stream(game.getCommonGoalCards())
                .map(card -> new CGCData(card.getDescription(), card.getAvailableScores(), card.getImagePath()))
                .toList();

        this.playersData = game.getPlayerInfo();
        this.errorMessage = game.getErrorMessage();
    }

    public Tile[][] getBookshelfMatrix() {
        return bookshelfMatrix;
    }

    public Tile[][] getLivingRoomBoardMatrix() {
        return livingRoomBoardMatrix;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    public String getFirstPlayerUsername(){
        return firstPlayerUsername;
    }

    public String getFinalPlayerUsername(){
        return finalPlayerUsername;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public List<PlayerInfo> getPlayerInfo() {
        return playersData;
    }

    public List<CGCData> getCGCData(){
        return cgcData;
    }

    public Constants.TileType[][] getPersonalGoalCardMatrix() {
        return personalGoalCardMatrix;
    }

    public boolean isGamePaused() {
        return gamePaused;
    }

    public int getMyIndex() {
        return myIndex;
    }

    public boolean isWalkover() {
        return walkover;
    }

    public String getPersonalGoalCardImagePath() {
        return personalGoalCardImagePath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}