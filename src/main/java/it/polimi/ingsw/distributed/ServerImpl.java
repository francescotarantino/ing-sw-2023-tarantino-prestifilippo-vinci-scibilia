package it.polimi.ingsw.distributed;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.util.Observer;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
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
    public void register(Client client) throws RemoteException {
        this.client = client;
        GameList.getInstance().addObserver(this);

        System.out.println("A client is registering to the server...");
    }

    @Override
    public void join(int gameID, String username) throws RemoteException, InvalidChoiceException {
        System.out.println("A client is joining game " + gameID + " with username " + username + "...");

        this.model = GameList.getInstance().getGame(gameID);
        if(this.model == null){
            throw new InvalidChoiceException("Partita non trovata.");
        }

        try {
            this.model.addBookshelf(new Player(username));
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidChoiceException(e.getMessage());
        }

        GameList.getInstance().deleteObserver(this);
        GameList.getInstance().setChangedAndNotify(GameList.Event.PLAYER_JOINED_GAME);

        this.controller = new Controller(this.model/*, view*/);
    }

    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException {
        int gameID = new Random().nextInt(998) + 1;

        System.out.println("A client is creating game " + gameID + " with username " + username + "...");

        try {
            this.model = new Game(gameID, numberOfPlayers, new Player(username), numberOfCommonGoalCards);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidChoiceException(e.getMessage());
        }

        GameList.getInstance().deleteObserver(this);
        GameList.getInstance().addGame(this.model);

        this.controller = new Controller(this.model/*, view */);
    }

    @Override
    public void getGamesList() throws RemoteException {
        update(GameList.getInstance(), GameList.Event.NEW_GAME);
    }

    @Override
    public void update(GameList o, GameList.Event arg) {
        try {
            this.client.updateGamesList(o.getGamesString(), arg);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}

