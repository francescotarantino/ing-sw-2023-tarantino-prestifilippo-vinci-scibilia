package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameList;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void updateGamesList(String[] o, GameList.Event e) throws RemoteException;
}
