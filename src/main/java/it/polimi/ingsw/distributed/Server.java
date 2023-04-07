package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameList;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface Server extends Remote {
    void join(int gameID, String username) throws RemoteException, ServerNotActiveException;

    void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, ServerNotActiveException;

    void register(Client client) throws RemoteException, ServerNotActiveException;

    String[] getGamesList() throws RemoteException, ServerNotActiveException;
}