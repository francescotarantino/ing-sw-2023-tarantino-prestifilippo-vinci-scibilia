package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LivingRoomBoardTest {
    @Test
    void checkInsertionInBoardForTwo() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(2);
        Tile t = new Tile(Constants.TileType.CATS, 0);
        int invalidPositionsTwoPlayers[][] = {
            {0, 0},
            {0, 1},
            {0, 2},
            {0, 5},
            {0, 6},
            {0, 7},
            {0, 8},
            {1, 0},
            {1, 1},
            {1, 2},
            {1, 6},
            {1, 7},
            {1, 8},
            {2, 0},
            {2, 1},
            {2, 7},
            {2, 8},
            {3, 0},
            {5, 8},
            {6, 0},
            {6, 1},
            {6, 7},
            {6, 8},
            {7, 0},
            {7, 1},
            {7, 2},
            {7, 6},
            {7, 7},
            {7, 8},
            {8, 0},
            {8, 1},
            {8, 2},
            {8, 3},
            {8, 6},
            {8, 7},
            {8, 8},
            /*extra positions that must be removed just for two or three players game*/
            {0, 4},
            {1, 5},
            {3, 2},
            {4, 0},
            {4, 8},
            {5, 7},
            {7, 3},
            {8, 4},
            /*extra positions that must be removed just for two players game*/
            {0, 3},
            {2, 2},
            {2, 6},
            {3, 8},
            {5, 0},
            {6, 2},
            {6, 6},
            {8, 5}
        };
        for (int i = 0; i < 9; i++) {
            try {
                livingRoomBoard.insertTile(t, new Point(invalidPositionsTwoPlayers[i][0], invalidPositionsTwoPlayers[i][1]));
            } catch (IllegalArgumentException e) {
                fail(e);
            }
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsTwoPlayers[i][0], invalidPositionsTwoPlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }
    @Test
    void checkInsertionInBoardForThree() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(3);
        Tile t = new Tile(Constants.TileType.CATS, 0);
        int invalidPositionsThreePlayers[][] = {
                {0, 0},
                {0, 1},
                {0, 2},
                {0, 5},
                {0, 6},
                {0, 7},
                {0, 8},
                {1, 0},
                {1, 1},
                {1, 2},
                {1, 6},
                {1, 7},
                {1, 8},
                {2, 0},
                {2, 1},
                {2, 7},
                {2, 8},
                {3, 0},
                {5, 8},
                {6, 0},
                {6, 1},
                {6, 7},
                {6, 8},
                {7, 0},
                {7, 1},
                {7, 2},
                {7, 6},
                {7, 7},
                {7, 8},
                {8, 0},
                {8, 1},
                {8, 2},
                {8, 3},
                {8, 6},
                {8, 7},
                {8, 8},
                /*extra positions that must be removed just for two or three players game*/
                {0, 4},
                {1, 5},
                {3, 2},
                {4, 0},
                {4, 8},
                {5, 7},
                {7, 3},
                {8, 4},
        };
        for (int i = 0; i < 9; i++) {
            try {
                livingRoomBoard.insertTile(t, new Point(invalidPositionsThreePlayers[i][0], invalidPositionsThreePlayers[i][1]));
            } catch (IllegalArgumentException e) {
                fail(e);
            }
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsThreePlayers[i][0], invalidPositionsThreePlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }
    @Test
    void checkInsertionInBoardForFour() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(4);
        Tile t = new Tile(Constants.TileType.CATS, 0);
        int invalidPositionsFourPlayers[][] = {
                {0, 0},
                {0, 1},
                {0, 2},
                {0, 5},
                {0, 6},
                {0, 7},
                {0, 8},
                {1, 0},
                {1, 1},
                {1, 2},
                {1, 6},
                {1, 7},
                {1, 8},
                {2, 0},
                {2, 1},
                {2, 7},
                {2, 8},
                {3, 0},
                {5, 8},
                {6, 0},
                {6, 1},
                {6, 7},
                {6, 8},
                {7, 0},
                {7, 1},
                {7, 2},
                {7, 6},
                {7, 7},
                {7, 8},
                {8, 0},
                {8, 1},
                {8, 2},
                {8, 3},
                {8, 6},
                {8, 7},
                {8, 8}
        };
        for (int i = 0; i < 9; i++) {
            try {
                livingRoomBoard.insertTile(t, new Point(invalidPositionsFourPlayers[i][0], invalidPositionsFourPlayers[i][1]));
            } catch (IllegalArgumentException e) {
                fail(e);
            }
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsFourPlayers[i][0], invalidPositionsFourPlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }
}