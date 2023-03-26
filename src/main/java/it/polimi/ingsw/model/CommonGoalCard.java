package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

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
}
