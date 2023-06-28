package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.GameUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a player in the game.
 */
public class Player {
    /**
     * The username of the player.
     * It is unique in a game, but not in the whole server (it is possible to have two players with the same username in two different games).
     */
    private final String username;
    /**
     * The points of the player.
     * This value is updated at the end of the game.
     */
    private int points;
    /**
     * The scoring tokens earned by the player.
     */
    private final List<Integer> scoringTokens = new ArrayList<>();
    /**
     * The connection status of the player.
     * If true, the player is online.
     */
    private boolean connected;
    /**
     * The points of the living room board where the player has taken the tiles from in the last move.
     * If null, the player has not made any move yet.
     */
    private Point[] lastMovePoints = null;
    /**
     * The tiles involved in the last move made by the player.
     */
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

        if(token != Constants.endGameToken && Arrays.stream(GameUtils.getScoringTokens(numberOfPlayers)).noneMatch(x -> x == token)){
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
