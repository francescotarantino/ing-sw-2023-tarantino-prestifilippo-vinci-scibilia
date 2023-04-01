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

    public void nextTurn() {

        //controlling if a refill of the living room is needed

        // if there are only tiles without any other adjacent tile

        int numTile = 0;
        int onlyTile = 0;

        for (int i = 0; i < Constants.livingRoomBoardY; i++) {
            for (int j = 0; j < Constants.livingRoomBoardX; j++) {
                Tile CurrentTile = game.getLivingRoomBoard().getTile(new Point(j, i));
                if (CurrentTile.getType() != Constants.TileType.PLACEHOLDER && CurrentTile != null) {
                    numTile++;
                    if (j != Constants.livingRoomBoardX) {
                        boolean b = game.getLivingRoomBoard().getTile(new Point(j + 1, i)).getType() == Constants.TileType.PLACEHOLDER;
                        boolean c = game.getLivingRoomBoard().getTile(new Point(j + 1, i)) == null;
                        if (b&&c) {
                            if (j != 0) {
                                b = game.getLivingRoomBoard().getTile(new Point(j - 1, i)).getType() == Constants.TileType.PLACEHOLDER;
                                c = game.getLivingRoomBoard().getTile(new Point(j - 1, i)) == null;
                                if (b&&c) {
                                    if (i != Constants.livingRoomBoardY) {
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
        if (onlyTile == numTile) { // proceed to empty the livingroomBoard
            for (int i = 0; i < Constants.livingRoomBoardY; i++) {
                for (int j = 0; j < Constants.livingRoomBoardX; j++) {
                    Tile CurrentTile = game.getLivingRoomBoard().getTile(new Point(j, i));
                    if (CurrentTile.getType() != Constants.TileType.PLACEHOLDER && CurrentTile != null) {
                        game.getLivingRoomBoard().removeTile(new Point(j, i));
                        game.getBag().pushTile(CurrentTile);
                    }
                }
            }

            // Populating the LivingRoomBoard
            this.fillLivingRoomBoard();
        }

        //TODO: control if player have achieved the requirements of a common goal card ----> common goal card implementation is needed

        //TODO: control if a player has filled their bookshelf

        // Changing the current player to the next one
        this.nextPlayer();
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

    public void checkCommonGoal(){

    }

    // Private methods

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
}
