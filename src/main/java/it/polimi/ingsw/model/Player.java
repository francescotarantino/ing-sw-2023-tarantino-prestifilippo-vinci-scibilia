package it.polimi.ingsw.model;

public class Player {
    private final String username;
    private int points;

    public Player(String newName){
        this.username = newName;
        this.points = 0;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPoints(){
        return this.points;
    }

    public void addPoints(int newPoints){
        this.points += newPoints;
    }

    @Override
    public String toString() {
        return "@" + username + " (" + points + ")";
    }
}
