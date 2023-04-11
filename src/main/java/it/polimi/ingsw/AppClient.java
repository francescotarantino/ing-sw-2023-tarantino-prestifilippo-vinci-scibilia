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
        String ip = "localhost";
        int port = Constants.defaultRMIRegistryPort;

        if(args.length == 0) {
            System.out.println("Starting client with default options...");
        } else {
            clientType = ClientType.valueOf(args[0].toUpperCase());
            System.out.println("Starting a " + clientType + " client...");

            if (clientType == ClientType.SOCKET) {
                port = Constants.defaultSocketPort;
            }

            if(args.length == 3) {
                ip = args[1];
                port = Integer.parseInt(args[2]);

                System.out.println("Connecting to " + ip + ":" + port);
            } else if(args.length != 1) {
                System.out.println("Invalid arguments. Exiting...");
                System.exit(1);
            }
        }

        try {
            switch (clientType) {
                case RMI -> startRMI(ip, port);
                case SOCKET -> startSocket(ip, port);
            }
        } catch (RemoteException | NotBoundException e){
            System.err.println("Cannot connect to server. Exiting...");
            System.exit(1);
        }
    }

    private static void startRMI(String ip, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        AppServer server = (AppServer) registry.lookup(Constants.defaultRMIName);

        ClientImpl client = new ClientImpl(server.connect());
        client.run();
    }

    private static void startSocket(String ip, int port) throws RemoteException {
        ServerStub serverStub = new ServerStub(ip, port);
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
