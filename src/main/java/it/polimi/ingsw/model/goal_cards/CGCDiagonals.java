package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

public class CGCDiagonals extends CommonGoalCard {
    public CGCDiagonals(int numberOfPlayers) {
        super(numberOfPlayers, 7);
    }

    @Override
    public boolean check(Tile[][] matrix) {
        for (int i = 0; i < Constants.bookshelfY - Constants.bookshelfX; i++) {
            for (int j = 1; j < Constants.bookshelfX; j++) {
                if(!(matrix[j][Constants.bookshelfY - (1 + j + i)].sameType(matrix[0][Constants.bookshelfY - (1 + i)]))) {
                    break;
                }
                else if (j == Constants.bookshelfX - 1) {
                    return true;
                }
            }
        }
        for (int i = 0; i < Constants.bookshelfY - Constants.bookshelfX; i++) {
            for (int j = 1; j < Constants.bookshelfX; j++) {
                if (!(matrix[j][i + j].sameType(matrix[0][i]))) {
                    break;
                }
                else if (j == Constants.bookshelfX - 1) {
                    return true;
                }
            }
        }
        return false;
    }
}