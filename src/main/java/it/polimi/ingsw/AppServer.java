package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppServer extends Remote {
    /**
     * This method is called by the client to connect to the ServerImpl.
     * @return the ServerImpl instance that will be used by the client
     */
    Server connect() throws RemoteException;
}
