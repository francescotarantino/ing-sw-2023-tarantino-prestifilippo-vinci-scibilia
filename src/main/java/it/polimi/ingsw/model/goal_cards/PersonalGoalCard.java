package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;

import static it.polimi.ingsw.Utils.checkMatrixSize;

public class PersonalGoalCard extends GoalCard {
    private final Constants.TileType[][] matrix;

    public PersonalGoalCard(int index) {
        super(index);
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

    // Constructor used for testing
    public PersonalGoalCard(Constants.TileType[][] matrix) {
        checkMatrixSize(matrix);

        this.matrix = matrix;
    }

    public Constants.TileType[][] getMatrix() {
        return this.matrix;
    }

    @Override
    public int checkValidity(Tile[][] matrix) {
        checkMatrixSize(matrix);

        int count = 0;
        for (int i = 0; i < Constants.bookshelfX; i++){
            for (int j = 0; j < Constants.bookshelfY; j++){
                if(this.matrix[i][j] != null && matrix[i][j] != null && this.matrix[i][j] == matrix[i][j].getType()){
                    count++;
                }
            }
        }

        return Constants.getPersonalGoalCardPoints(count);
    }
}
