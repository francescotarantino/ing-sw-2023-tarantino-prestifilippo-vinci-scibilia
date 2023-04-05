package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.ServerImpl;
import it.polimi.ingsw.model.Game;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class AppServerImpl extends UnicastRemoteObject implements AppServer {
    private static AppServerImpl instance;
    private static ArrayList<Game> games;

    protected AppServerImpl() throws RemoteException {}

    public static AppServerImpl getInstance() throws RemoteException {
        if (instance == null) {
            instance = new AppServerImpl();
            games = new ArrayList<>();
        }

        return instance;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            System.err.println("Unable to launch RMI registry on the default port 1099.");
            throw new RuntimeException(e);
        }

        Thread rmiThread = new Thread(() -> {
            try {
                startRMI();
            } catch (RemoteException e) {
                System.err.println("Cannot start RMI.");
                e.printStackTrace();
            }
        });
        rmiThread.start();

        try {
            rmiThread.join();
        } catch (InterruptedException e) {
            System.err.println("No connection protocol available. Exiting...");
        }
    }

    private static void startRMI() throws RemoteException {
        AppServerImpl server = getInstance();

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("myshelfie", server);
    }

    public String[] getGamesString() throws RemoteException {
        if(games.size() != 0)
            return games.stream().map(Game::toString).toArray(String[]::new); // TODO: scrivere qualcosa se la partita è piena
        else
            return new String[]{"Nessuna partita"};
    }

    public static Game getGame(int gameID) {
        return games.stream().filter(g -> g.getGameID() == gameID).toArray(Game[]::new)[0];
    }

    public static void insertGame(Game g) {
        games.add(g);
    }

    @Override
    public Server connect() throws RemoteException {
        return new ServerImpl();
    }
}
