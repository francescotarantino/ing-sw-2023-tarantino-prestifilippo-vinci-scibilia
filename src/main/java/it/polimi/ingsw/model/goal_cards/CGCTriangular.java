package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.Utils.checkMatrixSize;

public class CGCTriangular extends CommonGoalCard {
    public CGCTriangular(int numberOfPlayers, int ID) {
        super(numberOfPlayers, ID);
    }

    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);

        return checkAscending(matrix) || checkDescending(matrix);
    }

    private boolean checkAscending(Tile[][] matrix) {
        for(int i = 0; i < Constants.bookshelfX - 1; i++) {
            if (matrix[i][Constants.bookshelfY - 1] != null) {
                return false;
            }
        }
        for(int i = 0; i < Math.min(Constants.bookshelfX, Constants.bookshelfY); i++) {
            for (int j = 0; j < Math.min(Constants.bookshelfX, Constants.bookshelfY); j++) {
                if (i >= j && matrix[i][j] == null) {
                    return false;
                } else if (i < j && matrix[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkDescending(Tile[][] matrix) {
        for(int i = 0; i < Constants.bookshelfX - 1; i++) {
            if (matrix[i][Constants.bookshelfY - 1] != null) {
                return false;
            }
        }
        for(int i = 0; i < Math.min(Constants.bookshelfX, Constants.bookshelfY); i++){
            for(int j = 0; j < Math.min(Constants.bookshelfX, Constants.bookshelfY); j++){
                if(i + j <= Math.min(Constants.bookshelfX, Constants.bookshelfY) - 1 && matrix[i][j] == null){
                    return false;
                } else if(i + j > Math.min(Constants.bookshelfX, Constants.bookshelfY) - 1 && matrix[i][j] != null){
                    return false;
                }
            }
        }

        return true;
    }
}
