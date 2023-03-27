package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.model.Tile;

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
        return this.ID;
    }

    public abstract int checkValidity(Tile[][] matrix);
}
