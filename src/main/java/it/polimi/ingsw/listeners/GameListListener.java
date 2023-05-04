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
     * This method is invoked when a game in the game list is updated (e.g. new player joined).
     */
    void updatedGame() throws RemoteException;
}
