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

        return checkLeftToRight(matrix) || checkRightToLeft(matrix);
    }

    private boolean checkLeftToRight(Tile[][] matrix) {
        for(int i = 0; i < Math.min(Constants.bookshelfX, Constants.bookshelfY); i++){
            for(int j = 0; j < Math.min(Constants.bookshelfX, Constants.bookshelfY); j++){
                if(i >= j && matrix[i][j] == null){
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkRightToLeft(Tile[][] matrix) {
        for(int i = 0; i < Math.min(Constants.bookshelfX, Constants.bookshelfY); i++){
            for(int j = 0; j < Math.min(Constants.bookshelfX, Constants.bookshelfY); j++){
                if(i + j < Math.min(Constants.bookshelfX, Constants.bookshelfY) && matrix[i][j] == null){
                    return false;
                }
            }
        }

        return true;
    }
}
