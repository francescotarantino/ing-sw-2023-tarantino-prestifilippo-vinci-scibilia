package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameList;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Client extends Remote {
    void updateGamesList(String[] o) throws RemoteException;

    void showError(String err, boolean exit) throws RemoteException;

    void updatePlayersList(ArrayList<String> o) throws RemoteException;
}
