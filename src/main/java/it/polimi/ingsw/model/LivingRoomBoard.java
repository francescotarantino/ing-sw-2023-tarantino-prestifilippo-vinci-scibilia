package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.IllegalPositionException;

import it.polimi.ingsw.model.exceptions.IllegalTileException;

import it.polimi.ingsw.Constants;

import java.util.*;


public class LivingRoomBoard {
    private Tile[][] board;
    public Tile getTile(Point p) throws IllegalPositionException{
        if (p.getX() < 0 || p.getY() < 0 || p.getX() > Constants.livingRoomBoardSize[0] - 1 || p.getY() > Constants.livingRoomBoardSize[1] - 1) {
            throw new IndexOutOfBoundsException();
        }
        else if (board[p.getX()][p.getY()].equals(new Tile(Constants.TileType.PLACEHOLDER))) {
            throw new IllegalPositionException();
        }
        return board[p.getX()][p.getY()];
    }
    public void insertTile(Tile t, Point p) throws IllegalPositionException, IllegalTileException{
        if (p.getX() < 0|| p.getY() < 0 || p.getX() > Constants.livingRoomBoardSize[0] - 1 || p.getY() > Constants.livingRoomBoardSize[1] - 1) {
            throw new IndexOutOfBoundsException();
        }
        else if (board[p.getX()][p.getY()] != null) {
            throw new IllegalPositionException();
        }
        else if (t.equals(new Tile(Constants.TileType.PLACEHOLDER))) {
            throw new IllegalTileException();
        }
        board[p.getX()][p.getY()] = t;
    }
    public void removeTile(Point p) throws IllegalPositionException{
        if (p.getX() < 0|| p.getY() < 0 || p.getX() > Constants.livingRoomBoardSize[0] - 1 || p.getY() > Constants.livingRoomBoardSize[1] - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (board[p.getX()][p.getY()].equals(new Tile(Constants.TileType.PLACEHOLDER))) {
            throw new IllegalPositionException();
        }
        board[p.getX()][p.getY()] = null;
    }
    /*creator*/
    public LivingRoomBoard(int numPlayers) {
        if (numPlayers > Constants.playersUpperBound || numPlayers < Constants.playersLowerBound) {
            throw new IndexOutOfBoundsException();
        }
        board = new Tile[Constants.livingRoomBoardSize[0]][Constants.livingRoomBoardSize[1]];
        Set<Point> invalidPositions = new HashSet<Point>();
        invalidPositions = Constants.getInvalidPositions(numPlayers);
        for (Point p : invalidPositions) {
            board[p.getX()][p.getY()] = new Tile(Constants.TileType.PLACEHOLDER);
        }
    }
}