package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import static it.polimi.ingsw.Utils.checkMatrixSize;

public class CGCCorners extends CommonGoalCard{
    public CGCCorners(int numberOfPlayers, int ID) {
        super(numberOfPlayers, ID);
    }
    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);

        if(matrix[0][0] == null || matrix[Constants.bookshelfX - 1][0] == null || matrix[0][Constants.bookshelfY - 1] == null || matrix[Constants.bookshelfX - 1][Constants.bookshelfY - 1] == null) {
            return false;
        }

        if (!(matrix[Constants.bookshelfX - 1][0]).sameType(matrix[0][0])) {
            return false;
        } else if (!(matrix[0][Constants.bookshelfY - 1]).sameType(matrix[0][0])) {
            return false;
        } else {
            return (matrix[Constants.bookshelfX - 1][Constants.bookshelfY - 1]).sameType(matrix[0][0]);
        }
    }
}
