package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.Game;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface AppServer extends Remote {
    /**
     * Connect to the server
     * @return the server
     */
    Server connect() throws RemoteException;

    /**
     * Get the list of games
     *
     * @return the list of games
     */
    String[] getGamesString() throws RemoteException;
}
