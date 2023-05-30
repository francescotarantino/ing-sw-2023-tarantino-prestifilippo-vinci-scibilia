package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;

import java.util.HashMap;

import static it.polimi.ingsw.utils.GameUtils.checkMatrixSize;

public class CGCSquares extends CommonGoalCard {
    final int squareSize;
    public CGCSquares(int numberOfPlayers, int ID, int size) {
        super(numberOfPlayers, ID);
        squareSize = size;
    }
    private boolean checkSquare(Point point, Tile[][] matrix) {
        if (matrix[point.x()][point.y()] == null) {
            return false;
        }
        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                if (matrix[point.x() + i][point.y() + j] == null ||
                    !matrix[point.x() + i][point.y() + j].sameType(matrix[point.x()][point.y()])) {
                    return false;
                }
            }
        }
        return true;
    }
    private void setDone(Point point, boolean[][] done) {
        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                done[point.x() + i][point.y() + j] = true;
            }
        }
    }
    private boolean checkSquareEdges(Point point, Tile[][] matrix) {
        if (matrix[point.x()][point.y()] == null) {
            return false;
        }
       if (point.x() != 0) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.x() - 1][point.y() + i] != null &&
                   matrix[point.x() - 1][point.y() + i].sameType(matrix[point.x()][point.y()])) {
                   return false;
               }
           }
       }
       if (point.y() != 0) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.x() + i][point.y() - 1] != null &&
                   matrix[point.x() + i][point.y() - 1].sameType(matrix[point.x()][point.y()])) {
                   return false;
               }
           }
       }
       if(point.x() < Constants.bookshelfX - 2) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.x() + squareSize][point.y() + i] != null &&
                   matrix[point.x() + squareSize][point.y() + i].sameType(matrix[point.x()][point.y()])) {
                   return false;
               }
           }
       }
       if(point.y() < Constants.bookshelfY - 2) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.x() + i][point.y() + squareSize] != null &&
                   matrix[point.x() + i][point.y() + squareSize].sameType(matrix[point.x()][point.y()])) {
                   return false;
               }
           }
       }
       return true;
    }
    @Override
    public boolean check(Tile[][] matrix) {
        if (matrix == null) {
            throw new NullPointerException();
        }
        boolean[][] done = new boolean[Constants.bookshelfX][Constants.bookshelfY];
        HashMap<Constants.TileType, Integer> numOfSquares = new HashMap<>();
        checkMatrixSize(matrix);
        for (int i = 0; i < Constants.bookshelfX - 1; i++) {
            for (int j = 0; j < Constants.bookshelfY - 1; j++) {
                if (checkSquare(new Point(i, j), matrix) && checkSquareEdges(new Point(i, j), matrix)) {
                    setDone(new Point(i, j), done);
                    int num = numOfSquares.getOrDefault(matrix[i][j].getType(), 0);
                    numOfSquares.put(matrix[i][j].getType(), num + 1);
                }
                for (Constants.TileType type : Constants.TileType.values()) {
                    if (numOfSquares.getOrDefault(type, 0) >= 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
