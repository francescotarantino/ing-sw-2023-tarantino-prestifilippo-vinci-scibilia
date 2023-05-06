package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;

import java.util.Arrays;

public class Bookshelf {
    private final Player player;
    private final PersonalGoalCard personalGoalCard;
    private final Tile[][] matrix;
    private final boolean[] completedCommonGoalCards;

    /**
     * Creates a new Bookshelf for the specified player.
     * @param newPlayer the player who owns the bookshelf
     * @param personalGoalCard the personal goal card chosen for the player
     */
    public Bookshelf(Player newPlayer, PersonalGoalCard personalGoalCard) {
        this.player = newPlayer;
        this.personalGoalCard = personalGoalCard;
        this.matrix = new Tile[Constants.bookshelfX][Constants.bookshelfY];
        this.completedCommonGoalCards = new boolean[Constants.maxCommonGoalCards];
        Arrays.fill(completedCommonGoalCards, false);
    }

    /**
     * @return the player who owns the bookshelf
     */

    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return the personal goal card associated to the bookshelf
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }

    /**
     * Checks if the common goal card at the specified index is completed.
     * @param index the index of the common goal card to be checked
     * @return true if the common goal card at the specified index is completed, false otherwise
     */
    public boolean isCommonGoalCardCompleted(int index) {
        if(index < 0 || index >= Constants.maxCommonGoalCards)
            throw new IndexOutOfBoundsException("Invalid index");

        return this.completedCommonGoalCards[index];
    }
    /**
     * @return the matrix of the bookshelf
     */
    public Tile[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Returns the tile at the specified coordinates.
     * @param point the coordinates of the tile
     * @return the tile at the specified coordinates
     */
    public Tile getTile(Point point) {
        if(point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY)
            throw new IndexOutOfBoundsException("Invalid bookshelf coordinates");

        return this.matrix[point.getX()][point.getY()];
    }

    /**
     * Inserts the specified tile at the specified coordinates.
     * Throws exceptions if the coordinates are not valid.
     * @param point the coordinates where the tile will be inserted
     * @param tile the tile to be inserted
     */
    public void insertTile(Point point, Tile tile) {
        if (point == null || tile == null){
            throw new NullPointerException("Arguments cannot be null");
        } else if(point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY){
            throw new IndexOutOfBoundsException("Invalid bookshelf coordinates");
        } else if (this.matrix[point.getX()][point.getY()] != null){
            throw new IllegalArgumentException("There is already a tile in that position");
        } else if (point.getY() != 0 && this.matrix[point.getX()][point.getY()-1] == null){
            throw new IllegalArgumentException("There is no tile below, gravity is not respected");
        }

        this.matrix[point.getX()][point.getY()] = tile;
    }

    /**
     * Set the common goal card at the specified index as completed.
     * If the common goal card is already completed, an exception is thrown.
     * @param index the index of the common goal card to be set as completed
     */
    public void setCommonGoalCardCompleted(int index) {
        if(index < 0 || index >= Constants.maxCommonGoalCards)
            throw new IndexOutOfBoundsException("Invalid index");

        if (this.completedCommonGoalCards[index])
            throw new IllegalArgumentException("The common goal card is already completed");

        this.completedCommonGoalCards[index] = true;
    }

    /**
     * @return true if the bookshelf is full, false otherwise
     */
    public boolean isFull(){
        for (int i = 0; i < Constants.bookshelfX; i++) {
            for (int j = 0; j < Constants.bookshelfY; j++) {
                if (this.matrix[i][j] == null)
                    return false;
            }
        }
        return true;
    }
}
