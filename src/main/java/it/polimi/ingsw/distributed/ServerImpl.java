package it.polimi.ingsw.distributed;

import it.polimi.ingsw.AppServerImpl;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class ServerImpl extends UnicastRemoteObject implements Server {
    private Game model; // TODO: implements multiple game
    private Controller controller; // same here


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
    public void join(Client client, int gameID, String username) throws RemoteException {
        System.out.println("Registering client...");

        this.model = AppServerImpl.getGame(gameID);
        this.model.addBookshelf(new Player(username));

        this.controller = new Controller(this.model/*, view*/);

        // TODO: cerca la partita già pronta
    }

    @Override
    public void create(Client client, int numberOfPlayers, String username) throws RemoteException {
        System.out.println("Registering client...");
        this.model = new Game(new Random().nextInt(998) + 1, numberOfPlayers, new Player(username), 2);

        AppServerImpl.insertGame(this.model);

        this.controller = new Controller(this.model/*, view */);
    }
}

