package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

/**
 * This interface identifies a listener for the {@link it.polimi.ingsw.model.Game} class.
 *
 * @see it.polimi.ingsw.model.Game
 */
public interface GameListener extends Listener {
    /**
     * This method is invoked by the {@link it.polimi.ingsw.model.Game} class when the model has a significant change.
     * It is triggered only during the game.
     */
    void modelChanged() throws RemoteException;

    /**
     * This method is invoked by the {@link it.polimi.ingsw.model.Game} class when the game is ended.
     * It should trigger the end of the game for the client.
     */
    void gameEnded() throws RemoteException;

    /**
     * This method is invoked when a player joins the game.
     */
    void playerJoinedGame() throws RemoteException;

    /**
     * This method is invoked when the game becomes full.
     */
    void gameIsFull() throws RemoteException;
}
