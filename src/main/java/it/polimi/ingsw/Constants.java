package it.polimi.ingsw;

import it.polimi.ingsw.model.Point;

import java.util.*;

public class Constants {
    // Bookshelf size
    public static final int bookshelfX = 5;
    public static final int bookshelfY = 6;

    /*constants concerning LivingRoomBoard*/

    public static final int livingRoomBoardSize[] = {9, 9};

    public static final Set getInvalidPositions(int numPlayers) {

        Set<Point> invalidPositions = new HashSet<Point>();

        invalidPositions.add(new Point(0, 0));
        invalidPositions.add(new Point(0, 1));
        invalidPositions.add(new Point(0, 2));
        invalidPositions.add(new Point(0, 5));
        invalidPositions.add(new Point(0, 6));
        invalidPositions.add(new Point(0, 7));
        invalidPositions.add(new Point(0, 8));
        invalidPositions.add(new Point(1, 0));
        invalidPositions.add(new Point(1, 1));
        invalidPositions.add(new Point(1, 2));
        invalidPositions.add(new Point(1, 6));
        invalidPositions.add(new Point(1, 7));
        invalidPositions.add(new Point(1, 8));
        invalidPositions.add(new Point(2, 0));
        invalidPositions.add(new Point(2, 1));
        invalidPositions.add(new Point(2, 7));
        invalidPositions.add(new Point(2, 8));
        invalidPositions.add(new Point(3, 0));
        invalidPositions.add(new Point(5, 8));
        invalidPositions.add(new Point(6, 0));
        invalidPositions.add(new Point(6, 1));
        invalidPositions.add(new Point(6, 7));
        invalidPositions.add(new Point(6, 8));
        invalidPositions.add(new Point(7, 0));
        invalidPositions.add(new Point(7, 1));
        invalidPositions.add(new Point(7, 2));
        invalidPositions.add(new Point(7, 6));
        invalidPositions.add(new Point(7, 7));
        invalidPositions.add(new Point(7, 8));
        invalidPositions.add(new Point(8, 0));
        invalidPositions.add(new Point(8, 1));
        invalidPositions.add(new Point(8, 2));
        invalidPositions.add(new Point(8, 3));
        invalidPositions.add(new Point(8, 6));
        invalidPositions.add(new Point(8, 7));
        invalidPositions.add(new Point(8, 8));

        if (numPlayers < playersUpperBound) {
            invalidPositions.add(new Point(0, 4));
            invalidPositions.add(new Point(1, 5));
            invalidPositions.add(new Point(3, 2));
            invalidPositions.add(new Point(4, 0));
            invalidPositions.add(new Point(4, 8));
            invalidPositions.add(new Point(5, 7));
            invalidPositions.add(new Point(7, 3));
            invalidPositions.add(new Point(8, 4));
        }
        if (numPlayers < playersUpperBound - 1) {
            invalidPositions.add(new Point(0, 3));
            invalidPositions.add(new Point(2, 2));
            invalidPositions.add(new Point(2, 6));
            invalidPositions.add(new Point(3, 8));
            invalidPositions.add(new Point(5, 0));
            invalidPositions.add(new Point(6, 2));
            invalidPositions.add(new Point(6, 6));
            invalidPositions.add(new Point(8, 5));
        }
        return invalidPositions;
    }

    //constants relative to class Game:
    //to remove public static final int uniquePersonalGoalCards = 12;
    public static final int playersLowerBound = 2;
    public static final int playersUpperBound = 4;
    public static final int IDLowerBound = 0;

    //constants relative to class Bag:
    public static final int bagCapacity = 132;

    //enumeration of all the possible Tile Types:
    public enum TileType {
        CATS,
        BOOKS,
        GAMES,
        FRAMES,
        TROPHIES,
        PLANTS,
        PLACEHOLDER
    }
}
