package it.polimi.ingsw.model.goal_cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.Tile;

import java.util.HashMap;

import static it.polimi.ingsw.Utils.checkMatrixSize;


public class CGCGroupsOfEquals extends CommonGoalCard {
    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    public enum BlockType{
        FOUR_OF_FOUR,
        SIX_OF_TWO,
        TWO_SQUARES
    }
    
    private final BlockType blockType;
    
    public CGCGroupsOfEquals(int numberOfPlayers, int ID, BlockType blockType) {
        super(numberOfPlayers, ID);
        this.blockType = blockType;
    }
    
    private boolean checkAdjacentTile(int x, int y, Tile[][] matrix, Direction direction) {
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
    private int findGroup(int x, int y, Tile[][] matrix, boolean[][] done) {
        if (done[x][y]) {
            return 0;
        }
        done[x][y] = true;
        int currentSize = 1;
            for(Direction direction: Direction.values()) {
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
    private boolean findSquare(int i, int j, Tile[][] matrix) {
        return checkAdjacentTile(i, j, matrix, Direction.UP) &&
                checkAdjacentTile(i, j, matrix, Direction.RIGHT) &&
                checkAdjacentTile(i + 1, j, matrix, Direction.UP) &&
                !checkAdjacentTile(i, j, matrix, Direction.LEFT) &&
                !checkAdjacentTile(i + 1, j, matrix, Direction.LEFT) &&
                !checkAdjacentTile(i + 1, j, matrix, Direction.UP) &&
                !checkAdjacentTile(i + 1, j + 1, matrix, Direction.UP) &&
                !checkAdjacentTile(i + 1, j + 1, matrix, Direction.RIGHT) &&
                !checkAdjacentTile(i, j + 1, matrix, Direction.RIGHT) &&
                !checkAdjacentTile(i, j + 1, matrix, Direction.DOWN);
    }
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
        boolean[][] done = new boolean[6][6];
        int groupsFound = 0;
        switch (blockType) {
            case SIX_OF_TWO -> {
                for (int i = 0; i < Constants.bookshelfX; i++) {
                    for (int j = 0; j < Constants.bookshelfY; j++) {
                        if(!done[i][j]) {
                            if(findGroup(i, j, matrix, done) >= 2) {
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
                            if(findGroup(i, j, matrix, done) >= 4) {
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
                int number;
                for (int i = 0; i < Constants.bookshelfX - 1; i++) {
                    for (int j = 0; j < Constants.bookshelfY - 1; j++) {
                        if(!done[i][j]) {
                            if (findSquare(i, j, matrix)) {
                                setDone(i, j, done);
                                number = numberOfSquares.getOrDefault(matrix[i][j].getType(), 0);
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
