package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

/**
 * This interface identifies a listener for the {@link it.polimi.ingsw.model.GameList} class.
 *
 * @see it.polimi.ingsw.model.GameList
 */
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
