package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.*;

public class Controller {
    //private View view; //TODO: to be implemented
    private final Game game;

    public Controller(Game game /* , View view */){
        this.game = game;
        // this.view = view;
    }

    public void start(){
        //TODO

        // Populating the LivingRoomBoard
        this.fillLivingRoomBoard();
    }

    /**
     * This method is called at the end of each turn.
     */
    public void nextTurn() {
        // TODO: check if the player is allowed to end the turn

        if (checkBoardNeedRefill()) {
            // TODO: decide if the LRB should be emptied or not
        /*    // proceed to empty the livingroomBoard
            for (int i = 0; i < Constants.livingRoomBoardY; i++) {
                for (int j = 0; j < Constants.livingRoomBoardX; j++) {
                    Tile currentTile = game.getLivingRoomBoard().getTile(new Point(j, i));
                    if (currentTile != null && currentTile.getType() != Constants.TileType.PLACEHOLDER) {
                        game.getLivingRoomBoard().removeTile(new Point(j, i));
                        game.getBag().pushTile(currentTile);
                    }
                }
            }
        */
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

    //TODO: to be implemented controls on insertion/removing of tiles
    public Tile[] takeTiles(Point...points) {
        Tile[] tiles = new Tile[points.length];
        for (int i = 0; i < points.length; i++) {
            tiles[i] = game.getLivingRoomBoard().getTile(points[i]);
            game.getLivingRoomBoard().removeTile(points[i]);
        }
        return tiles;
    }

    public void insertTiles(int column, Tile[] tiles){

    }

    // Private methods

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
        int numTile = 0;
        int onlyTile = 0;

        for (int i = 0; i < Constants.livingRoomBoardY; i++) {
            for (int j = 0; j < Constants.livingRoomBoardX; j++) {
                Tile currentTile = game.getLivingRoomBoard().getTile(new Point(j, i));
                if (currentTile != null && currentTile.getType() != Constants.TileType.PLACEHOLDER) {
                    numTile++;
                    if (j != Constants.livingRoomBoardX - 1) {
                        boolean b = game.getLivingRoomBoard().getTile(new Point(j + 1, i)).getType() == Constants.TileType.PLACEHOLDER;
                        boolean c = game.getLivingRoomBoard().getTile(new Point(j + 1, i)) == null;
                        if (b&&c) {
                            if (j != 0) {
                                b = game.getLivingRoomBoard().getTile(new Point(j - 1, i)).getType() == Constants.TileType.PLACEHOLDER;
                                c = game.getLivingRoomBoard().getTile(new Point(j - 1, i)) == null;
                                if (b&&c) {
                                    if (i != Constants.livingRoomBoardY - 1) {
                                        b = game.getLivingRoomBoard().getTile(new Point(j, i + 1)).getType() == Constants.TileType.PLACEHOLDER;
                                        c = game.getLivingRoomBoard().getTile(new Point(j , i+1)) == null;
                                        if (b&&c) {
                                            if (i != 0) {
                                                b = game.getLivingRoomBoard().getTile(new Point(j, i - 1)).getType() == Constants.TileType.PLACEHOLDER;
                                                c = game.getLivingRoomBoard().getTile(new Point(j , i-1)) == null;
                                                if (b&&c) {
                                                    onlyTile++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return numTile == onlyTile;
    }

    private void endGame(){
        // TODO: implement end game

        //assigning points to each player
        for( int i = 0; i < game.getTotalPlayersNumber(); i++){
            int j=0;

            //counting for each player point gained with tokens
            while( game.getCurrentPlayer().getScoringTokens().isEmpty() == false ){
                game.getCurrentPlayer().addPoints( game.getCurrentPlayer().getScoringTokens().get(j));
                        game.getCurrentPlayer().getScoringTokens().remove(j);
                        j++;
            }

            //controlling players' personal goal card
           int pcgPoints;
            pcgPoints = game.getBookshelves()[game.getCurrentPlayerIndex()].getPersonalGoalCard().checkValidity( game.getBookshelves()[game.getCurrentPlayerIndex()].getMatrix());
            game.getCurrentPlayer().addPoints(pcgPoints);

            //assigning points for same type adjacent tiles

            Tile [][] playerBookshelf = game.getBookshelves()[game.getCurrentPlayerIndex()].getMatrix();
            boolean [][] checkedTiles = new boolean[0][];

            for(int x = 0; x < Constants.bookshelfX; x++){
                for( int y = 0; y < Constants.bookshelfY; y++){

                    Tile thisTile = playerBookshelf[x][y];
                    int sameType = 0;
                    int confrontingRows = x;
                    int confrontingColumns = y;
                    // controlling if inside the player's bookshelf, once you point a tile you can find others of the same type nearby
                    // looking for a same type tile on the successive row and eventually on all the columns after and before that tile
                    while( (confrontingRows + 1) < Constants.bookshelfX && checkedTiles[ (confrontingRows + 1)][y] != true && playerBookshelf[(confrontingRows + 1)][y].getType() == thisTile.getType() ){
                        sameType++;
                        checkedTiles[(confrontingRows + 1)][y] = true;
                        while( (confrontingColumns + 1) < Constants.bookshelfY && checkedTiles[( confrontingRows + 1)][( confrontingColumns + 1) ] != true && playerBookshelf[(confrontingRows + 1)][(confrontingColumns + 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows + 1)][(confrontingColumns + 1)] = true;
                            confrontingColumns++;
                        }
                        while( (confrontingColumns - 1) >= 0 && checkedTiles[( confrontingRows + 1)][( confrontingColumns - 1) ] != true && playerBookshelf[(confrontingRows + 1)][(confrontingColumns - 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows + 1)][(confrontingColumns - 1)] = true;
                            confrontingColumns--;
                        }

                        confrontingRows++;
                    }
                    // looking for a same type tile on the previous row and eventually on all the columns after and before that tile
                    while( (confrontingRows - 1) >= 0 && checkedTiles[ (confrontingRows - 1)][y] != true && playerBookshelf[(confrontingRows - 1)][y].getType() == thisTile.getType() ){
                        sameType++;
                        checkedTiles[(confrontingRows - 1)][y] = true;
                        while( (confrontingColumns + 1) < Constants.bookshelfY && checkedTiles[( confrontingRows - 1)][( confrontingColumns + 1) ] != true && playerBookshelf[(confrontingRows - 1)][(confrontingColumns + 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows - 1)][(confrontingColumns + 1)] = true;
                            confrontingColumns++;
                        }
                        while( (confrontingColumns - 1) >= 0 && checkedTiles[( confrontingRows - 1)][( confrontingColumns - 1) ] != true && playerBookshelf[(confrontingRows - 1)][(confrontingColumns - 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows - 1)][(confrontingColumns - 1)] = true;
                            confrontingColumns--;
                        }

                        confrontingRows--;
                    }


                    // looking for a same type tile on the successive column and eventually on all the rows after and before that tile
                    confrontingRows = x;
                    confrontingColumns = y;
                    while( (confrontingColumns + 1) < Constants.bookshelfY && checkedTiles[x][(confrontingColumns + 1)] != true && playerBookshelf[x][(confrontingColumns + 1)].getType() == thisTile.getType() ){
                        sameType++;
                        checkedTiles[x][(confrontingColumns + 1)] = true;
                        while( (confrontingRows + 1) < Constants.bookshelfX && checkedTiles[( confrontingRows + 1)][( confrontingColumns + 1) ] != true && playerBookshelf[(confrontingRows + 1)][(confrontingColumns + 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows + 1)][(confrontingColumns + 1)] = true;
                            confrontingRows++;
                        }
                        while( (confrontingRows - 1) >= 0 && checkedTiles[( confrontingRows - 1)][( confrontingColumns + 1) ] != true && playerBookshelf[(confrontingRows - 1)][(confrontingColumns + 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows - 1)][(confrontingColumns + 1)] = true;
                            confrontingRows--;
                        }
                        confrontingColumns++;
                    }

                    // looking for a same type tile on the previous column and eventually on all the rows after and before that tile
                    confrontingRows = x;
                    confrontingColumns = y;
                    while( (confrontingColumns - 1) >= 0 && checkedTiles[x][(confrontingColumns - 1)] != true && playerBookshelf[x][(confrontingColumns - 1)].getType() == thisTile.getType() ){
                        sameType++;
                        checkedTiles[x][(confrontingColumns - 1)] = true;
                        while( (confrontingRows + 1) < Constants.bookshelfX && checkedTiles[( confrontingRows + 1)][( confrontingColumns - 1) ] != true && playerBookshelf[(confrontingRows + 1)][(confrontingColumns - 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows + 1)][(confrontingColumns - 1)] = true;
                            confrontingRows++;
                        }
                        while( (confrontingRows - 1) >= 0 && checkedTiles[( confrontingRows - 1)][( confrontingColumns - 1) ] != true && playerBookshelf[(confrontingRows - 1)][(confrontingColumns - 1)].getType() == thisTile.getType() ){
                            sameType++;
                            checkedTiles[( confrontingRows - 1)][(confrontingColumns - 1)] = true;
                            confrontingRows--;
                        }
                        confrontingColumns--;
                    }
                    if( sameType > 2) {
                        int adjecentPoints;
                        adjecentPoints = Constants.getAdjacentTilesPoints(sameType);
                        game.getCurrentPlayer().addPoints(adjecentPoints);
                    }
                }
            }


            this.nextPlayer();
        }






    }
}
