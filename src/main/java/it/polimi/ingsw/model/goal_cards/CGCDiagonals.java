package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;
import static it.polimi.ingsw.utils.GameUtils.checkMatrixSize;

/**
 * This class represents the common goal card Diagonals.
 */
public class CGCDiagonals extends CommonGoalCard {
    public CGCDiagonals(int numberOfPlayers, int ID) {
        super(numberOfPlayers, ID);
    }

    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null){
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);
        for (int i = 0; i < Constants.bookshelfY - Constants.bookshelfX + 1; i++) {
            for (int j = 1; j < Constants.bookshelfX; j++) {
                if(
                        matrix[j][Constants.bookshelfY - (1 + j + i)] == null ||
                        matrix[0][Constants.bookshelfY - (1 + i)] == null ||
                        !(matrix[j][Constants.bookshelfY - (1 + j + i)].sameType(matrix[0][Constants.bookshelfY - (1 + i)]))
                ) {
                    break;
                } else if (j == Constants.bookshelfX - 1) {
                    return true;
                }
            }
        }
        for (int i = 0; i < Constants.bookshelfY - Constants.bookshelfX + 1; i++) {
            for (int j = 1; j < Constants.bookshelfX; j++) {
                if (
                        matrix[j][i + j] == null ||
                        matrix[0][i] == null ||
                        !(matrix[j][i + j].sameType(matrix[0][i]))
                ) {
                    break;
                } else if (j == Constants.bookshelfX - 1) {
                    return true;
                }
            }
        }
        return false;
    }
}