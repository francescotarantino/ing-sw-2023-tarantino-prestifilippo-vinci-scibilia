package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ClientImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;

public class AppClient {
    private enum ClientType {
        RMI,
        SOCKET
    }

    public static void main(String[] args) {
        ClientType clientType = ClientType.RMI;

        if(args.length == 0) {
            System.out.println("Starting client with default options...");
        } else if(args.length == 1) {
            System.out.println("Starting client with options: " + args[0]);
            clientType = ClientType.valueOf(args[0].toUpperCase());
        } else {
            System.out.println("Too many arguments!");
            System.exit(1);
        }

        switch (clientType) {
            case RMI -> {
                System.out.println("Starting RMI client...");
                try {
                    startRMI();
                } catch (RemoteException | NotBoundException | ServerNotActiveException e) {
                    e.printStackTrace(); // TODO: handle exception
                }
            }
            case SOCKET -> {
                System.out.println("Starting socket client...");
                System.out.println("Not implemented yet!");
            }
        }
    }

    private static void startRMI() throws RemoteException, NotBoundException, ServerNotActiveException {
        Registry registry = LocateRegistry.getRegistry();
        AppServer server = (AppServer) registry.lookup("myshelfie");

        ClientImpl client = new ClientImpl(server.connect());
        client.run();
    }
}
