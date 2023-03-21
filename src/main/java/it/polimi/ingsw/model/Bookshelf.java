package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.exceptions.GravityException;
import it.polimi.ingsw.model.exceptions.IllegalPositionException;

public class Bookshelf {
    private final String playerName;

    private PersonalGoalCard personalGoalCard;

    private Tile[][] matrix;

    public Bookshelf(String playerName, PersonalGoalCard personalGoalCard) {
        this.playerName = playerName;
        this.personalGoalCard = personalGoalCard;

        this.matrix = new Tile[Constants.bookshelfX][Constants.bookshelfY];
        for (int i = 0; i < Constants.bookshelfX; i++) {
            for (int j = 0; j < Constants.bookshelfY; j++) {
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

    public Tile getTile(Point point) {
        if(point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY)
            throw new IndexOutOfBoundsException();

        return this.matrix[point.getX()][point.getY()];
    }

    public void insertTile(Point point, Tile tile) throws IllegalPositionException, GravityException {
        if(point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY){
            throw new IndexOutOfBoundsException();
        } else if (this.matrix[point.getX()][point.getY()] != null){ // cannot replace a tile that is already present
            throw new IllegalPositionException();
        } else if (point.getY() != 0 && this.matrix[point.getX()][point.getY()-1] == null){ // check if there is a tile under the one is being inserted
            throw new GravityException();
        }

        this.matrix[point.getX()][point.getY()] = tile;
    }
}
