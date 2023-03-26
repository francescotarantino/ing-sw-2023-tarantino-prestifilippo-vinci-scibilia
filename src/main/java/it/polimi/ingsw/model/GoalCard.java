package it.polimi.ingsw.model;

public abstract class GoalCard {
    int ID;

    public abstract int getId();
    public abstract boolean checkValidity(Tile[][] matrix);
}
