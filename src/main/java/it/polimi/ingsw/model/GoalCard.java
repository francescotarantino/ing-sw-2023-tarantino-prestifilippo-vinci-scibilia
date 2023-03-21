package it.polimi.ingsw.model;

public abstract class GoalCard {
     int ID;

    public abstract int GetId();
    public abstract int CheckValidity(Bookshelf B);
}
