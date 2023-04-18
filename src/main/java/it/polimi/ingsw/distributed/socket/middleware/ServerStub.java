package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerStub implements Server {
    private final String ip;
    private final int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    /**
     * This enumeration contains all the methods that the client can call on the server.
     */
    public enum Methods {
        JOIN, CREATE, GET_GAMES_LIST
    }

    public ServerStub(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Open a socket connection to the server and create the input and output streams.
     * @param client the client to register, in this method is not used
     */
    @Override
    public void register(Client client) throws RemoteException {
        try {
            this.socket = new Socket(ip, port);
            try {
                this.oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new RemoteException("Cannot create output stream", e);
            }
            try {
                this.ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RemoteException("Cannot create input stream", e);
            }
        } catch (IOException e) {
            throw new RemoteException("Unable to connect to the server", e);
        }
    }

    @Override
    public void join(int gameID, String username) throws RemoteException {
        try {
            oos.writeObject(Methods.JOIN.ordinal());
            oos.writeObject(gameID);
            oos.writeObject(username);
        } catch (IOException e) {
            throw new RemoteException("Cannot send event", e);
        }
    }

    @Override
    public void create(int numberOfPlayers, int numberOfCommonGoalCards, String username) throws RemoteException {
        try {
            oos.writeObject(Methods.CREATE.ordinal());
            oos.writeObject(numberOfPlayers);
            oos.writeObject(numberOfCommonGoalCards);
            oos.writeObject(username);
        } catch (IOException e) {
            throw new RemoteException("Cannot send event", e);
        }
    }

    @Override
    public void getGamesList() throws RemoteException {
        try {
            oos.writeObject(Methods.GET_GAMES_LIST.ordinal());
        } catch (IOException e) {
            throw new RemoteException("Cannot receive event", e);
        }
    }

    public void receive(Client client) throws RemoteException {
        try {
            int enum_id = (Integer) ois.readObject();

            switch (ClientSkeleton.Methods.values()[enum_id]) {
                case UPDATE_GAMES_LIST -> client.updateGamesList((String[]) ois.readObject());
                case UPDATE_PLAYERS_LIST -> client.updatePlayersList((ArrayList<String>) ois.readObject());
                case SHOW_ERROR -> client.showError((String) ois.readObject(), (boolean) ois.readObject());
                case GAME_HAS_STARTED -> client.gameHasStarted();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RemoteException("Cannot receive event", e);
        }
    }

    public void close() throws RemoteException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RemoteException("Cannot close socket", e);
        }
    }
}
