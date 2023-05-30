package it.polimi.ingsw.controller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.exception.GameException;
import it.polimi.ingsw.exception.InvalidMoveException;
import it.polimi.ingsw.exception.PreGameException;
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
    void initializing() throws PreGameException {
        game = new Game(111,2,new Player("playerOne"),2);
        controller = new Controller(game);
        assertDoesNotThrow(() -> controller.addPlayer("playerTwo"));
        controller.startIfFull();
    }
    @Test
    void checkStart(){
       assertEquals(game.getCurrentPlayerIndex() , game.getFirstPlayerIndex());
    }

    @Test
    void setErrorTest(){
        controller.setError("test");
        assertEquals("test", game.getErrorMessage());
    }

    @Test
    void checkStartIfFull() throws PreGameException {
        Game gameNotFull = new Game(222,3,new Player("playerOne"),2);
        Controller controllerGNF = new Controller(gameNotFull);
        assertDoesNotThrow(() -> controllerGNF.addPlayer("playerTwo"));
        controllerGNF.startIfFull();
        assertNotEquals(gameNotFull.getCurrentPlayerIndex() , gameNotFull.getFirstPlayerIndex());
    }
    @Test
    void checkPerformTurn() throws GameException {
        //Checks if exception is thrown when column index assumes an illegal value
        assertThrows(InvalidMoveException.class, () -> {
            controller.performTurn( -1, new Point(1,2));
        });
        //Checks if exception is thrown when column index assumes an illegal value
        assertThrows(InvalidMoveException.class, () -> {
            controller.performTurn( 10, new Point(0,0));
        });
        //Checks if the method performTurn correctly throws an exception when trying to insert no tiles
        assertThrows(InvalidMoveException.class, () -> {
            Point[] emptyArray = {};
            controller.performTurn( 2, emptyArray);
        });
        //Checks if the method performTurn correctly throws an exception when trying to insert more tiles than allowed
        Point[] fourPointsArray = {new Point(1,2), new Point(3,4), new Point(6,7),new Point(3,2)};
        assertThrows(InvalidMoveException.class, () -> {
            controller.performTurn( 2, fourPointsArray);
        });
        //Checks if the method performTurn correctly throws an exception when trying to take an illegal tile
        for (Point point : Constants.getInvalidPositions(2)) {
            Point[] invalidPosition = {point};
            assertThrows(InvalidMoveException.class, () -> {
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
        assertThrows(InvalidMoveException.class, () -> {
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
        for(int i = 0; i < Constants.livingRoomBoardX; i++) {
            for(int j = 0; j < Constants.livingRoomBoardY; j++) {
                if(game.getLivingRoomBoard().getTile(new Point(i,j)) != null && !game.getLivingRoomBoard().getTile(new Point(i,j)).isPlaceholder() && !(i == 5 && j == 6)) {
                    game.getLivingRoomBoard().removeTile(new Point(i,j));
                }
            }
        }
        try {
            controller.performTurn(1, new Point(5, 6));
        } catch (InvalidMoveException e) {
            e.printStackTrace();
        }
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
    void testCheckFinalPhases() {
        for (int i = 0; i < Constants.bookshelfX; i++) {
            for (int j = 0; j < Constants.bookshelfY; j++) {
                if (!((i == Constants.bookshelfX - 1 && j == Constants.bookshelfY - 1))) {
                    game.getBookshelves()[game.getCurrentPlayerIndex()].insertTile(new Point(i, j), new Tile(Constants.TileType.PLANTS));
                }
            }
        }
        assertThrows(InvalidMoveException.class, () -> {
            controller.performTurn(0, new Point(5,6));
        });
        game.setPaused(true);
        assertThrows(InvalidMoveException.class, () -> {
            controller.performTurn(Constants.bookshelfX - 1, new Point(5, 6));
        });
        game.setPaused(false);
        try {
            controller.performTurn(Constants.bookshelfX - 1, new Point(5, 6));
        } catch (InvalidMoveException e) {
            e.printStackTrace();
        }
        assertTrue(game.getFinalPlayerIndex() != -1);
        try {
            controller.performTurn(1, new Point(5, 5));
        } catch (InvalidMoveException e) {
            e.printStackTrace();
        }
        assertTrue(game.isEnded());
    }
    @Test
    void checkDisconnectionHandling() throws PreGameException, InvalidMoveException {
        game = new Game(112,3,new Player("playerOne"),2);
        controller = new Controller(game);
        assertDoesNotThrow(() -> controller.addPlayer("playerTwo"));
        assertDoesNotThrow(() -> controller.addPlayer("playerThree"));
        controller.startIfFull();
        int nextPlayer = (game.getCurrentPlayerIndex() + 1) % game.getTotalPlayersNumber();
        String nextPlayerName = game.getPlayer((game.getCurrentPlayerIndex() + 1) % game.getTotalPlayersNumber()).getUsername();
        game.getPlayer(nextPlayer).setConnected(false);
        controller.performTurn(1, new Point(5, 6));
        assertTrue(game.getCurrentPlayerIndex() != nextPlayer);
        String currentPlayerName = game.getPlayer(game.getCurrentPlayerIndex()).getUsername();
        game.getPlayer(game.getCurrentPlayerIndex()).setConnected(false);
        controller.reconnectPlayer(nextPlayerName);
        controller.reconnectPlayer(currentPlayerName);
        for (int i = 0; i < 3; i++) {
            assertTrue(game.getPlayer(i).isConnected());
        }
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