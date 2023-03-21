package it.polimi.ingsw.model;

public class PersonalGoalCard extends GoalCard{

    private TileType[][] matrix;
    private  int[] scoringTokenStack;

    public TileType[][] getMatrix() {
        return matrix;
    }

    @Override
    public int GetId() {
        return this.ID;
    }

    @Override
    public int CheckValidity(Bookshelf B) {
        //return 0;  TODO: once we have bookshelf class coded
    }
}
