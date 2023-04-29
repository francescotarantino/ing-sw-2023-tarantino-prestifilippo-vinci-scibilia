package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

public interface StartUIListener extends Listener {
    /**
     * This method is invoked when the startUI needs a refresh of the current game list.
     */
    void refreshStartUI() throws RemoteException;

    /**
     * This method is invoked when startUI wants to create a new game.
     */
    void createGame(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException;

    /**
     * This method is invoked when startUI wants to join a game.
     */
    void joinGame(int gameID, String username) throws RemoteException;

    /**
     * This method is invoked when startUI wants to exit.
     */
    void exit() throws RemoteException;
}
