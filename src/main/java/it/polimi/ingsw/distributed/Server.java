package it.polimi.ingsw.distributed;

import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.model.Point;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents all the methods that can be called by the client on the server.
 *
 * @see ServerImpl
 */
public interface Server extends Remote {
    /**
     * Register a client to the server.
     * @param client the client to register
     */
    void register(Client client) throws RemoteException;

    /**
     * This method is called by the client to join an already existing game.
     * @param gameID the ID of the game to join
     * @param username the username of the player
     * @throws InvalidChoiceException if the player cannot join the game (e.g. invalid gameID, the game is full, the username is already taken, etc.)
     */
    void addPlayerToGame(int gameID, String username) throws RemoteException, InvalidChoiceException;

    /**
     * This method is called by the client to create a new game.
     * @param numberOfPlayers the number of players in the game
     * @param numberOfCommonGoalCards the number of common goal cards to use in the game
     * @param username the username of the player
     * @throws InvalidChoiceException if the player cannot create the game
     */
    void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException;

    /**
     * This method is called by the client to get the list of games.
     * The client will be notified of the list of games using the {@link Client#updateGamesList(java.util.List)} method.
     */
    void getGamesList() throws RemoteException;

    /**
     * This method is called by the client to perform a turn.
     * @param column the column where to put the tiles
     * @param points the points of the tiles from the living room board
     */
    void performTurn(int column, Point...points) throws RemoteException;

    /**
     * This method is called by the client to respond to a ping sent by the server.
     * @see Client#ping()
     */
    void pong() throws RemoteException;
}