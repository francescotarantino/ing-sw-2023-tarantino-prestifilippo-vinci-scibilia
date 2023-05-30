package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.utils.GameUtils.checkMatrixSize;

public class CGCCross extends CommonGoalCard {
    public CGCCross(int numberOfPlayers, int ID) {
        super(numberOfPlayers, ID);
    }

    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);

        for(int i = 1; i < Constants.bookshelfX - 1; i++){
            for(int j = 1; j < Constants.bookshelfY - 1; j++){
                if(
                        matrix[i][j] != null &&
                        matrix[i - 1][j - 1] != null &&
                        matrix[i + 1][j - 1] != null &&
                        matrix[i - 1][j + 1] != null &&
                        matrix[i + 1][j + 1] != null &&
                        matrix[i][j].sameType(matrix[i - 1][j - 1]) &&
                        matrix[i][j].sameType(matrix[i + 1][j - 1]) &&
                        matrix[i][j].sameType(matrix[i - 1][j + 1]) &&
                        matrix[i][j].sameType(matrix[i + 1][j + 1])
                )
                    return true;
            }
        }

        return false;
    }
}
