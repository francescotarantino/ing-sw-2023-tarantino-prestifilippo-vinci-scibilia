package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import static it.polimi.ingsw.Utils.checkMatrixSize;

public class CGCSquares extends CommonGoalCard {
    final int squareSize;
    public CGCSquares(int numberOfPlayers, int ID, int size) {
        super(numberOfPlayers, ID);
        squareSize = size;
    }
    private boolean checkSquare(Point point, Tile[][] matrix) {
        if (matrix[point.getX()][point.getY()] == null) {
            return false;
        }
        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                if (matrix[point.getX() + i][point.getY() + j] == null ||
                    !matrix[point.getX() + i][point.getY() + j].sameType(matrix[point.getX()][point.getY()])) {
                    return false;
                }
            }
        }
        return true;
    }
    private void setDone(Point point, boolean[][] done) {
        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                done[point.getX() + i][point.getY() + j] = true;
            }
        }
    }
    private boolean checkSquareEdges(Point point, Tile[][] matrix) {
        if (matrix[point.getX()][point.getY()] == null) {
            return false;
        }
       if (point.getX() != 0) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.getX() - 1][point.getY() + i] != null &&
                   matrix[point.getX() - 1][point.getY() + i].sameType(matrix[point.getX()][point.getY()])) {
                   return false;
               }
           }
       }
       if (point.getY() != 0) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.getX() + i][point.getY() - 1] != null &&
                   matrix[point.getX() + i][point.getY() - 1].sameType(matrix[point.getX()][point.getY()])) {
                   return false;
               }
           }
       }
       if(point.getX() < Constants.bookshelfX - 2) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.getX() + squareSize][point.getY() + i] != null &&
                   matrix[point.getX() + squareSize][point.getY() + i].sameType(matrix[point.getX()][point.getY()])) {
                   return false;
               }
           }
       }
       if(point.getY() < Constants.bookshelfY - 2) {
           for (int i = 0; i < squareSize; i++) {
               if (matrix[point.getX() + i][point.getY() + squareSize] != null &&
                   matrix[point.getX() + i][point.getY() + squareSize].sameType(matrix[point.getX()][point.getY()])) {
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
        int numOfSquares = 0;
        checkMatrixSize(matrix);
        for (int i = 0; i < Constants.bookshelfX - 1; i++) {
            for (int j = 0; j < Constants.bookshelfY - 1; j++) {
                if (checkSquare(new Point(i, j), matrix) && checkSquareEdges(new Point(i, j), matrix)) {
                    setDone(new Point(i, j), done);
                    numOfSquares++;
                    if(numOfSquares >= 2) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
