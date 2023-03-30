package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import java.util.Arrays;
import java.util.Stack;

public abstract class CommonGoalCard extends GoalCard {
    private final Stack<Integer> scoringTokenStack;

    public CommonGoalCard(int numberOfPlayers, int ID) {
        super(ID);
        this.scoringTokenStack = new Stack<>();

        Arrays.stream(Constants.getScoringTokens(numberOfPlayers)).forEach(this.scoringTokenStack::push);
    }

    public int[] getAvailableScores(){
        return scoringTokenStack.stream().mapToInt(i -> i).toArray();
    }

    public int takeScore(){
        return scoringTokenStack.pop();
    }
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
                //return new CGCCross(numPlayers, ID);
            }
            case 12 -> {
                //return new CGCTriangular(numPlayers, ID);
            }
            default -> {
                throw new IndexOutOfBoundsException("Invalid ID");
            }
        }
        return null;
    }
    
    @Override
    public int checkValidity(Tile[][] matrix) {
        if(check(matrix))
            return this.takeScore();
        else
            return 0;
    }

    public abstract boolean check(Tile[][] matrix);
}
