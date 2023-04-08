package it.polimi.ingsw.distributed;

import it.polimi.ingsw.model.GameList;
import it.polimi.ingsw.util.Observer;
import it.polimi.ingsw.view.textual.StartUI;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements Client, Runnable, Observer<StartUI, StartUI.Event> {
    private final Server server;
    private final StartUI startUI = new StartUI();

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
        startUI.addObserver(this);
    }

    private void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException {
        this.server.create(numberOfPlayers, numberOfCommonGoalCards, username);
    }

    private void join(int gameID, String username) throws RemoteException {
        this.server.join(gameID, username);
    }

    @Override
    public void run() {
        System.out.println("Starting StartUI...");
        startUI.run();
    }

    @Override
    public void update(StartUI o, StartUI.Event arg) {
        switch (arg) {
            case CREATE -> {
                try {
                    create(o.getNumberOfPlayers(), o.getNumberOfCommonGoalCards(), o.getUsername());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            case JOIN -> {
                try {
                    join(o.getGameID(), o.getUsername());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            case REFRESH -> {
                try {
                    server.getGamesList();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateGamesList(String[] o, GameList.Event e) throws RemoteException {
        startUI.showGamesList(o);
    }
}
