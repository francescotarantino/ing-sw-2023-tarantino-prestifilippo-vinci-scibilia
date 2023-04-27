package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.goal_cards.CommonGoalCard;

import static it.polimi.ingsw.Constants.getAdjacentTilesPoints;
import static it.polimi.ingsw.Utils.checkIfTilesCanBeTaken;
import static it.polimi.ingsw.Utils.checkIfColumnHasEnoughSpace;

public class Controller {
    private final Game game;

    public Controller(Game game){
        this.game = game;
    }

    /**
     * This method is called to start the game.
     */
    public void start(){
        // Populating the LivingRoomBoard
        this.fillLivingRoomBoard(this.game.getLivingRoomBoard(), this.game.getBag());
        // Setting the current player to the first one that as to make a move
        this.game.setCurrentPlayerIndex(this.game.getFirstPlayerIndex());
    }

    /**
     * This method takes the tiles from the living room board, it puts them in the player's bookshelf,
     * and it passes the turn to the next player.
     * @param column is the column where the player wants to insert the tiles
     * @param points is an array of points from which the player wants to take the tiles
     */
    public void performTurn(int column, Point...points){
        if(column < 0 || column > Constants.bookshelfX)
            throw new IndexOutOfBoundsException("Invalid column.");
        if (points.length > Constants.maxPick || points.length == 0)
            throw new IllegalArgumentException("Invalid number of tiles.");
        if(!checkIfTilesCanBeTaken(this.game.getLivingRoomBoard().getMatrix(), points))
            throw new IllegalArgumentException("Provided tiles can't be taken.");
        if(!checkIfColumnHasEnoughSpace(this.game.getBookshelves()[this.game.getCurrentPlayerIndex()].getMatrix(), column, points.length))
            throw new IllegalArgumentException("Provided column doesn't have enough space.");

        this.insertTiles(this.game.getBookshelves()[this.game.getCurrentPlayerIndex()], column, this.takeTiles(points));

        this.nextTurn();
    }

    /**
     * This method is called at the end of each turn.
     */
    private void nextTurn() {
        if (checkBoardNeedRefill(this.game.getLivingRoomBoard())) {
            this.fillLivingRoomBoard(this.game.getLivingRoomBoard(), this.game.getBag());
        }

        // Check if the current player has achieved common goals
        this.checkCommonGoal(this.game.getCurrentPlayer(), this.game.getBookshelves()[this.game.getCurrentPlayerIndex()], this.game.getCommonGoalCards());

        // Check if the current player has completed the bookshelf
        if(this.game.getBookshelves()[this.game.getCurrentPlayerIndex()].isFull() && this.game.getFinalPlayerIndex() == -1){
            this.game.setFinalPlayerIndex(this.game.getCurrentPlayerIndex());
        }

        if(
                this.game.getFinalPlayerIndex() != -1 &&
                ((this.game.getCurrentPlayerIndex() + 1) % this.game.getTotalPlayersNumber()) == this.game.getFinalPlayerIndex()
        ){
            this.endGame();
        } else {
            // Changing the current player to the next one
            this.nextPlayer();
        }
    }

    /**
     * This method removes the tiles from the living room board, returning them.
     * @param points is an array of points from which the player wants to take the tiles
     * @return the tiles taken from the living room board
     */
    private Tile[] takeTiles(Point...points) {
        Tile[] tiles = new Tile[points.length];
        for (int i = 0; i < points.length; i++) {
            tiles[i] = game.getLivingRoomBoard().getTile(points[i]);
            game.getLivingRoomBoard().removeTile(points[i]);
        }
        return tiles;
    }

    /**
     * This method insert the given tiles into the current player's bookshelf at the specified column,
     * it is taken for granted that the column has enough space.
     * @param bookshelf the player's bookshelf
     * @param column the column where the player wants to insert the tiles
     * @param tiles the tiles to insert
     */
    private void insertTiles(Bookshelf bookshelf, int column, Tile[] tiles){
        int freePosition = Constants.bookshelfY;

        while(bookshelf.getTile(new Point(column, freePosition - 1)) == null) {
            freePosition--;
        }

        for (Tile tile : tiles) {
            bookshelf.insertTile(new Point(column, freePosition), tile);
            freePosition++;
        }
    }

    /**
     * This method verifies if the current player has achieved the common goals, and then updates tokens accordingly.
     * If the common goal is already achieved, it is not checked again.
     * @param player the player who's getting the points
     * @param bookshelf the bookshelf to check
     * @param commonGoalCards the game's CGCs
     */
    private void checkCommonGoal(Player player, Bookshelf bookshelf, CommonGoalCard[] commonGoalCards){
        for(int i = 0; i < commonGoalCards.length; i++){
            if(!bookshelf.isCommonGoalCardCompleted(i)){
                player.addScoringToken(
                        commonGoalCards[i].checkValidity(bookshelf.getMatrix())
                );

                bookshelf.setCommonGoalCardCompleted(i);
            }
        }
    }

    /**
     * This method fills the LivingRoomBoard with tiles from the bag.
     * @param livingRoomBoard the living room board to fill
     * @param bag the bag from where the tiles are taken
     */
    private void fillLivingRoomBoard(LivingRoomBoard livingRoomBoard, Bag bag){
        for (int i = 0; i < Constants.livingRoomBoardX; i++) {
            for (int j = 0; j < Constants.livingRoomBoardY; j++) {
                if (livingRoomBoard.getTile(new Point(i, j)) == null) {
                    livingRoomBoard.insertTile(bag.getRandomTile(), new Point(i, j));
                }
            }
        }
    }

    /**
     * This method changes the current player to the next one
     */
    private void nextPlayer(){
        this.game.setCurrentPlayerIndex((this.game.getCurrentPlayerIndex() + 1) % this.game.getTotalPlayersNumber());
    }

    /**
     * This method checks if a refill of the living room is needed.
     * If there are only tiles without any other adjacent tile, a refill is needed.
     * @return true if a refill is needed, false otherwise
     */
    private boolean checkBoardNeedRefill(LivingRoomBoard livingRoomBoard) {
        for (int i = 0; i < Constants.livingRoomBoardX; i++) {
            for (int j = 0; j < Constants.livingRoomBoardY; j++) {
                Tile currentTile = livingRoomBoard.getTile(new Point(i, j));
                if (
                        currentTile != null &&
                        !currentTile.isPlaceholder() &&
                        !livingRoomBoard.checkIsolatedTile(new Point(i, j), livingRoomBoard.getMatrix())
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method adds to the player's total points the ones scored with tokens,
     * aka the ones coming from the common goals and the bookshelf's completion.
     * @param player the player who's getting the points
     */
    private void addTokenPoints(Player player) {
        while(!player.getScoringTokens().isEmpty()){
            for (int j = 0; j < player.getScoringTokens().size(); j++) {
                player.addPoints(player.getScoringTokens().get(j));
            }
        }
    }

    /**
     * This method add to the player's total points the ones from his personal goal.
     * @param player the player who's getting the points
     * @param bookshelf the player's bookshelf
     */
    private void addPGCPoints(Player player, Bookshelf bookshelf) {
        player.addPoints(bookshelf.getPersonalGoalCard().checkValidity(bookshelf.getMatrix()));
    }

    /**
     * Adds the points scored by the player by creating clusters of tiles
     */
    private void addAdjacentObjectTilesPoints(Player player, Tile[][] matrix) {
        boolean[][] done = new boolean[Constants.bookshelfX][Constants.bookshelfY];
        for (int i = 0; i < Constants.bookshelfX; i++) {
            for (int j = 0; j < Constants.bookshelfY; j++) {
                if(!done[i][j]) {
                    int groupSize = Utils.findGroup(new Point(i, j), matrix, done);
                    if(groupSize >= 3) {
                        player.addPoints(getAdjacentTilesPoints(groupSize));
                    }
                }
            }
        }
    }

    /**
     * This method assigns points to the players according to the rules of the game.
     */
    private void assignPoints() {
        for(int i = 0; i < game.getTotalPlayersNumber(); i++){
            addTokenPoints(this.game.getPlayer(i));
            addPGCPoints(this.game.getPlayer(i), this.game.getBookshelves()[i]);
            addAdjacentObjectTilesPoints(this.game.getPlayer(i), this.game.getBookshelves()[i].getMatrix());
        }
    }

    /**
     * This method is called to end a game.
     */
    private void endGame(){
        assignPoints();
        // TODO: implement other aspects related to the conclusion of a game
    }
}
