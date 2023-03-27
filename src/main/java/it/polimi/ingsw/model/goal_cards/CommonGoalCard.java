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

    @Override
    public int checkValidity(Tile[][] matrix) {
        if(check(matrix))
            return this.takeScore();
        else
            return 0;
    }

    public abstract boolean check(Tile[][] matrix);
}
