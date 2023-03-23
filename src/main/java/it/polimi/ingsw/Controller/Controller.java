package it.polimi.ingsw.Controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.LivingRoomBoard;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.exceptions.IllegalPositionException;

public class Controller {
    //private View view;        TODO: to be implemented

    private Game game;


    public Controller(Game game /* , View view */){
        this.game = game;
        // this.view = view
    }


    public void start(){
        //TODO
        game.ModifiescurrentPlayer(0);
    };

    public void nextTurn(){

        //control if refilling the living room is needed


        //control if player have achieved the requirements of a common goal card ----> common goal card implementation is needed

        //control if a player has filled their bookshelf


        if(game.getCurrentPlayer()  < game.getBookshelf().length)
                game.ModifiescurrentPlayer(game.getCurrentPlayer() + 1 );
        else game.ModifiescurrentPlayer(0);
    };
        //TODO: to be implemented controls on insertion/removing of tiles
    public Tile[] takeTiles(Point...points) throws IllegalPositionException {
        Tile[] tiles = new Tile[points.length];
        for (int i = 0; i < points.length; i++) {
            tiles[i] = game.getLivingRoomBoard().getTile(points[i]);
            game.getLivingRoomBoard().removeTile(points[i]);
        }
        return tiles;
    };

    public void insertTiles( int column, Tile[] tiles){

    };

    public void checkCommonGoal(){

    };


}
