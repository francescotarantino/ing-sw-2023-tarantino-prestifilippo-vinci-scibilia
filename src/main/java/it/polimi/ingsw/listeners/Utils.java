package it.polimi.ingsw.listeners;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Utils {
    @FunctionalInterface
    public interface ConsumerWithRemoteException<T> {
        void accept(T t) throws RemoteException;
    }

    public static <Listener> void notifyListeners(ArrayList<Listener> listeners, ConsumerWithRemoteException<Listener> lambda){
        ArrayList<Listener> tmp = new ArrayList<>(listeners);

        tmp.forEach(listener -> {
            try {
                lambda.accept(listener);
            } catch (RemoteException e) {
                listeners.remove(listener);
                System.err.println("Removing listener " + listener + " because of a RemoteException.");
            }
        });
    }
}
