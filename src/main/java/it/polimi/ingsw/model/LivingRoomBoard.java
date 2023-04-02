package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

import java.util.*;

public class LivingRoomBoard {
    private final Tile[][] board;

    /**
     * @param p the coordinates of the tile to be returned
     * @return the tile at the specified coordinates
     */
    public Tile getTile(Point p) {
        if (p.getX() < 0 || p.getY() < 0 || p.getX() > Constants.livingRoomBoardX - 1 || p.getY() > Constants.livingRoomBoardY - 1) {
            throw new IndexOutOfBoundsException("Invalid living room board coordinates");
        }

        return board[p.getX()][p.getY()];
    }

    /**
     * Inserts the specified tile at the specified coordinates.
     * @param t the tile to be inserted
     * @param p the coordinates of the tile to be inserted
     */
    public void insertTile(Tile t, Point p) {
        if (p.getX() < 0|| p.getY() < 0 || p.getX() > Constants.livingRoomBoardX - 1 || p.getY() > Constants.livingRoomBoardY - 1) {
            throw new IndexOutOfBoundsException("Invalid living room board coordinates");
        } else if (board[p.getX()][p.getY()] != null) {
            throw new IllegalArgumentException("There is already a tile in that position");
        } else if (t.getType() == Constants.TileType.PLACEHOLDER) {
            throw new IllegalArgumentException("A placeholder tile cannot be inserted");
        }

        board[p.getX()][p.getY()] = t;
    }

    /**
     * Removes the tile at the specified coordinates.
     * @param p the coordinates of the tile to be removed
     */
    public void removeTile(Point p) {
        if (p.getX() < 0|| p.getY() < 0 || p.getX() > Constants.livingRoomBoardX - 1 || p.getY() > Constants.livingRoomBoardY - 1) {
            throw new IndexOutOfBoundsException("Invalid living room board coordinates");
        } else if (board[p.getX()][p.getY()].getType() == Constants.TileType.PLACEHOLDER) {
            throw new IllegalArgumentException("A placeholder tile cannot be removed");
        }

        board[p.getX()][p.getY()] = null;
    }

    /**
     * Creates a new living room board with the appropriate invalid positions.
     * @param numPlayers the number of players in the game
     */
    public LivingRoomBoard(int numPlayers) {
        if (numPlayers > Constants.playersUpperBound || numPlayers < Constants.playersLowerBound) {
            throw new IndexOutOfBoundsException("Invalid number of players");
        }

        board = new Tile[Constants.livingRoomBoardX][Constants.livingRoomBoardY];
        Set<Point> invalidPositions = Constants.getInvalidPositions(numPlayers);
        for (Point p : invalidPositions) {
            board[p.getX()][p.getY()] = new Tile(Constants.TileType.PLACEHOLDER);
        }
    }
}