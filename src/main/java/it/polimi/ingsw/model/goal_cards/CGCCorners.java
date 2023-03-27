package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

public class CGCCorners extends CommonGoalCard{
    public CGCCorners(int numberOfPlayers) {
        super(numberOfPlayers, 2);
    }
    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
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
