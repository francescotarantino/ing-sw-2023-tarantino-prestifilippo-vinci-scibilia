package it.polimi.ingsw.distributed;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void join(Client client, int gameID, String username) throws RemoteException;

    void create(Client client, int numberOfPlayers, String username) throws RemoteException;
}