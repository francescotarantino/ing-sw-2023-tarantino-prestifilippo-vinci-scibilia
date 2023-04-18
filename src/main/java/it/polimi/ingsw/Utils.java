package it.polimi.ingsw;

import it.polimi.ingsw.model.Tile;
public class Utils {
    /**
     * Checks if the matrix has the correct size of the bookshelf (Constants.bookshelfX x Constants.bookshelfY)
     * @param matrix matrix to check
     * @param <T> type of the matrix
     */
    public static <T> void checkMatrixSize(T[][] matrix){
        if(matrix.length != Constants.bookshelfX)
            throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
        for (T[] tiles : matrix)
            if (tiles.length != Constants.bookshelfY)
                throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
    }
    /**
     * @return is the size of the group found starting from the tile in position (x,y) in the matrix
     * The method is recursive, and it builds the biggest possible block by checking the adjacent tiles in all directions
     */
    public static int findGroup(int x, int y, Tile[][] matrix, boolean[][] done) {
        if (done[x][y]) {
            return 0;
        }
        done[x][y] = true;
        int currentSize = 1;
        for(Constants.Direction direction: Constants.Direction.values()) {
            if (checkAdjacentTile(x, y, matrix, direction)) {
                switch (direction) {
                    case UP ->  currentSize += findGroup(x, y + 1, matrix, done);
                    case RIGHT -> currentSize += findGroup(x + 1, y, matrix, done);
                    case DOWN -> currentSize += findGroup(x, y - 1, matrix, done);
                    case LEFT -> currentSize += findGroup(x - 1, y, matrix, done);
                }
            }
        }
        return currentSize;
    }
    /**
     * @param direction is the direction in which the tile in the matrix is checked
     * @return true if the tile in the matrix has the same type of the tile in the direction specified
     */
    public static boolean checkAdjacentTile(int x, int y, Tile[][] matrix, Constants.Direction direction) {
        if(x < 0 || x >= Constants.bookshelfX || y < 0 || y >= Constants.bookshelfY) {
            return false;
        }
        switch (direction) {
            case UP -> {
                if (y == Constants.bookshelfY - 1) {
                    return false;
                }
                else {
                    if (matrix[x][y].sameType(matrix[x][y + 1])) {
                        return true;
                    }
                }
            }
            case RIGHT -> {
                if (x == Constants.bookshelfX - 1) {
                    return false;
                }
                else {
                    if (matrix[x][y].sameType(matrix[x + 1][y])) {
                        return true;
                    }
                }
            }
            case DOWN -> {
                if (y == 0) {
                    return false;
                }
                else {
                    if (matrix[x][y].sameType(matrix[x][y - 1])) {
                        return true;
                    }
                }
            }
            case LEFT -> {
                if (x == 0) {
                    return false;
                }
                else {
                    if (matrix[x][y].sameType(matrix[x - 1][y])) {
                        return true;
                    }
                }
            }
            default -> throw new IllegalStateException("Invalid direction: " + direction);
        }
        return false;
    }
}
