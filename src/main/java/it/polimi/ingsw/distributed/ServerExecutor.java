package it.polimi.ingsw.distributed;

import it.polimi.ingsw.exception.InvalidChoiceException;
import it.polimi.ingsw.model.Point;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class wraps the server methods in order to execute them in a separate thread.
 * This is done in order to avoid blocking RMI calls, which could cause delays.
 */
public class ServerExecutor extends UnicastRemoteObject implements Server {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Server server;
    private Client client;

    public ServerExecutor(Server server) throws RemoteException {
        super();
        this.server = server;
    }

    @Override
    public void register(Client client) throws RemoteException {
        this.client = client;
        executorService.submit(() -> {
            try {
                server.register(client);
            } catch (RemoteException e) {
                try {
                    client.showError("Error while registering to the server.");
                } catch (RemoteException ignored) {}
            }
        });
    }

    @Override
    public void addPlayerToGame(int gameID, String username) throws RemoteException, InvalidChoiceException {
        executorService.submit(() -> {
            try {
                server.addPlayerToGame(gameID, username);
            } catch (RemoteException | InvalidChoiceException e) {
                try {
                    client.showError(e.getMessage());
                } catch (RemoteException ignored) {}
            }
        });
    }

    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException, InvalidChoiceException {
        executorService.submit(() -> {
            try {
                server.create(numberOfPlayers, numberOfCommonGoalCards, username);
            } catch (RemoteException | InvalidChoiceException e) {
                try {
                    client.showError(e.getMessage());
                } catch (RemoteException ignored) {}
            }
        });
    }

    @Override
    public void getGamesList() throws RemoteException {
        executorService.submit(() -> {
            try {
                server.getGamesList();
            } catch (RemoteException e) {
                try {
                    client.showError("Error while getting the games list.");
                } catch (RemoteException ignored) {}
            }
        });
    }

    @Override
    public void performTurn(int column, Point... points) throws RemoteException {
        executorService.submit(() -> {
            try {
                server.performTurn(column, points);
            } catch (RemoteException e) {
                try {
                    client.showError("Error while performing the turn.");
                } catch (RemoteException ignored) {}
            }
        });
    }

    @Override
    public void pong() throws RemoteException {
        executorService.submit(() -> {
            try {
                server.pong();
            } catch (RemoteException e) {
                try {
                    client.showError("Error while responding to the ping.");
                } catch (RemoteException ignored) {}
            }
        });
    }
}
