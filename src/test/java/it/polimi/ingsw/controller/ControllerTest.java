package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    private static Controller controller;
    private static Game game;
    @BeforeEach
    void initializing(){
        game = new Game(111,2,new Player("playerOne"),2);
        controller = new Controller(game);
        controller.addPlayer("playerTwo");
        controller.start();
    }
    @Test
    void checkStart(){
       assertEquals(game.getCurrentPlayerIndex() , game.getFirstPlayerIndex());
    }

    @Test
    void checkStartIfFull(){
        Game gameNotFull = new Game(222,3,new Player("playerOne"),2);
        Controller controllerGNF = new Controller(gameNotFull);
        controllerGNF.addPlayer("playerTwo");
        controllerGNF.startIfFull();
        assertNotEquals(gameNotFull.getCurrentPlayerIndex() , gameNotFull.getFirstPlayerIndex());
    }
    @Test
    void checkPerformTurn(){
        //Checks if exceptions is thrown when column index assumes an illegal value
        assertThrows(IndexOutOfBoundsException.class, () -> {
            controller.performTurn( -1, new Point(1,2));
        });
        //Checks if exceptions is thrown when column index assumes an illegal value
        assertThrows(IndexOutOfBoundsException.class, () -> {
            controller.performTurn( 10, new Point(0,0));
        });
        //Checks if the method performTurn correctly throws an exception when trying to insert no tiles
        assertThrows(IllegalArgumentException.class, () -> {
            Point[] emptyArray = {};
            controller.performTurn( 2, emptyArray);
        });
        //Checks if the method performTurn correctly throws an exception when trying to insert more tiles than allowed
        Point[] fourPointsArray = {new Point(1,2), new Point(3,4), new Point(6,7),new Point(3,2)};
        assertThrows(IllegalArgumentException.class, () -> {
            controller.performTurn( 2, fourPointsArray);
        });
        //Checks if the method performTurn correctly throws an exception when trying to take an illegal tile
        for (Point point : Constants.getInvalidPositions(2)) {
            Point[] invalidPosition = {point};
            assertThrows(IllegalArgumentException.class, () -> {
                controller.performTurn(3, invalidPosition);
            });
        }
        /* Performs two turns for each player, testing the regular flow of the game,
         * whilst filling up the bookshelves for the next test*/
        //First turn for the first player
        Point[] firstPlayerFirstChoice = {new Point(4,1), new Point(5,1)};
        controller.performTurn( 1, firstPlayerFirstChoice);
        //First turn for the second player
        Point[] secondPlayerFirstChoice = {new Point(3,7), new Point(4,7)};
        controller.performTurn( 1, secondPlayerFirstChoice);
        //Second turn for the first player
        Point[] firstPlayerSecondChoice = {new Point(3,2), new Point(4,2), new Point(5,2)};
        controller.performTurn( 1, firstPlayerSecondChoice);
        //Second turn for the second player
        Point[] secondPlayerSecondChoice = {new Point(3,6), new Point(4,6), new Point(5,6)};
        controller.performTurn( 1, secondPlayerSecondChoice);
        //Third turn for the first player, tries to take three tiles and insert them in a column with only one empty space
        assertThrows(IllegalArgumentException.class, () -> {
            controller.performTurn( 1, new Point(4,6));
        });
    }
    @Test
    void testCheckBoardNeedRefill1() {
        controller.fillLivingRoomBoard(game.getLivingRoomBoard(), game.getBag());
        assertFalse(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
        Point[] selection = {   //this selection shouldn't trigger the refill
                new Point(5, 1),
                new Point(4, 2),
                new Point(1, 3),
                new Point(3, 3),
                new Point(5, 3),
                new Point(2, 4),
                new Point(6, 4),
                new Point(3, 5),
                new Point(5, 5),
                new Point(7, 5),
                new Point(4, 6),
                new Point(3, 7)
        };
        controller.takeTiles(selection);
        assertFalse(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
        controller.takeTiles(new Point(4, 4)); //the additional point should trigger the refill
        assertTrue(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
    }

    @Test
    void checkWalkover(){
        controller.walkover();
        assertTrue(game.isWalkover());
        assertTrue(game.isEnded());
    }

    @Test
    void checkHandlePlayerDisconnection(){
        controller.handlePlayerDisconnection(1);
        List<Player> connectedPlayers = game.getPlayers().stream().filter(Player::isConnected).toList();
        assertFalse(connectedPlayers.contains(new Player("playerTwo")));
    }

    @Test
    void checkReconnectPlayer(){
        controller.handlePlayerDisconnection(1);
        controller.reconnectPlayer("playerTwo");
        List<Player> connectedPlayers = game.getPlayers().stream().filter(Player::isConnected).toList();
        assertTrue(connectedPlayers.contains(game.getPlayer(1)));
    }

    @Test
    void checkHasPlayerDisconnected(){
        controller.handlePlayerDisconnection(1);
        assertTrue(controller.hasPlayerDisconnected("playerTwo"));
    }
   @Test
   void testCheckBoardNeedRefill2() {
       controller.fillLivingRoomBoard(game.getLivingRoomBoard(), game.getBag());
       assertFalse(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
       Point[] selection = {   //this selection shouldn't trigger the refill
               new Point(2, 3),
               new Point(3, 3),
               new Point(4, 3),
               new Point(1, 4),
               new Point(2, 4),
               new Point(3, 4),
               new Point(4, 4),
               new Point(5, 4),
               new Point(6, 4),
               new Point(2, 5),
               new Point(3, 5),
               new Point(4, 5),
               new Point(5, 5),
               new Point(6, 5),
               new Point(7, 5),
               new Point(3, 6),
               new Point(4, 6),
               new Point(5, 6),
               new Point(3, 7),
               new Point(4, 7)
       };
       controller.takeTiles(selection);
       assertFalse(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
       controller.takeTiles(new Point(5, 1));
       assertFalse(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
       controller.takeTiles(new Point(4, 2));
       assertFalse(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
       controller.takeTiles(new Point(5, 3));
       assertTrue(controller.checkBoardNeedRefill(game.getLivingRoomBoard()));
   }
    @Test
    void checkPoints() {
        game.getPlayer(0).addScoringToken(8, 2);
        game.getPlayer(1).addScoringToken(8, 2);
        game.getBookshelves()[0].insertTile(new Point(0, 0), new Tile(Constants.TileType.PLANTS));
        game.getBookshelves()[0].insertTile(new Point(0, 1), new Tile(Constants.TileType.PLANTS));
        game.getBookshelves()[0].insertTile(new Point(1, 0), new Tile(Constants.TileType.PLANTS));
        game.getBookshelves()[0].insertTile(new Point(2, 0), new Tile(Constants.TileType.PLANTS));
        game.getBookshelves()[1].insertTile(new Point(0, 0), new Tile(Constants.TileType.CATS));
        game.getBookshelves()[1].insertTile(new Point(0, 1), new Tile(Constants.TileType.CATS));
        game.getBookshelves()[1].insertTile(new Point(1, 0), new Tile(Constants.TileType.CATS));
        game.getBookshelves()[1].insertTile(new Point(2, 0), new Tile(Constants.TileType.CATS));
        game.getBookshelves()[1].insertTile(new Point(0, 2), new Tile(Constants.TileType.CATS));
        int playerOnePGCPoints = game.getBookshelves()[0].getPersonalGoalCard().checkValidity(game.getBookshelves()[0].getMatrix());
        int playerTwoPGCPoints = game.getBookshelves()[1].getPersonalGoalCard().checkValidity(game.getBookshelves()[1].getMatrix());
        controller.endGame();
        assertEquals(11 + playerOnePGCPoints, game.getPlayer(0).getPoints());
        assertEquals(13 + playerTwoPGCPoints, game.getPlayer(1).getPoints());
        assertTrue(game.isEnded());
    }
}