package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.model.GameList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class ClientSkeleton implements Client {
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    public enum Methods {
        UPDATE_GAMES_LIST
    }

    public ClientSkeleton(Socket socket) throws RemoteException {
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
    }

    @Override
    public void updateGamesList(String[] o, GameList.Event e) throws RemoteException {
        try {
            oos.writeObject(Methods.UPDATE_GAMES_LIST.ordinal());
            oos.writeObject(o);
        } catch (IOException e1) {
            throw new RemoteException("Cannot send model view", e1);
        }
        try {
            oos.writeObject(e);
        } catch (IOException e1) {
            throw new RemoteException("Cannot send event", e1);
        }
    }

    public void receive(Server server) throws RemoteException {
        try {
            int enum_id = (Integer) ois.readObject();

            switch (ServerStub.Methods.values()[enum_id]) {
                case JOIN -> server.join((int) ois.readObject(), (String) ois.readObject());
                case CREATE -> server.create((int) ois.readObject(), (int) ois.readObject(), (String) ois.readObject());
                case GET_GAMES_LIST -> server.getGamesList();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
