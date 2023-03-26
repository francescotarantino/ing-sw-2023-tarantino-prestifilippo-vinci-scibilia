package it.polimi.ingsw.model;

public abstract class GoalCard {
    final int ID;

    public GoalCard(int ID) {
        this.ID = ID;
    }

    // Default ID is -1 if not specified
    public GoalCard() {
        this.ID = -1;
    }

    public int getID(){
        return ID;
    }

    public abstract boolean checkValidity(Tile[][] matrix);
}
