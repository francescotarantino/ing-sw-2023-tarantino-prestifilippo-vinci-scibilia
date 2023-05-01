package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import java.util.List;

import static it.polimi.ingsw.Utils.checkMatrixSize;

public class PersonalGoalCard extends GoalCard {
    private final Constants.TileType[][] matrix;

    /**
     * Creates a personal goal card with the given ID, reading the matrix from the Constants class (so from the JSON file)
     * @param index the index of the personal goal card
     */
    public PersonalGoalCard(int index) {
        super(index);
        this.matrix = new Constants.TileType[Constants.bookshelfX][Constants.bookshelfY];

        List<String> data = Constants.getPersonalGoalCards().get(index);

        int indexInFile = 0;
        for (int j = Constants.bookshelfY - 1; j >= 0; j--){
            for (int i = 0; i < Constants.bookshelfX; i++){
                if(data.get(indexInFile) != null){
                    this.matrix[i][j] = Constants.TileType.valueOf(data.get(indexInFile));
                } else {
                    this.matrix[i][j] = null;
                }

                indexInFile++;
            }
        }
    }

    /**
     * This constructor should be used only for testing purposes.
     * It creates a personal goal card with the given matrix.
     * @param matrix the matrix of the personal goal card
     */
    protected PersonalGoalCard(Constants.TileType[][] matrix) {
        checkMatrixSize(matrix);

        this.matrix = matrix;
    }

    /**
     * @return the matrix of the personal goal card
     */
    public Constants.TileType[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Checks the validity of the personal goal card, returning the number of points earned.
     * @param matrix the matrix of the bookshelf to be checked
     * @return the number of points
     */
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
