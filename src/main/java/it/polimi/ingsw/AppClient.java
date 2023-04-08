package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ClientImpl;
import it.polimi.ingsw.distributed.socket.middleware.ServerStub;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace(); // TODO: handle exception
                }
            }
            case SOCKET -> {
                System.out.println("Starting socket client...");
                try {
                    startSocket();
                } catch (RemoteException e) {
                    e.printStackTrace(); // TODO: handle exception
                }
            }
        }
    }

    private static void startRMI() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        AppServer server = (AppServer) registry.lookup("myshelfie");

        ClientImpl client = new ClientImpl(server.connect());
        client.run();
    }

    private static void startSocket() throws RemoteException {
        ServerStub serverStub = new ServerStub("localhost", 12345); // TODO: read ip and port from args
        ClientImpl client = new ClientImpl(serverStub);
        new Thread(() -> {
            while(true) {
                try {
                    serverStub.receive(client);
                } catch (RemoteException e) {
                    System.err.println("Cannot receive from server. Stopping...");
                    try {
                        serverStub.close();
                    } catch (RemoteException ex) {
                        System.err.println("Cannot close connection with server. Halting...");
                    }
                    System.exit(1);
                }
            }
        }).start();

        client.run();
    }
}
