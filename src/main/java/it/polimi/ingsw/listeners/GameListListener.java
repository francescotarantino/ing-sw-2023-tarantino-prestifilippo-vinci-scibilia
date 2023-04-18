package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

public interface GameListListener extends Listener {
    /**
     * This method is invoked when there is a new game in the game list.
     */
    void newGame() throws RemoteException;

    /**
     * This method is invoked when a game is removed from the game list.
     */
    void removedGame() throws RemoteException;

    /**
     * This method is invoked when a player joins a game.
     * @param gameID ID of the game where the player joined
     */
    void playerJoinedGame(int gameID) throws RemoteException;

    /**
     * This method is invoked when a game becomes full.
     * @param gameID ID of the game that became full
     */
    void gameIsFull(int gameID) throws RemoteException;
}
