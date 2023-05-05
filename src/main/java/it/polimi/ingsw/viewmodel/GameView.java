package it.polimi.ingsw.viewmodel;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import static java.lang.Math.abs;

/**
 * This class is used to send the current relevant data from model of the game to the client.
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

    private final Constants.TileType[][] personalGoalCardMatrix;
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
     * True if the game is finished, false otherwise.
     */
    private final boolean gameFinished;
    /**
     * True if the game is paused (there is only one player connected), false otherwise.
     */
    private final boolean gamePaused;
    /**
     * Data about players in the game. Contains the scores of the players in the game.
     * The map is sorted in descending order, so the first entry is the winner.
     * The key is the score, the value is the username. Always contains scoring tokens in possession of each player.
     */
    private final List<PlayerInfo> playersData;

    public GameView(Game game, int receivingPlayerIndex){
        this.myIndex = receivingPlayerIndex;

        this.livingRoomBoardMatrix = game.getLivingRoomBoard().getMatrix();
        this.bookshelfMatrix = game.getBookshelves()[this.myIndex].getMatrix();
        this.personalGoalCardMatrix = game.getBookshelves()[this.myIndex].getPersonalGoalCard().getMatrix();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.currentPlayerUsername = game.getCurrentPlayer().getUsername();
        this.firstPlayerUsername = game.getBookshelves()[game.getFirstPlayerIndex()].getPlayer().getUsername();
        if(game.getFinalPlayerIndex() == -1)
            this.finalPlayerUsername = null;
        else{ //Player who plays last has an index of 1 less than the one who filled the bookshelf first
            int temp = game.getFinalPlayerIndex() - 1;
            if(temp == -1)
                temp = game.getTotalPlayersNumber() - 1;
            this.finalPlayerUsername = game.getPlayers().get(temp).getUsername();
        }
        this.gameFinished = game.isFinished();
        this.gamePaused = game.isPaused();
        this.cgcData = Arrays.stream(game.getCommonGoalCards())
                .map(card -> new CGCData(card.getDescription(), card.getAvailableScores()))
                .toList();
        this.playersData = game.getPlayerInfo();
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

    @Deprecated
    public boolean isMyTurn(){
        return this.currentPlayerIndex == this.myIndex;
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
    public boolean isGameFinished() {
        return gameFinished;
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
}