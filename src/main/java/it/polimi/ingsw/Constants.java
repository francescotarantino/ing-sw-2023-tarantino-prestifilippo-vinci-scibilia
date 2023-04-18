package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Constants {
    // Bookshelf size
    public static final int bookshelfX = 5;
    public static final int bookshelfY = 6;

    // Constants concerning LivingRoomBoard
    public static final int livingRoomBoardX = 9;
    public static final int livingRoomBoardY = 9;
    public static Set<Point> getInvalidPositions(int numPlayers) {

        Set<Point> invalidPositions = new HashSet<>();

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

    // Constants relative to class Game
    public static final int playersLowerBound = 2;
    public static final int playersUpperBound = 4;
    public static final int IDLowerBound = 0;

    // Constants relative to class Tile
    public static final int tileVariants = 3;
    public static final int tilesPerType = 22;

    // Enumeration of all the possible Tile Types
    public enum TileType {
        CATS,
        BOOKS,
        GAMES,
        FRAMES,
        TROPHIES,
        PLANTS,
        PLACEHOLDER
    }

    // Constants relative to Common Goal Cards
    public static final int minCommonGoalCards = 1;
    public static final int maxCommonGoalCards = 2;
    public static final int totalNumberOfCommonGoalCards = 12;

    // Scoring tokens used in CommonGoalCards
    public static final int[] allScoringTokens = {2, 4, 6, 8};

    // Constants relative to user choices
    public static final int minPick = 1;
    public static final int maxPick = 3;

    /**
     * Returns an array of scoring tokens used in common goal cards based on the number of players
     * @param numberOfPlayers number of players of the game
     * @return array of scoring tokens
     */
    public static int[] getScoringTokens(int numberOfPlayers) {
        if (numberOfPlayers < playersLowerBound || numberOfPlayers > playersUpperBound) {
            throw new IllegalArgumentException("Number of players must be between" + playersLowerBound + " and " + playersUpperBound);
        }

        switch (numberOfPlayers) {
            case 2 -> {
                return new int[]{4, 8};
            }
            case 3 -> {
                return new int[]{4, 6, 8};
            }
            case 4 -> {
                return new int[]{2, 4, 6, 8};
            }
        }

        return new int[]{};
    }

    // Constants relative to Personal Goal Cards
    private static ArrayList<ArrayList<String>> personalGoalCards;
    /**
     * Returns a list of all the Personal Goal Cards from the JSON file
     * @return list of all the Personal Goal Cards
     */
    public static ArrayList<ArrayList<String>> getPersonalGoalCards() {
        if (personalGoalCards != null) {
            return new ArrayList<>(personalGoalCards);
        }

        File file = new File("src/main/resources/PersonalGoalCards.json");
        try {
            personalGoalCards = (ArrayList<ArrayList<String>>) new Gson().fromJson(new FileReader(file), ArrayList.class);
            return new ArrayList<>(personalGoalCards);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the number of points a player gets for a given number of tile matches
     * @param matches number of tile matches
     * @return points
     */
    public static int getPersonalGoalCardPoints(int matches) {
        if (matches < 0) {
            throw new IllegalArgumentException("Number of matches must be positive");
        }

        switch (matches) {
            case 1 -> {
                return 1;
            }
            case 2 -> {
                return 2;
            }
            case 3 -> {
                return 4;
            }
            case 4 -> {
                return 6;
            }
            case 5 -> {
                return 9;
            }
            case 6 -> {
                return 12;
            }
            default -> {
                return 0;
            }
        }
    }

    public static int getAdjacentTilesPoints(int numberOfTiles) {
        if (numberOfTiles < 3) {
            throw new IllegalArgumentException("Number of Tiles must be at least 3");
        }

        switch (numberOfTiles) {
            case 3 -> {
                return 2;
            }
            case 4 -> {
                return 3;
            }
            case 5 -> {
                return 5;
            }
            default -> {
                return 8;
            }
        }
    }

    // Constants relative to the server/client communication
    public static final int defaultSocketPort = 12345;
    public static final int defaultRMIRegistryPort = java.rmi.registry.Registry.REGISTRY_PORT;
    public static final String defaultRMIName = "myshelfie";
}