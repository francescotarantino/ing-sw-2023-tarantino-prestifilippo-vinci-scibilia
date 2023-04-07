package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.Observer;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class ServerImpl extends UnicastRemoteObject implements Server, Observer<GameList, GameList.Event> {
    private Game model;
    private Controller controller;
    private Client client;


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
    public void register(Client client) throws RemoteException, ServerNotActiveException {
        this.client = client;
        GameList.getInstance().addObserver(this);

        System.out.println(getClientHost() + " is registering to the server...");
    }

    @Override
    public void join(int gameID, String username) throws RemoteException, ServerNotActiveException {
        GameList.getInstance().deleteObserver(this);

        System.out.println(getClientHost() + " is joining game " + gameID + " with username " + username + "...");

        this.model = GameList.getInstance().getGame(gameID);
        if(this.model == null){
            throw new IllegalArgumentException();
        }
        this.model.addBookshelf(new Player(username));

        this.controller = new Controller(this.model/*, view*/);
    }

    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, ServerNotActiveException {
        GameList.getInstance().deleteObserver(this);

        int gameID = new Random().nextInt(998) + 1;

        System.out.println(getClientHost() + " is creating game " + gameID + " with username " + username + "...");
        this.model = new Game(gameID, numberOfPlayers, new Player(username), numberOfCommonGoalCards);

        GameList.getInstance().addGame(this.model);

        this.controller = new Controller(this.model/*, view */);
    }

    @Override
    public String[] getGamesList() throws RemoteException {
        return GameList.getInstance().getGamesString();
    }

    @Override
    public void update(GameList o, GameList.Event arg) {
        try {
            this.client.update(o.getGamesString(), arg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}

