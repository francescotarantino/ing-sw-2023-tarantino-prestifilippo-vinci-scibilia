package it.polimi.ingsw.distributed.socket.middleware;

import it.polimi.ingsw.distributed.Client;
import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.exception.InvalidChoiceException;
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
        UPDATE_GAMES_LIST,
        SHOW_ERROR
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
            oos.writeObject(e);
        } catch (IOException e1) {
            throw new RemoteException("Cannot send object", e1);
        }
    }

    @Override
    public void showError(String err, boolean exit) throws RemoteException {
        try {
            oos.writeObject(Methods.SHOW_ERROR.ordinal());
            oos.writeObject(err);
            oos.writeObject(exit);
        } catch (IOException e) {
            throw new RemoteException("Cannot send error signal", e);
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
        } catch (InvalidChoiceException e) {
            this.showError(e.getMessage(), false);
        }
    }
}
