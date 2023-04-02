package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private final String username;
    private int points;
    private final ArrayList<Integer> scoringTokens = new ArrayList<>();

    /**
     * Creates a new player with the given name and 0 points
     * @param username the name of the player
     */
    public Player(String username){
        this.username = username;
        this.points = 0;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPoints(){
        return this.points;
    }

    /**
     * Adds the given points to the player's points counter
     * @param newPoints the points to add
     */
    public void addPoints(int newPoints){
        this.points += newPoints;
    }

    /**
     * Adds a scoring token to the player's scoring tokens list
     * @param token the token to add
     */
    public void addScoringToken(int token){
        if(Arrays.stream(Constants.allScoringTokens).noneMatch(x -> x == token)){
            throw new IllegalArgumentException(token + " is not a valid scoring token supported by this game");
        }

        this.scoringTokens.add(token);
    }

    public ArrayList<Integer> getScoringTokens(){
        return new ArrayList<>(this.scoringTokens);
    }

    @Override
    public String toString() {
        return "@" + username + " (" + points + ")";
    }
}
