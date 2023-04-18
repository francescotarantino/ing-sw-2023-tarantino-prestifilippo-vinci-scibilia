package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

public interface StartUIListener {
    void refreshStartUI() throws RemoteException;
    void createGame(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException;
    void joinGame(int gameID, String username) throws RemoteException;
}
