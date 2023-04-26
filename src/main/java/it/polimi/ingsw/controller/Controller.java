package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.model.*;

import java.util.Arrays;

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
     * @return
     */
    public void performTurn(int column, Point...points){
        if(column < 0 || column > Constants.bookshelfX)
            throw new IndexOutOfBoundsException("Invalid column.");
        if (points.length > Constants.maxPick || points.length == 0)
            throw new IllegalArgumentException("Invalid number of tiles.");
        if(!checkIfTilesCanBeTaken(points))
            throw new IllegalArgumentException("Provided tiles can't be taken.");
        if(!checkIfColumnHasEnoughSpace(column, points.length))
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

    private boolean checkIfTilesCanBeTaken(Point...points){
        //checks if tiles are adjacent
        if (
                points.length != 1 &&
                        (
                                (!(points[0].getX() == points[1].getX() && checkContiguity(Point::getY, points))) ||
                                (!(points[0].getY() == points[1].getY() && checkContiguity(Point::getX, points)))
                        )
        )
            return false;
        
        //checks if tiles have at least one free side
        for(Point point : points){
            boolean flag = false;

            if(point.getX() != 0){
                if(game.getLivingRoomBoard().getTile(new Point(point.getX() - 1, point.getY())) == null ||
                    game.getLivingRoomBoard().getTile(new Point(point.getX() - 1, point.getY())).getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if(point.getX() != Constants.livingRoomBoardX - 1){
                if(game.getLivingRoomBoard().getTile(new Point(point.getX() + 1, point.getY())) == null ||
                        game.getLivingRoomBoard().getTile(new Point(point.getX() + 1, point.getY())).getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if(point.getY() != 0){
                if(game.getLivingRoomBoard().getTile(new Point(point.getX(), point.getY() - 1)) == null ||
                        game.getLivingRoomBoard().getTile(new Point(point.getX(), point.getY() - 1)).getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if(point.getY() != Constants.livingRoomBoardY - 1){
                if(game.getLivingRoomBoard().getTile(new Point(point.getX(), point.getY() + 1)) == null ||
                        game.getLivingRoomBoard().getTile(new Point(point.getX(), point.getY() + 1)).getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if(!flag) return false;
        }

        return true;
    }

    private boolean checkContiguity(java.util.function.ToIntFunction<Point> lambda, Point...points){
        int[] tmp = Arrays.stream(points)
                .mapToInt(lambda)
                .sorted()
                .toArray();
        for(int i = 1; i < tmp.length; i++){
            if(tmp[i] != (tmp[i-1] + 1))
                return false;
        }
        return true;
    }

    private boolean checkIfColumnHasEnoughSpace(int column, int tilesNum){
        Tile[][] tempMatrix = this.game.getBookshelves()[this.game.getCurrentPlayerIndex()].getMatrix();
        int counter = 0;
        for(int i = Constants.bookshelfY; i >= 0; i--){
            if(tempMatrix[column][i] == null){
                counter++;
            } else break;
        }
        return counter >= tilesNum;
    }

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
        int freeposition = Constants.bookshelfX - 1; //using X as an Y for now
        while( playersBookshelf[freeposition][column] != null) freeposition--;
        int tilesindex = 0;
        /*if( freeposition < tiles.length) throw  IndexOutOfBoundsException; // TODO: discuss about exceptions in controller
        else for( int i = 0; i < tiles.length; i++){
                game.getBookshelves()[game.getCurrentPlayerIndex()].insertTile(new Point(freeposition,column),tiles[tilesindex]);
                freeposition--;
                tilesindex++;
            }
*/
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
        int numTile = 0;
        int onlyTile = 0;

        for (int i = 0; i < Constants.livingRoomBoardY; i++) {
            for (int j = 0; j < Constants.livingRoomBoardX; j++) {
                Tile currentTile = game.getLivingRoomBoard().getTile(new Point(j, i));
                if (currentTile != null && currentTile.getType() != Constants.TileType.PLACEHOLDER) {
                    numTile++;
                    boolean b, c, d1, d2, d3, d4;
                    if (j != Constants.livingRoomBoardX - 1) {
                         b = game.getLivingRoomBoard().getTile(new Point(j + 1, i)).getType() == Constants.TileType.PLACEHOLDER;
                         c = game.getLivingRoomBoard().getTile(new Point(j + 1, i)) == null;
                         d1 = b || c;
                    }else d1 =true;

                    if (j != 0) {
                        b = game.getLivingRoomBoard().getTile(new Point(j - 1, i)).getType() == Constants.TileType.PLACEHOLDER;
                        c = game.getLivingRoomBoard().getTile(new Point(j - 1, i)) == null;
                        d2 = b || c;
                    }else d2=true;

                    if (i != Constants.livingRoomBoardY - 1) {
                        b = game.getLivingRoomBoard().getTile(new Point(j, i + 1)).getType() == Constants.TileType.PLACEHOLDER;
                        c = game.getLivingRoomBoard().getTile(new Point(j, i + 1)) == null;
                        d3 = b || c;
                    }else d3=true;

                    if (i != 0) {
                        b = game.getLivingRoomBoard().getTile(new Point(j, i - 1)).getType() == Constants.TileType.PLACEHOLDER;
                        c = game.getLivingRoomBoard().getTile(new Point(j, i - 1)) == null;
                        d4 = b || c;
                    }else d4=true;

                    if(d1&&d2&&d3&&d4) onlyTile++;

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
                        int adjacentPoints;
                        adjacentPoints = Constants.getAdjacentTilesPoints(sameType);
                        game.getCurrentPlayer().addPoints(adjacentPoints);
                    }
                }
            }

            this.nextPlayer();
        }
    }
}
