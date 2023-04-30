package it.polimi.ingsw.viewmodel;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * This class is used to send the current relevant data from model of the game to the client.
 */
public class GameView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The index of the player who is receiving this GameView.
     */
    private final int playerIndex;
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
     * The descriptions of the common goal cards in the game.
     */
    private final List<String> cgcDescriptions;
    /**
     * True if the game is finished, false otherwise.
     */
    private final boolean gameFinished;
    /**
     * True if the game is paused (there is only one player connected), false otherwise.
     */
    private final boolean gamePaused;
    /**
     * The final scores of the players in the game, if the game is finished.
     * The key is the score, the value is the username.
     * The map is sorted in descending order, so the first entry is the winner.
     */
    private final List<PlayerPoints> finalScores;

    public GameView(Game game, int playerIndex){
        this.playerIndex = playerIndex;

        this.livingRoomBoardMatrix = game.getLivingRoomBoard().getMatrix();
        this.bookshelfMatrix = game.getBookshelves()[this.playerIndex].getMatrix();
        this.personalGoalCardMatrix = game.getBookshelves()[this.playerIndex].getPersonalGoalCard().getMatrix();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.currentPlayerUsername = game.getCurrentPlayer().getUsername();
        this.gameFinished = game.isFinished();
        this.gamePaused = game.isPaused();

        this.cgcDescriptions = Arrays.stream(game.getCommonGoalCards())
                .map(CommonGoalCard::getDescription)
                .toList();

        if(this.gameFinished){
            this.finalScores = game.getPlayers()
                    .stream()
                    .sorted(Comparator.comparingInt(Player::getPoints).reversed())
                    .map(p -> new PlayerPoints(p.getUsername(), p.getPoints()))
                    .toList();
        } else {
            finalScores = null;
        }
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

    public boolean isMyTurn(){
        return this.currentPlayerIndex == this.playerIndex;
    }

    public String getCurrentPlayerUsername() {
        return currentPlayerUsername;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public List<PlayerPoints> getFinalScores() {
        return finalScores;
    }

    public List<String> getCGCDescriptions(){
        return cgcDescriptions;
    }

    public Constants.TileType[][] getPersonalGoalCardMatrix() {
        return personalGoalCardMatrix;
    }

    public boolean isGamePaused() {
        return gamePaused;
    }
}