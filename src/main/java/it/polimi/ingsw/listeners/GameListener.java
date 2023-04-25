package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;

public interface GameListener extends Listener {
    /**
     * This method is invoked by the {@link it.polimi.ingsw.model.Game} class when the model has a significant change.
     */
    void modelChanged() throws RemoteException;
}
