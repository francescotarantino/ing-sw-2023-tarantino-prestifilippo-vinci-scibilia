package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
    private final String username;
    private int points;
    private final List<Integer> scoringTokens = new ArrayList<>();
    private boolean connected;
    private Point[] lastMovePoints = null;
    private Tile[] lastMoveTiles = null;

    /**
     * Creates a new player with the given name and 0 points
     * @param username the name of the player
     */
    public Player(String username){
        this.username = username;
        this.points = 0;
        this.connected = true;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPoints(){
        return this.points;
    }

    /**
     * Adds the given points to the player's point counter
     * @param newPoints the points to add
     */
    public void addPoints(int newPoints){
        this.points += newPoints;
    }

    /**
     * Adds a scoring token to the player's scoring tokens list
     * @param token the token to add
     * @param numberOfPlayers the current number of players in the game
     */
    public void addScoringToken(int token, int numberOfPlayers){
        if(token == 0){
            return;
        }

        if(token != Constants.endGameToken && Arrays.stream(Constants.getScoringTokens(numberOfPlayers)).noneMatch(x -> x == token)){
            throw new IllegalArgumentException(token + " is not a valid scoring token supported by this game");
        }

        this.scoringTokens.add(token);
    }
    public void setLastMove(Tile[] tiles, Point[] points){
        this.lastMoveTiles = tiles;
        this.lastMovePoints = points;
    }

    public List<Integer> getScoringTokens(){
        return new ArrayList<>(this.scoringTokens);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean disconnected) {
        this.connected = disconnected;
    }
    public Tile[] getLastMoveTiles(){
        return this.lastMoveTiles;
    }
    public Point[] getLastMovePoints(){
        return this.lastMovePoints;
    }

    @Override
    public String toString() {
        if(!isConnected()){
            return "@" + username + " (disconnected)";
        } else {
            return "@" + username;
        }
    }
}
