package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.ServerImpl;
import it.polimi.ingsw.model.Game;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class AppServerImpl extends UnicastRemoteObject implements AppServer {
    private static AppServerImpl instance;
    private static final ArrayList<Game> games = new ArrayList<>();

    /**
     * AppServerImpl fake constructor so it cannot be instantiated.
     */
    protected AppServerImpl() throws RemoteException {}

    /**
     * AppServerImpl singleton instance getter.
     * @return the AppServerImpl instance
     */
    public static AppServerImpl getInstance() throws RemoteException {
        if (instance == null) {
            instance = new AppServerImpl();
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

    /**
     * This method is used to start the RMI server.
     */
    private static void startRMI() throws RemoteException {
        AppServerImpl server = getInstance();

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("myshelfie", server);
    }

    /**
     * This method is used to get the list of games on the server to be displayed on the client.
     * @return an array of String describing the games on the server
     */
    public String[] getGamesString() throws RemoteException {
        if(games.size() != 0)
            return games
                    .stream()
                    .map(Game::toString)
                    .toArray(String[]::new);
        else
            return new String[]{"Nessuna partita"};
    }

    /**
     * This method is used to get a game from the list of games on the server.
     * @param gameID the ID of the game to get
     * @return the Game class of the game with the specified ID, null if the game doesn't exist
     */
    public static Game getGame(int gameID) {
        return games
                .stream()
                .filter(g -> g.getGameID() == gameID)
                .findFirst()
                .orElse(null);
    }

    /**
     * This method is used to insert a new game in the list of games on the server.
     * @param g the reference to the new Game class
     */
    public static void insertGame(Game g) {
        games.add(g);
    }

    /**
     * This method is called by the client to connect to the ServerImpl.
     * @return the ServerImpl instance that will be used by the client
     */
    @Override
    public Server connect() throws RemoteException {
        return new ServerImpl();
    }
}
