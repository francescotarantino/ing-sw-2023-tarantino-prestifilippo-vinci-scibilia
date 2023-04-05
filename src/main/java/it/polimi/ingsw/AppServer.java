package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppServer extends Remote {
    /**
     * Connect to the server
     * @return the ServerImpl class
     */
    Server connect() throws RemoteException;

    /**
     * Get the list of games
     * @return a formatted list of games
     */
    String[] getGamesString() throws RemoteException;
}
