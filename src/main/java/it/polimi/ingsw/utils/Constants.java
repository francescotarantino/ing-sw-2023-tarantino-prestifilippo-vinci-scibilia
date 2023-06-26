package it.polimi.ingsw.utils;

import org.fusesource.jansi.Ansi;

/**
 * This class contains all the constants used in the game.
 */
public abstract class Constants {
    /**
     * Game version
     */
    public static final String version = "0.2a";

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
        BOOKS(Ansi.Color.DEFAULT),
        GAMES(Ansi.Color.YELLOW),
        FRAMES(Ansi.Color.BLUE),
        TROPHIES(Ansi.Color.CYAN),
        PLANTS(Ansi.Color.MAGENTA),
        /**
         * This is a placeholder for all the invalid positions in the Living Room Board.
         * When there is a PLACEHOLDER, it means that the position cannot be occupied by a tile.
         */
        PLACEHOLDER(null);

        /**
         * Tile color, used to print the tile in the TextualUI.
         */
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
     * This is the value in points of the end game token.
     */
    public static final int endGameToken = 1;

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
    /**
     * Timeout (in milliseconds) used in the ping-pong mechanism.
     */
    public static final long pingpongTimeout = 1000;
    /**
     * Timeout (in milliseconds) used by the client to determine if the connection to the server is lost.
     * This value should be greater than the ping-pong timeout.
     */
    public static final long connectionLostTimeout = pingpongTimeout * 3;
    /**
     * Time (in milliseconds) after which the only player left in the game wins.
     */
    public static final long walkoverTimeout = 600 * 1000;
    /**
     * Type of connection that can be used to connect to the server.
     */
    public enum ConnectionType {
        RMI,
        SOCKET
    }
    /**
     * Types of User Interfaces.
     */
    public enum UIType {
        CLI,
        GUI
    }
}