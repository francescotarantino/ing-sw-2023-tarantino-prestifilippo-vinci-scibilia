package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.model.Tile;

/**
 * This class is the superclass of all the goal cards in the game.
 * Both personal and common goal cards have an ID and a method to check if the goal is satisfied.
 */
public abstract class GoalCard {
    /**
     * ID of the goal card.
     */
    final int ID;

    public GoalCard(int ID) {
        this.ID = ID;
    }

    public GoalCard() {
        // Default ID is -1 if not specified
        this.ID = -1;
    }

    public int getID(){
        return this.ID;
    }

    /**
     * This method checks if the goal is satisfied.
     * @param matrix the matrix of tiles to check
     * @return the number of points earned by the player if the goal is satisfied, 0 otherwise
     */
    public abstract int checkValidity(Tile[][] matrix);

    /**
     * This method returns the path of the image of the goal card.
     * @return the path of the image of the goal card
     */
    public abstract String getImagePath();
}
