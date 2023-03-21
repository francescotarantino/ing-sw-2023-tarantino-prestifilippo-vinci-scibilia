package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

public class PersonalGoalCard extends GoalCard{

    private Constants.TileType[][] matrix;
    private  int[] scoringTokenStack;

    public Constants.TileType[][] getMatrix() {
        return matrix;
    }

    @Override
    public int GetId() {
        return this.ID;
    }

    @Override
    public int CheckValidity(Bookshelf B) {
        return 0;  // TODO: once we have bookshelf class coded
    }
}
