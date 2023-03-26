package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

import java.util.ArrayList;

public class PersonalGoalCard extends GoalCard {
    private final Constants.TileType[][] matrix;
    private int[] scoringTokenStack; // TODO: to be implemented

    public PersonalGoalCard(int index) {
        this.matrix = new Constants.TileType[Constants.bookshelfX][Constants.bookshelfY];

        ArrayList<String> data = Constants.getPersonalGoalCards().get(index);

        for (int i = 0; i < Constants.bookshelfX; i++){
            for (int j = 0; j < Constants.bookshelfY; j++){
                if(data.get(i*Constants.bookshelfY + j) != null){
                    this.matrix[i][j] = Constants.TileType.valueOf(data.get(i*Constants.bookshelfY + j));
                } else {
                    this.matrix[i][j] = null;
                }
            }
        }
    }

    public Constants.TileType[][] getMatrix() {
        return this.matrix;
    }

    @Override
    public int getId() {
        return this.ID;
    }

    @Override
    public boolean checkValidity(Tile[][] matrix) {
        for (int i = 0; i < Constants.bookshelfX; i++){
            for (int j = 0; j < Constants.bookshelfY; j++){
                if(this.matrix[i][j] != null && this.matrix[i][j] != matrix[i][j].getType()){
                    return false;
                }
            }
        }

        return true;
    }
}
