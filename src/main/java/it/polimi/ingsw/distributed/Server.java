package it.polimi.ingsw.distributed;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
        void register (Client client) throws RemoteException;
        void update (Client client, Data arg) throws RemoteException;
}
