package it.polimi.ingsw;

import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;

import java.util.Arrays;

public class Utils {
    /**
     * Checks if the matrix has the correct size of the bookshelf (Constants.bookshelfX x Constants.bookshelfY)
     *
     * @param matrix matrix to check
     * @param <T>    type of the matrix
     */
    public static <T> void checkMatrixSize(T[][] matrix) {
        if (matrix.length != Constants.bookshelfX)
            throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
        for (T[] tiles : matrix)
            if (tiles.length != Constants.bookshelfY)
                throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
    }

    /**
     * @return is the size of the group found starting from the tile in position (x,y) in the matrix
     * The method is recursive, and it builds the biggest possible block by checking the adjacent tiles in all directions
     */
    public static int findGroup(Point point, Tile[][] matrix, boolean[][] done) {
        if (done[point.getX()][point.getY()]) {
            return 0;
        }
        done[point.getX()][point.getY()] = true;
        if(matrix[point.getX()][point.getY()] == null) {
            return 0;
        }
        int currentSize = 1;
        for (Constants.Direction direction : Constants.Direction.values()) {
            Tile tile = checkAdjacentTile(point, matrix, direction);
            if (tile != null && tile.sameType(matrix[point.getX()][point.getY()])) {
                switch (direction) {
                    case UP -> currentSize += findGroup(new Point(point.getX(), point.getY() + 1), matrix, done);
                    case RIGHT -> currentSize += findGroup(new Point(point.getX() + 1, point.getY()), matrix, done);
                    case DOWN -> currentSize += findGroup(new Point(point.getX(), point.getY() - 1), matrix, done);
                    case LEFT -> currentSize += findGroup(new Point(point.getX() - 1, point.getY()), matrix, done);
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
        if (point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY) {
            return null;
        }
        switch (direction) {
            case UP -> {
                return matrix[point.getX()][point.getY() + 1];
            }
            case RIGHT -> {
                return matrix[point.getX() + 1][point.getY()];
            }
            case DOWN -> {
                return matrix[point.getX()][point.getY() - 1];
            }
            case LEFT -> {
                return matrix[point.getX() - 1][point.getY()];
            }
            default -> throw new IllegalStateException("Invalid direction: " + direction);
        }
    }

    /**
     * TODO
     * @param boardMatrix
     * @param points
     * @return
     */
    public static boolean checkIfTilesCanBeTaken(Tile[][] boardMatrix, Point... points) {
        // Checks if tiles are adjacent
        if (
                points.length != 1 &&
                        (
                                (!(points[0].getX() == points[1].getX() && checkContiguity(Point::getY, points))) ||
                                        (!(points[0].getY() == points[1].getY() && checkContiguity(Point::getX, points)))
                        )
        )
            return false;

        // Checks if tiles have at least one free side
        for (Point point : points) {
            boolean flag = false;
            // Up
            if (point.getX() != 0) {
                if (boardMatrix[point.getX() - 1][point.getY()] == null ||
                        boardMatrix[point.getX() - 1][point.getY()].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Down
            if (point.getX() != Constants.livingRoomBoardX - 1) {
                if (boardMatrix[point.getX() + 1][point.getY()] == null ||
                        boardMatrix[point.getX() + 1][point.getY()].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Left
            if (point.getY() != 0) {
                if (boardMatrix[point.getX()][point.getY() - 1] == null ||
                        boardMatrix[point.getX()][point.getY() - 1].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Right
            if (point.getY() != Constants.livingRoomBoardY - 1) {
                if (boardMatrix[point.getX()][point.getY() + 1] == null ||
                        boardMatrix[point.getX()][point.getY() + 1].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if (!flag) return false;
        }

        return true;
    }

    /**
     * TODO
     * @param lambda
     * @param points
     * @return
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
     * TODO
     * @param bookshelfMatrix
     * @param column
     * @param tilesNum
     * @return
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