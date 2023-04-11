package it.polimi.ingsw.distributed;

import it.polimi.ingsw.exception.InvalidChoiceException;
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

    private void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException {
        this.server.create(numberOfPlayers, numberOfCommonGoalCards, username);
    }

    private void join(int gameID, String username) throws RemoteException, InvalidChoiceException {
        this.server.join(gameID, username);
    }

    @Override
    public void run() {
        System.out.println("Starting StartUI...");
        startUI.run();
    }

    @Override
    public void update(StartUI o, StartUI.Event arg) throws RemoteException {
        try {
            switch (arg) {
                case CREATE -> create(o.getNumberOfPlayers(), o.getNumberOfCommonGoalCards(), o.getUsername());
                case JOIN -> join(o.getGameID(), o.getUsername());
                case REFRESH -> server.getGamesList();
            }
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage(), false);
        }
    }

    @Override
    public void updateGamesList(String[] o, GameList.Event e) throws RemoteException {
        startUI.showGamesList(o);
    }

    @Override
    public void showError(String err, boolean exit) throws RemoteException {
        startUI.showError(err, exit);
    }
}
