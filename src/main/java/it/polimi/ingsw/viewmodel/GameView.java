package it.polimi.ingsw.viewmodel;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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

    private final List<String> cgcDescriptions = new ArrayList<>();

    private final boolean gameFinished;

    private final Map<String, Integer> finalScores;


    public GameView(Game game, int playerIndex){
        this.playerIndex = playerIndex;

        this.livingRoomBoardMatrix = game.getLivingRoomBoard().getMatrix();
        this.bookshelfMatrix = game.getBookshelves()[this.playerIndex].getMatrix();
        this.personalGoalCardMatrix = game.getBookshelves()[this.playerIndex].getPersonalGoalCard().getMatrix();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.currentPlayerUsername = game.getCurrentPlayer().getUsername();
        this.gameFinished = game.isFinished();

        for (CommonGoalCard commonGoalCard : game.getCommonGoalCards()) {
            cgcDescriptions.add(commonGoalCard.getDescription());
        }

        Map<String, Integer> scoresTmp = new HashMap<>();
        if(this.gameFinished) {
            for(Player player : game.getPlayers()) {
                scoresTmp.put(player.getUsername(), player.getPoints());
            }
        }

        this.finalScores = scoresTmp
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
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

    public Map<String, Integer> getFinalScores() {
        return finalScores;
    }

    public List<String> getCGCDescriptions(){
        return cgcDescriptions;
    }

    public Constants.TileType[][] getPersonalGoalCardMatrix() {
        return personalGoalCardMatrix;
    }
}