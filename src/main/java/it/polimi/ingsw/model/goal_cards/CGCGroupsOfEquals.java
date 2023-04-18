package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.Tile;

import java.util.HashMap;

import static it.polimi.ingsw.Utils.checkMatrixSize;


public class CGCGroupsOfEquals extends CommonGoalCard {

    /**
     * type of blocks that must be found in the matrix by the Common Goal Card
     */
    public enum BlockType{
        FOUR_OF_FOUR,
        SIX_OF_TWO,
        TWO_SQUARES
    }
    
    private final BlockType blockType;
    /**
     * @param blockType refers to the type of blocks that must be found, it can be:
     *                  4x4 (CGC1), 6x2 (CGC3) or 2 squares (CGC4)
     */
    public CGCGroupsOfEquals(int numberOfPlayers, int ID, BlockType blockType) {
        super(numberOfPlayers, ID);
        this.blockType = blockType;
    }

    /**
     * @return true if the matrix contains a 4x4 group of the same type, duly shaped and separated from other groups
     */
    private boolean findSquare(int i, int j, Tile[][] matrix) {
        if (matrix[i][j].sameType(matrix[i+1][j]) && matrix[i][j].sameType(matrix[i][j+1]) && matrix[i][j].sameType(matrix[i+1][j+1])) {
            if (i != 0) {
                if (matrix[i][j].sameType(matrix[i - 1][j]) || matrix[i][j].sameType(matrix[i - 1][j + 1])) {
                    return false;
                }
            }
            if (j != 0) {
                if (matrix[i][j].sameType(matrix[i][j - 1]) || matrix[i][j].sameType(matrix[i + 1][j - 1])) {
                    return false;
                }
            }
            if (i != Constants.bookshelfX - 2) {
                if (matrix[i][j].sameType(matrix[i + 2][j]) || matrix[i][j].sameType(matrix[i + 2][j + 1])) {
                    return false;
                }
            }
            if (j != Constants.bookshelfY - 2) {
                if (matrix[i][j].sameType(matrix[i][j + 2]) || matrix[i][j].sameType(matrix[i + 1][j + 2])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     *This method takes care of updating the done matrix for 4x4 groups
     */
    private void setDone (int i, int j, boolean[][] done) {
        done[i][j] = true;
        done[i+1][j] = true;
        done[i][j+1] = true;
        done[i+1][j+1] = true;
        if (i < Constants.bookshelfX - 2) {
            done[i+2][j] = true;
            done[i+2][j+1] = true;
        }
        if (j < Constants.bookshelfY - 2) {
            done[i][j+2] = true;
            done[i+1][j+2] = true;
        }
        if (j < Constants.bookshelfY - 2 && i < Constants.bookshelfX - 2) {
            done[i+2][j+2] = true;
        }
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
                            if(Utils.findGroup(i, j, matrix, done) >= 2) {
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
                            if(Utils.findGroup(i, j, matrix, done) >= 4) {
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
            case TWO_SQUARES -> {
                HashMap<Constants.TileType, Integer> numberOfSquares = new HashMap<>();
                for (int i = 0; i < Constants.bookshelfX - 2; i++) {
                    for (int j = 0; j < Constants.bookshelfY - 2; j++) {
                        if(!done[i][j]) {
                            if (findSquare(i, j, matrix)) {
                                setDone(i, j, done);
                                int number = numberOfSquares.getOrDefault(matrix[i][j].getType(), 0);
                                numberOfSquares.put(matrix[i][j].getType(), number + 1);
                                if (numberOfSquares.values().stream().anyMatch(kind -> kind > 1)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }
}
