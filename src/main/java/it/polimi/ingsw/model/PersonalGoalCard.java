package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import java.util.ArrayList;

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

    private static <T> void checkMatrixSize(T[][] matrix){
        if(matrix.length != Constants.bookshelfX)
            throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
        for (T[] tiles : matrix)
            if (tiles.length != Constants.bookshelfY)
                throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
    }
}
