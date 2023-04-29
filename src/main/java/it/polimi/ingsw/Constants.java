package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Point;
import org.fusesource.jansi.Ansi;

import java.io.*;
import java.util.*;

public class Constants {
    /**
     * Number of columns in the Bookshelf.
     */
    public static final int bookshelfX = 5;
    /**
     * Number of rows in the Bookshelf.
     */
    public static final int bookshelfY = 6;

    /**
     * x-size of the Living Room Board.
     */
    public static final int livingRoomBoardX = 9;
    /**
     * y-size of the Living Room Board.
     */
    public static final int livingRoomBoardY = 9;
    /**
     * This method is used to retrieve the invalid positions for the living room board.
     * @param numPlayers the number of players in the game
     * @return a set of Points containing the invalid positions
     */
    public static Set<Point> getInvalidPositions(int numPlayers) {
        Set<Point> invalidPositions = new HashSet<>();

        switch (numPlayers) {
            case 2:
                invalidPositions.add(new Point(0, 3));
                invalidPositions.add(new Point(2, 2));
                invalidPositions.add(new Point(2, 6));
                invalidPositions.add(new Point(3, 8));
                invalidPositions.add(new Point(5, 0));
                invalidPositions.add(new Point(6, 2));
                invalidPositions.add(new Point(6, 6));
                invalidPositions.add(new Point(8, 5));
            case 3:
                invalidPositions.add(new Point(0, 4));
                invalidPositions.add(new Point(1, 5));
                invalidPositions.add(new Point(3, 1));
                invalidPositions.add(new Point(4, 0));
                invalidPositions.add(new Point(4, 8));
                invalidPositions.add(new Point(5, 7));
                invalidPositions.add(new Point(7, 3));
                invalidPositions.add(new Point(8, 4));
            case 4:
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
        }

        return invalidPositions;
    }

    /**
     * Minimum number of players in a game.
     */
    public static final int playersLowerBound = 2;
    /**
     * Maximum number of players in a game.
     */
    public static final int playersUpperBound = 4;
    /**
     * Game IDs lower bound.
     */
    public static final int IDLowerBound = 0;

    /**
     * This is the number of different variants of a single tile type.
     * For example, there are <i>tileVariants</i> different variants of the CATS tile type,
     * each one with a different image.
     */
    public static final int tileVariants = 3;
    /**
     * This is the number of tiles of each type in the bag at the beginning of the game.
     */
    public static final int tilesPerType = 22;
    /**
     * Enumeration of all the possible Tile Types in the game.
     */
    public enum TileType {
        CATS(Ansi.Color.GREEN),
        BOOKS(Ansi.Color.WHITE),
        GAMES(Ansi.Color.YELLOW),
        FRAMES(Ansi.Color.BLUE),
        TROPHIES(Ansi.Color.CYAN),
        PLANTS(Ansi.Color.MAGENTA),
        /**
         * This is a placeholder for all the invalid positions in the Living Room Board.
         * When there is a PLACEHOLDER, it means that the position cannot be occupied by a tile.
         */
        PLACEHOLDER(null);

        private final Ansi.Color color;

        TileType (Ansi.Color color){
            this.color = color;
        }

        public Ansi.Color color(){
            return color;
        }
    }

    /**
     * Minimum number of Common Goal Cards in a game.
     */
    public static final int minCommonGoalCards = 1;
    /**
     * Maximum number of Common Goal Cards in a game.
     */
    public static final int maxCommonGoalCards = 2;
    /**
     * This is the total number of Common Goal Cards available in the game.
     * Check also {@link it.polimi.ingsw.model.goal_cards.CommonGoalCard#create(int, int)}.
     */
    public static final int totalNumberOfCommonGoalCards = 12;

    /**
     * Minimum pick from the Living Room Board in a turn.
     */
    public static final int minPick = 1;
    /**
     * Maximum pick from the Living Room Board in a turn.
     */
    public static final int maxPick = 3;

    /**
     * Returns an array of scoring tokens used in common goal cards based on the number of players.
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

    /**
     * This is the value in points of the end game token.
     */
    public static final int endGameToken = 1;

    /**
     * This private attribute stores temporarily the list of all the Personal Goal Cards read from the JSON file.
     */
    private static List<List<String>> personalGoalCards;
    /**
     * Returns a list of all the Personal Goal Cards from the JSON file.
     * The first time this method is called, it reads the JSON file and stores the result in a private variable.
     * @return list of all the Personal Goal Cards
     */
    public static List<List<String>> getPersonalGoalCards() {
        if (personalGoalCards != null) {
            return new ArrayList<>(personalGoalCards);
        }

        Reader reader = new InputStreamReader(Objects.requireNonNull(Constants.class.getResourceAsStream("/json/PersonalGoalCards.json")));

        personalGoalCards = (List<List<String>>) new Gson().fromJson(reader, ArrayList.class);
        return new ArrayList<>(personalGoalCards);
    }

    /**
     * This private attribute stores temporarily the descriptions of all the Common Goal Cards from the JSON file.
     */
    private static Map<String, String> commonGoalCardsDescriptions;

    /**
     * Returns the description of a Common Goal Card given its ID.
     * @param ID the id of the Common Goal Card
     * @return the description of the Common Goal Card
     */
    public static String getCGCDescriptionByID(int ID) {
        if(commonGoalCardsDescriptions != null){
            commonGoalCardsDescriptions.get(Integer.valueOf(ID).toString());
        }

        Reader reader = new InputStreamReader(Objects.requireNonNull(Constants.class.getResourceAsStream("/json/CommonGoalCardsDescriptions.json")));

        commonGoalCardsDescriptions = ((Map<String, String>) new Gson().fromJson(reader, Map.class));
        return commonGoalCardsDescriptions.get(Integer.valueOf(ID).toString());
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

    /**
     * Returns the number of points a player gets for a given number of adjacent tiles.
     * @param numberOfTiles number of adjacent tiles
     * @return points
     */
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

    /**
     * Directions in a 2D space.
     */
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /**
     * Default port used for the Socket connection.
     */
    public static final int defaultSocketPort = 12345;
    /**
     * Default port used for connecting to the RMI Registry.
     */
    public static final int defaultRMIRegistryPort = java.rmi.registry.Registry.REGISTRY_PORT;
    /**
     * Default name used for the RMI Registry.
     */
    public static final String defaultRMIName = "myShelfie";
}