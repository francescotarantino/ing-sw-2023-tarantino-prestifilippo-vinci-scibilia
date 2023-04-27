package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.Utils;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.*;

import static it.polimi.ingsw.Constants.getAdjacentTilesPoints;
import static it.polimi.ingsw.Utils.checkIfTilesCanBeTaken;
import static it.polimi.ingsw.Utils.checkIfColumnHasEnoughSpace;

public class Controller {
    private final Client client;
    private final Game game;

    public Controller(Game game, Client client){
        this.game = game;
        this.client = client;
    }

    public void start(){
        //TODO

        // Populating the LivingRoomBoard
        this.fillLivingRoomBoard();

        // Setting the current player to the first one that as to make a move
        this.game.setCurrentPlayerIndex(this.game.getFirstPlayerIndex());
    }

    /**
     * TODO docs
     * @param column
     * @param points
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

        insertTiles(column, takeTiles(points));

        nextTurn();
    }

    /**
     * This method is called at the end of each turn.
     */
    public void nextTurn() {
        // TODO: check if the player is allowed to end the turn

        if (checkBoardNeedRefill()) {
            // Populating the LivingRoomBoard
            this.fillLivingRoomBoard();
        }

        // Check if the current player has achieved common goals
        this.checkCommonGoal();

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

    // Private methods

    private Tile[] takeTiles(Point...points) {
        Tile[] tiles = new Tile[points.length];
        for (int i = 0; i < points.length; i++) {
            tiles[i] = game.getLivingRoomBoard().getTile(points[i]);
            game.getLivingRoomBoard().removeTile(points[i]);
        }
        return tiles;
    }

    private void insertTiles(int column, Tile[] tiles){
        Tile[][] playersBookshelf = game.getBookshelves()[game.getCurrentPlayerIndex()].getMatrix();
        // TODO: revision of indexing policy for bookshelf
        int freePosition = Constants.bookshelfX - 1; //using X as an Y for now
        while( playersBookshelf[freePosition][column] != null) freePosition--;
        int tilesIndex = 0;
        if( freePosition < tiles.length) throw new IndexOutOfBoundsException("Column too short");
        else for( int i = 0; i < tiles.length; i++){
                game.getBookshelves()[game.getCurrentPlayerIndex()].insertTile(new Point(freePosition,column),tiles[tilesIndex]);
                freePosition--;
                tilesIndex++;
            }
    }

    /**
     * This method verifies if the current player has achieved the common goals, and then updates tokens accordingly.
     * If the common goal is already achieved, it is not checked again.
     */
    private void checkCommonGoal(){
        Bookshelf currentBookshelf = this.game.getBookshelves()[this.game.getCurrentPlayerIndex()];

        for(int i = 0; i < this.game.getCommonGoalCards().length; i++){
            if(!currentBookshelf.isCommonGoalCardCompleted(i)){
                this.game.getCurrentPlayer().addScoringToken(
                        this.game.getCommonGoalCards()[i].checkValidity(currentBookshelf.getMatrix())
                );

                currentBookshelf.setCommonGoalCardCompleted(i);
            }
        }
    }

    /**
     * This method fills the LivingRoomBoard with tiles from the bag
     */
    private void fillLivingRoomBoard(){
        for (int i = 0; i < Constants.livingRoomBoardX; i++) {
            for (int j = 0; j < Constants.livingRoomBoardY; j++) {
                if (game.getLivingRoomBoard().getTile(new Point(i, j)) == null) {
                    Tile newtile = game.getBag().getRandomTile();
                    game.getLivingRoomBoard().insertTile(newtile, new Point(i, j));
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
     *
     * @return true if a refill is needed, false otherwise
     */
    private boolean checkBoardNeedRefill() {
        for (int i = 0; i < Constants.livingRoomBoardX; i++) {
            for (int j = 0; j < Constants.livingRoomBoardY; j++) {
                Tile currentTile = game.getLivingRoomBoard().getTile(new Point(i, j));
                if (currentTile != null && currentTile.sameType(new Tile(Constants.TileType.PLACEHOLDER))) {
                    if (!game.getLivingRoomBoard().checkIsolatedTile(i, j, game.getLivingRoomBoard().getMatrix())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private void addTokenPoints(Player player) {
        while(!player.getScoringTokens().isEmpty()){
            for (int j = 0; j < player.getScoringTokens().size(); j++) {
                player.addPoints(player.getScoringTokens().get(j));
            }
        }
    }
    private void addPGCPoints(Player player, Bookshelf bookshelf) {
        int PCGPoints;
        PCGPoints = bookshelf.getPersonalGoalCard().checkValidity(bookshelf.getMatrix());
        player.addPoints(PCGPoints);
    }
    /**
     * adds the points scored by the player by creating clusters of tiles
     */
    private void addAdjacentObjectTilesPoints(Player player, Tile[][] matrix) {
        boolean[][] done = new boolean[Constants.bookshelfX][Constants.bookshelfY];
        for (int i = 0; i < Constants.bookshelfX; i++) {
            for (int j = 0; j < Constants.bookshelfY; j++) {
                if(!done[i][j]) {
                    int groupSize = Utils.findGroup(i, j, matrix, done);
                    if(groupSize >= 3) {
                        player.addPoints(getAdjacentTilesPoints(groupSize));
                    }
                }
            }
        }
    }
    private void assignPoints() {
        for( int i = 0; i < game.getTotalPlayersNumber(); i++){
            addTokenPoints(game.getCurrentPlayer());
            addPGCPoints(game.getCurrentPlayer() , game.getBookshelves()[game.getCurrentPlayerIndex()]);
            addAdjacentObjectTilesPoints(game.getCurrentPlayer(), game.getBookshelves()[game.getCurrentPlayerIndex()].getMatrix());
            this.nextPlayer();
        }
    }
    private void endGame(){
        assignPoints();
        // TODO: implement other aspects related to the conclusion of a game
    }
}
