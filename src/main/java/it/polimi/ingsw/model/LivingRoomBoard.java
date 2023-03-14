package it.polimi.ingsw.model;
public class LivingRoomBoard {
    public Tile[][] board;

    public Tile getTile(int x, int y) {
        return board[x][y];
    }

    public void insertTile(Tile t, int x, int y) {
        board [x][y] = t;
    }
    public void removeTile(int x, int y) {
        board[x][y] = null;
    }
    public Tile[][] getBoard () {
        return board;
    }
    /*creator*/
    public LivingRoomBoard(int numPlayers) {
        if (numPlayers > 4 || numPlayers < 2) {
         /* error */
        }
        board = new Tile[9][9];
        board[0][0] = new Tile(TileType.PLACEHOLDER);
        board[0][1] = new Tile(TileType.PLACEHOLDER);
        board[0][2] = new Tile(TileType.PLACEHOLDER);
        board[0][5] = new Tile(TileType.PLACEHOLDER);
        board[0][6] = new Tile(TileType.PLACEHOLDER);
        board[0][7] = new Tile(TileType.PLACEHOLDER);
        board[0][8] = new Tile(TileType.PLACEHOLDER);
        board[1][0] = new Tile(TileType.PLACEHOLDER);
        board[1][1] = new Tile(TileType.PLACEHOLDER);
        board[1][2] = new Tile(TileType.PLACEHOLDER);
        board[1][6] = new Tile(TileType.PLACEHOLDER);
        board[1][7] = new Tile(TileType.PLACEHOLDER);
        board[1][8] = new Tile(TileType.PLACEHOLDER);
        board[2][0] = new Tile(TileType.PLACEHOLDER);
        board[2][1] = new Tile(TileType.PLACEHOLDER);
        board[2][7] = new Tile(TileType.PLACEHOLDER);
        board[2][8] = new Tile(TileType.PLACEHOLDER);
        board[3][0] = new Tile(TileType.PLACEHOLDER);
        board[5][8] = new Tile(TileType.PLACEHOLDER);
        board[6][0] = new Tile(TileType.PLACEHOLDER);
        board[6][1] = new Tile(TileType.PLACEHOLDER);
        board[6][7] = new Tile(TileType.PLACEHOLDER);
        board[6][8] = new Tile(TileType.PLACEHOLDER);
        board[7][0] = new Tile(TileType.PLACEHOLDER);
        board[7][1] = new Tile(TileType.PLACEHOLDER);
        board[7][2] = new Tile(TileType.PLACEHOLDER);
        board[7][6] = new Tile(TileType.PLACEHOLDER);
        board[7][7] = new Tile(TileType.PLACEHOLDER);
        board[7][8] = new Tile(TileType.PLACEHOLDER);
        board[8][0] = new Tile(TileType.PLACEHOLDER);
        board[8][1] = new Tile(TileType.PLACEHOLDER);
        board[8][2] = new Tile(TileType.PLACEHOLDER);
        board[8][3] = new Tile(TileType.PLACEHOLDER);
        board[8][6] = new Tile(TileType.PLACEHOLDER);
        board[8][7] = new Tile(TileType.PLACEHOLDER);
        board[8][8] = new Tile(TileType.PLACEHOLDER);
        if (numPlayers < 4) {
            board[0][4] = new Tile(TileType.PLACEHOLDER);
            board[1][5] = new Tile(TileType.PLACEHOLDER);
            board[3][2] = new Tile(TileType.PLACEHOLDER);
            board[4][0] = new Tile(TileType.PLACEHOLDER);
            board[4][8] = new Tile(TileType.PLACEHOLDER);
            board[5][7] = new Tile(TileType.PLACEHOLDER);
            board[7][3] = new Tile(TileType.PLACEHOLDER);
            board[8][4] = new Tile(TileType.PLACEHOLDER);
        }
        if (numPlayers < 3) {
            board[0][3] = new Tile(TileType.PLACEHOLDER);
            board[2][2] = new Tile(TileType.PLACEHOLDER);
            board[2][6] = new Tile(TileType.PLACEHOLDER);
            board[3][8] = new Tile(TileType.PLACEHOLDER);
            board[5][0] = new Tile(TileType.PLACEHOLDER);
            board[6][2] = new Tile(TileType.PLACEHOLDER);
            board[6][6] = new Tile(TileType.PLACEHOLDER);
            board[8][5] = new Tile(TileType.PLACEHOLDER);
        }
    }
}