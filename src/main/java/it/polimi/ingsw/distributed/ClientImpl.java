package it.polimi.ingsw.distributed;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements Client, Runnable {
    public ClientImpl() throws RemoteException {
    }

    public ClientImpl(int port) throws RemoteException {
        super(port);
    }

    public ClientImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    public void create(Server server, int numberOfPlayers, String username) throws RemoteException, ServerNotActiveException {
        server.create(this, numberOfPlayers, username);
    }

    public void join(Server server, int gameID, String username) throws RemoteException, ServerNotActiveException {
        server.join(this, gameID, username);
    }

    @Override
    public void run() {
        System.out.println("Starting view...");
        // TODO: start view
    }
}
