package it.polimi.ingsw.distributed;

import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.view.textual.GameUI;
import it.polimi.ingsw.view.textual.StartUI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ClientImpl extends UnicastRemoteObject implements Client, Runnable, StartUIListener {
    private final Server server;
    private final StartUI startUI = new StartUI();
    private final GameUI gameUI = new GameUI();

    public ClientImpl(Server server) throws RemoteException {
        this.server = server;
        initialize();
    }

    public ClientImpl(Server server, int port) throws RemoteException {
        super(port);
        this.server = server;
        initialize();
    }

    public ClientImpl(Server server, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
        this.server = server;
        initialize();
    }

    public void initialize() throws RemoteException {
        this.server.register(this);
        startUI.addListener(this);
    }

    @Override
    public void run() {
        System.out.println("Starting StartUI...");
        startUI.run();
    }

    @Override
    public void refreshStartUI() throws RemoteException {
        server.getGamesList();
    }

    @Override
    public void createGame(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException {
        try {
            this.server.create(numberOfPlayers, numberOfCommonGoalCards, username);
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage(), false);
        }
    }

    @Override
    public void joinGame(int gameID, String username) throws RemoteException {
        try {
            this.server.addPlayerToGame(gameID, username);
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage(), false);
        }
    }

    @Override
    public void updateGamesList(String[] o) throws RemoteException {
        startUI.showGamesList(o);
    }

    @Override
    public void showError(String err, boolean exit) throws RemoteException {
        startUI.showError(err, exit);
    }

    @Override
    public void updatePlayersList(ArrayList<String> o) throws RemoteException {
        startUI.showPlayersList(o);
    }

    @Override
    public void gameHasStarted() throws RemoteException {
        System.out.println("Starting GameUI...");
        new Thread(gameUI).start(); // TODO: check if it's a good practice
    }
}
