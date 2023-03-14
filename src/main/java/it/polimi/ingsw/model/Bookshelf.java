package it.polimi.ingsw.model;

public class Bookshelf {
    private final String playerName;

    private PersonalGoalCard personalGoalCard;

    private Tile[][] matrix;

    public Bookshelf(String playerName, PersonalGoalCard personalGoalCard) {
        this.playerName = playerName;
        this.personalGoalCard = personalGoalCard;

        this.matrix = new Tile[5][6];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                this.matrix[i][j] = null; // Forse è inutile
            }
        }
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }

    public Tile getTile(int x, int y) {
        if(x < 0 || x >= 5 || y < 0 || y >= 6)
            throw new IndexOutOfBoundsException();

        return this.matrix[x][y];
    }

    public void insertTile(int x, int y, Tile tile) {
        this.matrix[x][y] = tile;
    }
}
