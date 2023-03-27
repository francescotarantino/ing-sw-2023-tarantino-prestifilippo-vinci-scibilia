package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

public class CGCEightOf  extends CommonGoalCard{
    public CGCEightOf(int numberOfPlayers) {
        super(numberOfPlayers, 6);
    }

    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        int tilesOcc = 0;
        for (Constants.TileType type : Constants.TileType.values()) {
            for (int i = 0; i < Constants.bookshelfX - 1; i++) {
                for (int j = 0; j < Constants.bookshelfY - 1; j++) {
                    if (matrix[i][j].getType().equals(type))  {
                        tilesOcc++;
                    }
                }
            }
            if (tilesOcc >= 8) {
                return true;
            }
            else {
                tilesOcc = 0;
            }
        }
        return false;
    }
}
