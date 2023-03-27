package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;

public class Bookshelf {
    private final Player player;

    private final PersonalGoalCard personalGoalCard;

    private final Tile[][] matrix;

    public Bookshelf(Player newPlayer, PersonalGoalCard personalGoalCard) {
        this.player = newPlayer;
        this.personalGoalCard = personalGoalCard;
        this.matrix = new Tile[Constants.bookshelfX][Constants.bookshelfY];
    }

    public Player getPlayer() {
        return this.player;
    }

    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }

    public Tile getTile(Point point) {
        if(point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY)
            throw new IndexOutOfBoundsException("Invalid bookshelf coordinates");

        return this.matrix[point.getX()][point.getY()];
    }

    public void insertTile(Point point, Tile tile) {
        if (point == null || tile == null){
            throw new NullPointerException("Arguments cannot be null");
        } else if(point.getX() < 0 || point.getX() >= Constants.bookshelfX || point.getY() < 0 || point.getY() >= Constants.bookshelfY){
            throw new IndexOutOfBoundsException("Invalid bookshelf coordinates");
        } else if (this.matrix[point.getX()][point.getY()] != null){
            throw new IllegalArgumentException("There is already a tile in that position");
        } else if (point.getY() != 0 && this.matrix[point.getX()][point.getY()-1] == null){
            throw new IllegalArgumentException("There is no tile below, gravity is not respected");
        }

        this.matrix[point.getX()][point.getY()] = tile;
    }
}
