package it.polimi.ingsw.model;

public abstract class GoalCard {
    private int ID;

    public abstract int GetId(GoalCard gc);
    public abstract int CheckValidity(Bookshelf B);
}
