package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Point;

import java.rmi.RemoteException;

public interface GameUIListener extends Listener {
    /**
     * This method is called when the GameUI wants to perform a turn.
     * @param column the column where to put the tiles
     * @param points the points of the tiles from the living room board
     */
    void performTurn(int column, Point...points) throws RemoteException;
}
