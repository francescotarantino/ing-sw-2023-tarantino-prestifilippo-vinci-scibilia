package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;
import java.util.Arrays;
import static it.polimi.ingsw.Utils.checkMatrixSize;

public class CGCEightOf  extends CommonGoalCard{
    public CGCEightOf(int numberOfPlayers, int ID) {
        super(numberOfPlayers, ID);
    }

    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);
        int tilesOcc = 0;
        for(Constants.TileType type : Arrays.stream(Constants.TileType.values())
                .filter(x -> x != Constants.TileType.PLACEHOLDER) //PLACEHOLDER must be excluded
                .toArray(Constants.TileType[]::new)) {
            for (int i = 0; i < Constants.bookshelfX; i++) {
                for (int j = 0; j < Constants.bookshelfY; j++) {
                    if (matrix[i][j].getType() == type)  {
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
