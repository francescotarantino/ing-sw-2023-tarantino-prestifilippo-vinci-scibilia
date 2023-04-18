package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

public interface GameListListener {
    void newGame() throws RemoteException;
    void removedGame() throws RemoteException;
    void playerJoinedGame(int gameID) throws RemoteException;
    void gameIsFull(int gameID) throws RemoteException;
}
