package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private static Controller controller ;

    @BeforeAll
     static void initializing(){
        controller = new Controller(new Game(111,2,new Player("playerone"),2));
    }




    @Test
    void checkStart(){
       Game game = new Game(111,2,new Player("playerone"),2);
       controller = new Controller(game);
       controller.start();
       int Xboard = (new Random()).nextInt(Constants.livingRoomBoardX - 1);
       int Yboard  = (new Random()).nextInt(Constants.livingRoomBoardY - 1);
        assertNotNull(game.getLivingRoomBoard().getTile(new Point(Xboard, Yboard)));
        game.setFinalPlayerIndex(0);
        assert(game.getPlayers().get(game.getCurrentPlayerIndex()) == game.getPlayers().get(game.getFirstPlayerIndex()));
    }
    @Test
    void checkPerformTurn(){

        //check if exceptions is thrown when column index assume an illegal value
        assertThrows(IndexOutOfBoundsException.class, () -> {
            controller.performTurn( -1, new Point(1,2));
        } );
        assertThrows(IndexOutOfBoundsException.class, () -> {
            controller.performTurn( 10, new Point(0,0));
        } );


        //Checks if the method performTurn correctly throws an exception when trying to insert more tiles than allowed:
        Point[] insertion = { new Point(1,2), new Point(3,4), new Point(6,7),new Point(3,2), new Point(0,0) };
        assertThrows(IllegalArgumentException.class, () -> {
            controller.performTurn( 2, insertion);
        } );

        //Checks if the method performTurn correctly throws an exception when trying to insert less tiles than allowed:
        Point[] insertion1 = { };
        assertThrows(IllegalArgumentException.class, () -> {
            controller.performTurn( 2, insertion1);
        } );

        //Checks if the method performTurn correctly throws an exception when trying to take an illegal tile:
        Point choice = new Point(0,3);
        assertThrows(IllegalArgumentException.class, () -> {
            controller.performTurn( 2, choice);
        } );

        //Checks if the method performTurn correctly throws an exception when trying to insert in a bookshelf an illegal number of tiles:
        Game game = new Game(111,2,new Player("playerone"),2);
        controller = new Controller(game);
        controller.start();
        for(int j = 0; j < Constants.bookshelfY - 1; j++ ){
            game.getBookshelves()[game.getCurrentPlayerIndex()].insertTile(new Point(1,j), new Tile(Constants.TileType.CATS));
        }

        assertThrows(IllegalArgumentException.class, () -> {
            controller.performTurn( 1, insertion);
        } );

    }
}
