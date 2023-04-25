package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.Arrays;

public class GameView implements Serializable {
    static final long serialVersionUID = 1L;
    private final LivingRoomBoard board;
    private final Tile[][] bookshelfMatrix;
    private final int currentPlayerIndex;
    /**
     * The index of the player who is receiving this GameView.
     */
    private final int playerIndex;
    private final String[] playerUsernames;

    public GameView(Game game, int playerIndex){
        this.playerIndex = playerIndex;

        this.board = game.getLivingRoomBoard(); // TODO: we should consider to make a copy of the board instead of serializing the model
        this.bookshelfMatrix = game.getBookshelves()[this.playerIndex].getMatrix();
        this.currentPlayerIndex = game.getCurrentPlayerIndex();
        this.playerUsernames = Arrays.stream(game.getBookshelves()).map(x -> x.getPlayer().getUsername()).toArray(String[]::new);
    }

    public Tile[][] getBookshelfMatrix() {
        return bookshelfMatrix;
    }

    public LivingRoomBoard getBoard() {
        return board;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isMyTurn(){
        return this.currentPlayerIndex == this.playerIndex;
    }

    public String[] getPlayerUsernames() {
        return playerUsernames;
    }
}