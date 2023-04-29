package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import java.util.*;

public abstract class CommonGoalCard extends GoalCard {
    private final Stack<Integer> scoringTokenStack;
    private final String description;

    /**
     * Creates a common goal card with the given ID
     * @param numberOfPlayers the number of players in the game
     * @param ID the ID of the common goal card
     */
    public CommonGoalCard(int numberOfPlayers, int ID) {
        super(ID);
        this.scoringTokenStack = new Stack<>();
        Arrays.stream(Constants.getScoringTokens(numberOfPlayers)).forEach(this.scoringTokenStack::push);

        this.description = Constants.getCGCDescriptionByID(ID);
    }

    /**
     * @return the points of the scoring tokens available for this common goal card
     */
    public int[] getAvailableScores(){
        return scoringTokenStack.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Pops a scoring token from the stack and returns it
     * @return the point of the scoring token
     */
    protected int takeScore(){
        return scoringTokenStack.pop();
    }

    /**
     * Creates a specific common goal card based on the number of players and the ID
     * @param numPlayers the number of players in the game
     * @param ID the ID of the common goal card
     * @return the common goal card, that is an instance of a subclass of CommonGoalCard
     */
    public static CommonGoalCard create(int numPlayers, int ID) {
        if (numPlayers < Constants.playersLowerBound || numPlayers > Constants.playersUpperBound) {
            throw new IndexOutOfBoundsException("Invalid number of players.");
        }
        switch (ID) {
            case 1 -> {
                return new CGCGroupsOfEquals(numPlayers, ID, CGCGroupsOfEquals.BlockType.SIX_OF_TWO);
            }
            case 2 -> {
                return new CGCCorners(numPlayers, ID);
            }
            case 3 -> {
                return new CGCGroupsOfEquals(numPlayers, ID, CGCGroupsOfEquals.BlockType.FOUR_OF_FOUR);
            }
            case 4 -> {
                return new CGCGroupsOfEquals(numPlayers, ID, CGCGroupsOfEquals.BlockType.TWO_SQUARES);
            }
            case 5 -> {
                return new CGCLines(numPlayers, ID, CGCLines.Direction.VERTICAL, 3, 3);
            }
            case 6 -> {
                return new CGCEightOf(numPlayers, ID);
            }
            case 7 -> {
                return new CGCDiagonals(numPlayers, ID);
            }
            case 8 -> {
                return new CGCLines(numPlayers, ID, CGCLines.Direction.HORIZONTAL, 3, 4);
            }
            case 9 -> {
                return new CGCLines(numPlayers, ID, CGCLines.Direction.VERTICAL, 0, 2);
            }
            case 10 -> {
                return new CGCLines(numPlayers, ID, CGCLines.Direction.HORIZONTAL, 0, 2);
            }
            case 11 -> {
                return new CGCCross(numPlayers, ID);
            }
            case 12 -> {
                return new CGCTriangular(numPlayers, ID);
            }
            default -> throw new IndexOutOfBoundsException("Invalid ID");
        }
    }

    /**
     * Checks if the given matrix satisfies the requirements of the common goal card and returns the appropriate score.
     * @param matrix the matrix to be checked
     * @return the score of the common goal card if the matrix satisfies the requirements, 0 otherwise
     */
    @Override
    public int checkValidity(Tile[][] matrix) {
        if(check(matrix))
            return this.takeScore();
        else
            return 0;
    }

    /**
     * Checks if the given matrix satisfies the requirements of the common goal card
     * @param matrix the matrix to be checked
     * @return true if the matrix satisfies the requirements, false otherwise
     */
    public abstract boolean check(Tile[][] matrix);

    /**
     * @return the description of the common goal card
     */
    public String getDescription(){
        return this.description;
    }
}
