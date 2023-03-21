package it.polimi.ingsw.model;

public abstract class GoalCard {
    int ID;

    public abstract int getId();
    public abstract int checkValidity(Bookshelf B);
}
