package it.polimi.ingsw;

public class Constants {
    // Bookshelf size
    public static final int bookshelfX = 5;
    public static final int bookshelfY = 6;

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
