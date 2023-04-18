package it.polimi.ingsw.model;

import java.io.Serializable;

public class GameView implements Serializable {
    static final long serialVersionUID = 1L;
    private final LivingRoomBoard board;
    private final Bookshelf[] bookshelves;

    public GameView(Game game){
        this.board = game.getLivingRoomBoard();
        this.bookshelves = game.getBookshelves();
    }

    public Bookshelf[] getBookshelves() {
        return bookshelves;
    }

    public LivingRoomBoard getBoard() {
        return board;
    }
}