package it.polimi.ingsw.distributed;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements Client, Runnable {
    public ClientImpl(Server server, boolean isNewGame, int number, String username) throws RemoteException, ServerNotActiveException {
        super();
        if(isNewGame)
            server.create(this, number, username);
        else
            server.join(this, number, username);
    }

    @Override
    public void run() {
        System.out.println("Starting view...");
        // TODO: start view
    }
}
