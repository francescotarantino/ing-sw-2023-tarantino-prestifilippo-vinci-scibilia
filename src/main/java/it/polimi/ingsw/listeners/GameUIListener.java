package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Point;

import java.rmi.RemoteException;

public interface GameUIListener extends Listener {
    void performTurn(int column, Point...points) throws RemoteException;
}
