package it.polimi.ingsw.distributed;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Client extends Remote {
    /**
     * This method is called by the server to notify the client of the list of games.
     * @param o the list of games
     */
    void updateGamesList(String[] o) throws RemoteException;

    /**
     * This method is called by the server when there is an error to show.
     * @param err the error message
     * @param exit if true, the client is asked to exit
     */
    void showError(String err, boolean exit) throws RemoteException;

    /**
     * This method is called by the server when the list of players in the current game changes.
     * @param o the list of players
     */
    void updatePlayersList(ArrayList<String> o) throws RemoteException;
}
