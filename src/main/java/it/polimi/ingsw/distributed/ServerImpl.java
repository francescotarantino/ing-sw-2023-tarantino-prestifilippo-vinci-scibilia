package it.polimi.ingsw.distributed;

import it.polimi.ingsw.AppServerImpl;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private Game model;
    private Controller controller;


    public ServerImpl() throws RemoteException {
        super();
    }

    public ServerImpl(int port) throws RemoteException {
        super(port);
    }

    public ServerImpl(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public void join(Client client, int gameID, String username) throws RemoteException, ServerNotActiveException {
        System.out.println(getClientHost() + " is joining game " + gameID + " with username " + username + "...");

        this.model = AppServerImpl.getGame(gameID);
        if(this.model == null){
            throw new IllegalArgumentException();
        }
        this.model.addBookshelf(new Player(username));

        this.controller = new Controller(this.model/*, view*/);
    }

    @Override
    public void create(Client client, int numberOfPlayers, String username) throws RemoteException, ServerNotActiveException {
        int gameID = new Random().nextInt(998) + 1;

        System.out.println(getClientHost() + " is creating game " + gameID + " with username " + username + "...");
        this.model = new Game(gameID, numberOfPlayers, new Player(username), 2); // TODO: ask for number of Command Goal Cards

        AppServerImpl.insertGame(this.model);

        this.controller = new Controller(this.model/*, view */);
    }
}

