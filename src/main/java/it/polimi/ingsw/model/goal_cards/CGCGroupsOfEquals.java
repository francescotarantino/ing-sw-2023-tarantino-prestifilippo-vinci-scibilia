package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;

import static it.polimi.ingsw.utils.GameUtils.findGroup;
import static it.polimi.ingsw.utils.GameUtils.checkMatrixSize;

public class CGCGroupsOfEquals extends CommonGoalCard {

    /**
     * type of blocks that must be found in the matrix by the Common Goal Card
     */
    public enum BlockType{
        FOUR_OF_FOUR,
        SIX_OF_TWO
    }
    
    private final BlockType blockType;
    /**
     * @param blockType refers to the type of blocks that must be found, it can be:
     *                  4x4 (CGC1) or 6x2 (CGC3)
     */
    public CGCGroupsOfEquals(int numberOfPlayers, int ID, BlockType blockType) {
        super(numberOfPlayers, ID);
        this.blockType = blockType;
    }
    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        checkMatrixSize(matrix);
        boolean[][] done = new boolean[Constants.bookshelfX][Constants.bookshelfY];
        int groupsFound = 0;
        switch (blockType) {
            case SIX_OF_TWO -> {
                for (int i = 0; i < Constants.bookshelfX; i++) {
                    for (int j = 0; j < Constants.bookshelfY; j++) {
                        if(!done[i][j]) {
                            if(findGroup(new Point(i, j), matrix, done) >= 2) {
                                groupsFound++;
                            }
                        }
                        if (groupsFound >= 6) {
                            return true;
                        }
                    }
                }
                return false;
            }
            case FOUR_OF_FOUR ->  {
                for (int i = 0; i < Constants.bookshelfX; i++) {
                    for (int j = 0; j < Constants.bookshelfY; j++) {
                        if(!done[i][j]) {
                            if(findGroup(new Point(i, j), matrix, done) >= 4) {
                                groupsFound++;
                            }
                        }
                        if (groupsFound >= 4) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}
