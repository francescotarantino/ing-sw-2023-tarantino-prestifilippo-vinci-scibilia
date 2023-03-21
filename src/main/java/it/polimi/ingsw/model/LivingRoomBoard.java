package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.exceptions.IllegalPositionException;
import it.polimi.ingsw.model.exceptions.IllegalTileException;


public class LivingRoomBoard {
    private Tile[][] board;
    public Tile getTile(int x, int y) throws IllegalPositionException{
        if (x < 0|| y < 0 || x > 8 || y > 8) {
            throw new IndexOutOfBoundsException();
        }
        else if (board[x][y] != null) {
            throw new IllegalPositionException();
        }
        return board[x][y];
    }
    public void insertTile(Tile t, int x, int y) throws IllegalPositionException, IllegalTileException{
        if (x < 0|| y < 0 || x > 8 || y > 8) {
            throw new IndexOutOfBoundsException();
        }
        else if (board[x][y] != null) {
            throw new IllegalPositionException();
        }
        else if (t.equals(new Tile(Constants.TileType.PLACEHOLDER))) {
            throw new IllegalTileException();
        }
    }
    public void removeTile(int x, int y) throws IllegalPositionException{
        if (board[x][y].equals(new Tile(Constants.TileType.PLACEHOLDER))) {
            throw new IllegalPositionException();
        }
        board[x][y] = null;
    }
    /* potentially unsafe */
    public Tile[][] getBoard () {
        return board;
    }
    /*creator*/
    public LivingRoomBoard(int numPlayers) {
        if (numPlayers > 4 || numPlayers < 2) {
            throw new IndexOutOfBoundsException();
        }
        board = new Tile[9][9];
        int invalidPositionsFourPlayers[][] = {
            {0, 0},
            {0, 1},
            {0, 2},
            {0, 5},
            {0, 6},
            {0, 7},
            {0, 8},
            {1, 0},
            {1, 1},
            {1, 2},
            {1, 6},
            {1, 7},
            {1, 8},
            {2, 0},
            {2, 1},
            {2, 7},
            {2, 8},
            {3, 0},
            {5, 8},
            {6, 0},
            {6, 1},
            {6, 7},
            {6, 8},
            {7, 0},
            {7, 1},
            {7, 2},
            {7, 6},
            {7, 7},
            {7, 8},
            {8, 0},
            {8, 1},
            {8, 2},
            {8, 3},
            {8, 6},
            {8, 7},
            {8, 8},
        };
        int invalidPositionsThreePlayers[][] = {
            {0, 0},
            {0, 1},
            {0, 2},
            {0, 5},
            {0, 6},
            {0, 7},
            {0, 8},
            {1, 0},
            {1, 1},
            {1, 2},
            {1, 6},
            {1, 7},
            {1, 8},
            {2, 0},
            {2, 1},
            {2, 7},
            {2, 8},
            {3, 0},
            {5, 8},
            {6, 0},
            {6, 1},
            {6, 7},
            {6, 8},
            {7, 0},
            {7, 1},
            {7, 2},
            {7, 6},
            {7, 7},
            {7, 8},
            {8, 0},
            {8, 1},
            {8, 2},
            {8, 3},
            {8, 6},
            {8, 7},
            {8, 8},
            /*extra positions that must be removed just for two or three players game*/
            {0, 4},
            {1, 5},
            {3, 2},
            {4, 0},
            {4, 8},
            {5, 7},
            {7, 3},
            {8, 4}
        };
        int invalidPositionsTwoPlayers[][] = {
            {0, 0},
            {0, 1},
            {0, 2},
            {0, 5},
            {0, 6},
            {0, 7},
            {0, 8},
            {1, 0},
            {1, 1},
            {1, 2},
            {1, 6},
            {1, 7},
            {1, 8},
            {2, 0},
            {2, 1},
            {2, 7},
            {2, 8},
            {3, 0},
            {5, 8},
            {6, 0},
            {6, 1},
            {6, 7},
            {6, 8},
            {7, 0},
            {7, 1},
            {7, 2},
            {7, 6},
            {7, 7},
            {7, 8},
            {8, 0},
            {8, 1},
            {8, 2},
            {8, 3},
            {8, 6},
            {8, 7},
            {8, 8},
            /*extra positions that must be removed just for two or three players game*/
            {0, 4},
            {1, 5},
            {3, 2},
            {4, 0},
            {4, 8},
            {5, 7},
            {7, 3},
            {8, 4},
            /*extra positions that must be removed just for two players game*/
            {0, 3},
            {2, 2},
            {2, 6},
            {3, 8},
            {5, 0},
            {6, 2},
            {6, 6},
            {8, 5}
        };
        switch (numPlayers) {
            case 4 -> {
                for (int i = 0; i < 9; i++) {
                    board[invalidPositionsFourPlayers[i][0]][invalidPositionsFourPlayers[i][1]] = new Tile(Constants.TileType.PLACEHOLDER);
                }
            }
            case 3 -> {
                for (int i = 0; i < 9; i++) {
                    board[invalidPositionsThreePlayers[i][0]][invalidPositionsThreePlayers[i][1]] = new Tile(Constants.TileType.PLACEHOLDER);
                }
            }
            case 2 -> {
                for (int i = 0; i < 9; i++) {
                    board[invalidPositionsTwoPlayers[i][0]][invalidPositionsTwoPlayers[i][1]] = new Tile(Constants.TileType.PLACEHOLDER);
                }
            }
        }
    }
}