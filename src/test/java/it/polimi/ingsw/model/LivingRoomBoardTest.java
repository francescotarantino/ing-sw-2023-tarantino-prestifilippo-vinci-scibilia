package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LivingRoomBoardTest {
    int[][] invalidPositionsTwoPlayers = {{0, 0},{0, 1},{0, 2},{0, 5},{0, 6},{0, 7},{0, 8},{1, 0},{1, 1},{1, 2},{1, 6},{1, 7},{1, 8},{2, 0},{2, 1},{2, 7},{2, 8},{3, 0},{5, 8},{6, 0},{6, 1},{6, 7},{6, 8},{7, 0},{7, 1},{7, 2},{7, 6},{7, 7},{7, 8},{8, 0},{8, 1},{8, 2},{8, 3},{8, 6},{8, 7},{8, 8},{0, 4},{1, 5},{3, 1},{4, 0},{4, 8},{5, 7},{7, 3},{8, 4},{0, 3},{2, 2},{2, 6},{3, 8},{5, 0},{6, 2},{6, 6},{8, 5}};

    int[][] invalidPositionsThreePlayers = {{0, 0},{0, 1},{0, 2},{0, 5},{0, 6},{0, 7},{0, 8},{1, 0},{1, 1},{1, 2},{1, 6},{1, 7},{1, 8},{2, 0},{2, 1},{2, 7},{2, 8},{3, 0},{5, 8},{6, 0},{6, 1},{6, 7},{6, 8},{7, 0},{7, 1},{7, 2},{7, 6},{7, 7},{7, 8},{8, 0},{8, 1},{8, 2},{8, 3},{8, 6},{8, 7},{8, 8},{0, 4},{1, 5},{3, 1},{4, 0},{4, 8},{5, 7},{7, 3},{8, 4}};

    int[][] invalidPositionsFourPlayers = {{0, 0},{0, 1},{0, 2},{0, 5},{0, 6},{0, 7},{0, 8},{1, 0},{1, 1},{1, 2},{1, 6},{1, 7},{1, 8},{2, 0},{2, 1},{2, 7},{2, 8},{3, 0},{5, 8},{6, 0},{6, 1},{6, 7},{6, 8},{7, 0},{7, 1},{7, 2},{7, 6},{7, 7},{7, 8},{8, 0},{8, 1},{8, 2},{8, 3},{8, 6},{8, 7},{8, 8}};

    Tile t = new Tile(Constants.TileType.CATS);

    @Test
    void checkInsertionInBoardForTwo() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(2);

        for (int i = 0; i < invalidPositionsTwoPlayers.length; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.insertTile(t, new Point(invalidPositionsTwoPlayers[finalI][0], invalidPositionsTwoPlayers[finalI][1])));
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsTwoPlayers[i][0], invalidPositionsTwoPlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }

    @Test
    void checkInsertionInBoardForThree() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(3);

        for (int i = 0; i < invalidPositionsThreePlayers.length; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.insertTile(t, new Point(invalidPositionsThreePlayers[finalI][0], invalidPositionsThreePlayers[finalI][1])));
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsThreePlayers[i][0], invalidPositionsThreePlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }

    @Test
    void checkInsertionInBoardForFour() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(4);

        for (int i = 0; i < invalidPositionsFourPlayers.length; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.insertTile(t, new Point(invalidPositionsFourPlayers[finalI][0], invalidPositionsFourPlayers[finalI][1])));
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsFourPlayers[i][0], invalidPositionsFourPlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }

    @Test
    void checkDeletionInBoardForTwo() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(2);

        for (int i = 0; i < invalidPositionsTwoPlayers.length; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.removeTile(new Point(invalidPositionsTwoPlayers[finalI][0], invalidPositionsTwoPlayers[finalI][1])));
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsTwoPlayers[i][0], invalidPositionsTwoPlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }

    @Test
    void checkDeletionInBoardForThree() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(3);

        for (int i = 0; i < invalidPositionsThreePlayers.length; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.removeTile(new Point(invalidPositionsThreePlayers[finalI][0], invalidPositionsThreePlayers[finalI][1])));
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsThreePlayers[i][0], invalidPositionsThreePlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }

    @Test
    void checkDeletionInBoardForFour() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(4);

        for (int i = 0; i < invalidPositionsFourPlayers.length; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.removeTile(new Point(invalidPositionsFourPlayers[finalI][0], invalidPositionsFourPlayers[finalI][1])));
            assertEquals(livingRoomBoard.getTile(new Point(invalidPositionsFourPlayers[i][0], invalidPositionsFourPlayers[i][1])), new Tile(Constants.TileType.PLACEHOLDER, 0));
        }
    }
    @Test
    void checkBoundaries() {
        LivingRoomBoard livingRoomBoard = new LivingRoomBoard(2);
        assertThrows(IllegalArgumentException.class, () -> new LivingRoomBoard(6));
        Tile t1 = new Tile(Constants.TileType.PLACEHOLDER);
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(-1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(0, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(-1, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(12, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(-1, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(12, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(12, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.insertTile(t, new Point(0, 12)));
        assertThrows(IllegalArgumentException.class, () -> livingRoomBoard.insertTile(t1, new Point(5, 6)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(-1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(0, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(-1, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(12, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(-1, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(12, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(12, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.getTile(new Point(0, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(-1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(0, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(-1, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(12, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(-1, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(12, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(12, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.removeTile(new Point(0, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(-1, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(0, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(-1, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(12, -1)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(-1, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(12, 12)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(12, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> livingRoomBoard.checkIsolatedTile(new Point(0, 12)));
    }
}