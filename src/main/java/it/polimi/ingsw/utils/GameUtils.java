package it.polimi.ingsw.utils;

import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;

import java.util.Arrays;

/**
 * This class contains some utility methods useful for checks on the game.
 */
public abstract class GameUtils {
    /**
     * Checks if the matrix has the correct size of the bookshelf (Constants.bookshelfX x Constants.bookshelfY)
     * @param matrix matrix to check
     * @param <T> type of the matrix
     */
    public static <T> void checkMatrixSize(T[][] matrix) {
        if (matrix.length != Constants.bookshelfX)
            throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
        for (T[] tiles : matrix)
            if (tiles.length != Constants.bookshelfY)
                throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
    }

    /**
     * The method is recursive, and it builds the biggest possible block by checking the adjacent tiles in all directions.
     * @return the size of the group found starting from the tile in position (x,y) in the matrix
     */
    public static int findGroup(Point point, Tile[][] matrix, boolean[][] done) {
        if (done[point.x()][point.y()]) {
            return 0;
        }
        done[point.x()][point.y()] = true;
        if(matrix[point.x()][point.y()] == null) {
            return 0;
        }
        int currentSize = 1;
        for (Constants.Direction direction : Constants.Direction.values()) {
            Tile tile = checkAdjacentTile(point, matrix, direction);
            if (tile != null && tile.sameType(matrix[point.x()][point.y()])) {
                switch (direction) {
                    case UP -> currentSize += findGroup(new Point(point.x(), point.y() + 1), matrix, done);
                    case RIGHT -> currentSize += findGroup(new Point(point.x() + 1, point.y()), matrix, done);
                    case DOWN -> currentSize += findGroup(new Point(point.x(), point.y() - 1), matrix, done);
                    case LEFT -> currentSize += findGroup(new Point(point.x() - 1, point.y()), matrix, done);
                }
            }
        }
        return currentSize;
    }

    /**
     * @param direction is the direction in which the tile in the matrix is checked
     * @return the tile in the matrix in the direction specified
     */
    public static Tile checkAdjacentTile(Point point, Tile[][] matrix, Constants.Direction direction) {
        int xSize = matrix.length;
        int ySize = matrix[0].length;
        if (point.x() < 0 || point.x() >= xSize || point.y() < 0 || point.y() >= ySize) {
            return null;
        }
        switch (direction) {
            case UP -> {
                if(point.y() == ySize - 1) return null;
                return matrix[point.x()][point.y() + 1];
            }
            case RIGHT -> {
                if(point.x() == xSize - 1) return null;
                return matrix[point.x() + 1][point.y()];
            }
            case DOWN -> {
                if(point.y() == 0) return null;
                return matrix[point.x()][point.y() - 1];
            }
            case LEFT -> {
                if(point.x() == 0) return null;
                return matrix[point.x() - 1][point.y()];
            }
            default -> throw new IllegalStateException("Invalid direction: " + direction);
        }
    }

    /**
     * This method checks if the tiles at the given positions can be taken from the board matrix.
     * @param boardMatrix the matrix of the board
     * @param points the positions of the tiles to check
     * @return true if the tiles can be taken, false otherwise
     */
    public static boolean checkIfTilesCanBeTaken(Tile[][] boardMatrix, Point... points) {
        if(points.length < Constants.minPick || points.length > Constants.maxPick){
            return false;
        }
        // Check if the tiles are not null or placeholders
        for (Point point : points) {
            if (boardMatrix[point.x()][point.y()] == null || boardMatrix[point.x()][point.y()].isPlaceholder()) return false;
        }

        // Check if tiles are adjacent
        if(points.length != 1){
            if(Arrays.stream(points).mapToInt(Point::x).allMatch(x -> x == points[0].x()) && !checkContiguity(Point::y, points)) return false;

            if(Arrays.stream(points).mapToInt(Point::y).allMatch(y -> y == points[0].y()) && !checkContiguity(Point::x, points)) return false;

            if(!Arrays.stream(points).mapToInt(Point::x).allMatch(x -> x == points[0].x()) &&
                    !Arrays.stream(points).mapToInt(Point::y).allMatch(y -> y == points[0].y())) return false;
        }

        // Check if tiles have at least one free side
        for (Point point : points) {
            boolean flag = false;
            // Up
            if (point.x() != 0) {
                if (boardMatrix[point.x() - 1][point.y()] == null ||
                        boardMatrix[point.x() - 1][point.y()].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Down
            if (point.x() != Constants.livingRoomBoardX - 1) {
                if (boardMatrix[point.x() + 1][point.y()] == null ||
                        boardMatrix[point.x() + 1][point.y()].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Left
            if (point.y() != 0) {
                if (boardMatrix[point.x()][point.y() - 1] == null ||
                        boardMatrix[point.x()][point.y() - 1].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Right
            if (point.y() != Constants.livingRoomBoardY - 1) {
                if (boardMatrix[point.x()][point.y() + 1] == null ||
                        boardMatrix[point.x()][point.y() + 1].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if (!flag) return false;
        }

        return true;
    }

    /**
     * This method checks if the tiles at the given are contiguous.
     * @param lambda the lambda function used to get the x or y coordinate of the tile
     * @param points the positions of the tiles to check
     * @return true if the tiles are contiguous, false otherwise
     */
    private static boolean checkContiguity(java.util.function.ToIntFunction<Point> lambda, Point... points) {
        int[] tmp = Arrays.stream(points)
                .mapToInt(lambda)
                .sorted()
                .toArray();
        for (int i = 1; i < tmp.length; i++) {
            if (tmp[i] != (tmp[i - 1] + 1))
                return false;
        }
        return true;
    }

    /**
     * This method checks if the column of the bookshelf where the user wants to place the tiles has enough space.
     * @param bookshelfMatrix the matrix of the bookshelf
     * @param column the column of the bookshelf
     * @param tilesNum the number of tiles to place
     * @return true if the column has enough space, false otherwise
     */
    public static boolean checkIfColumnHasEnoughSpace(Tile[][] bookshelfMatrix, int column, int tilesNum) {
        int counter = 0;
        for (int i = Constants.bookshelfY - 1; i >= 0; i--) {
            if (bookshelfMatrix[column][i] == null) {
                counter++;
                if (counter == tilesNum)
                    return true;
            } else return false;
        }
        return false;
    }
}
