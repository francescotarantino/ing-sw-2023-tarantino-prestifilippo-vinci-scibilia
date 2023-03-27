package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import static it.polimi.ingsw.Utils.checkMatrixSize;

public class CGCCorners extends CommonGoalCard{
    public CGCCorners(int numberOfPlayers) {
        super(numberOfPlayers, Constants.commonGoalCardName.get("CGCCorners"));
    }
    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);
        if (!(matrix[Constants.bookshelfX - 1][0]).sameType(matrix[0][0])) {
            return false;
        }
        else if (!(matrix[0][Constants.bookshelfY - 1]).sameType(matrix[0][0])) {
            return false;
        }
        else {
            return (matrix[Constants.bookshelfX - 1][Constants.bookshelfY - 1]).sameType(matrix[0][0]);
        }
    }
}
