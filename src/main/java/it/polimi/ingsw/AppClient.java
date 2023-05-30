package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ClientImpl;
import it.polimi.ingsw.distributed.socket.middleware.ServerStub;
import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.utils.TextualUtils;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import it.polimi.ingsw.utils.Constants.UIType;
import it.polimi.ingsw.utils.Constants.ConnectionType;

public class AppClient {
    private static UIType uiType;
    private static ConnectionType connectionType;
    private static String ip;
    private static int port;

    /**
     * Starts a client application.
     * @param args client type (RMI or SOCKET), server IP and server port
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        if(args.length == 0) {
            askArgs(in);
        } else {
            parseArgs(args);
        }

        System.out.println("Select UI type:");
        for (UIType value : UIType.values()) {
            System.out.println((value.ordinal() + 1) + ". " + value);
        }
        int uiTypeInt = TextualUtils.nextInt(in, 1, UIType.values().length, "Invalid UI type. Please retry: ") - 1;
        uiType = UIType.values()[uiTypeInt];

        System.out.println("Connecting to " + ip + ":" + port + " using " + connectionType + "...");

        try {
            switch (connectionType) {
                case RMI -> startRMI();
                case SOCKET -> startSocket();
            }
        } catch (RemoteException | NotBoundException e){
            System.err.println("Cannot connect to server. Exiting...");
            System.exit(1);
        }
    }

    private static void askArgs(Scanner in) {
        System.out.println("Select connection type:");
        for (ConnectionType value : ConnectionType.values()) {
            System.out.println((value.ordinal() + 1) + ". " + value);
        }

        int clientTypeInt = TextualUtils.nextInt(in, 1, ConnectionType.values().length, "Invalid client type. Please retry: ") - 1;
        connectionType = ConnectionType.values()[clientTypeInt];

        // Consumes the \n left by nextInt
        in.nextLine();

        System.out.print("Enter server IP (blank for localhost): ");
        ip = in.nextLine();
        if (ip.isBlank()){
            ip = "localhost";
        }

        System.out.print("Enter server port (blank for default): ");
        String portString = in.nextLine();
        if (portString.isBlank()){
            switch (connectionType) {
                case RMI -> port = Constants.defaultRMIRegistryPort;
                case SOCKET -> port = Constants.defaultSocketPort;
            }
        } else {
            port = Integer.parseInt(portString);
        }
    }

    private static void parseArgs(String[] args) {
        if(args.length == 3) {
            connectionType = ConnectionType.valueOf(args[0].toUpperCase());
            ip = args[1];
            port = Integer.parseInt(args[2]);
        } else {
            System.out.println("Invalid arguments.");
            System.out.println("Usage: java -jar myshelfie-client.jar [RMI|SOCKET] [ip] [port]");
            System.exit(1);
        }
    }

    /**
     * Starts an RMI client.
     */
    private static void startRMI() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        AppServer appServer = (AppServer) registry.lookup(Constants.defaultRMIName);

        ClientImpl client = new ClientImpl(appServer.connect(), uiType);
        client.run();
    }

    /**
     * Starts a socket client.
     */
    private static void startSocket() throws RemoteException {
        ServerStub serverStub = new ServerStub(ip, port);
        ClientImpl client = new ClientImpl(serverStub, uiType);
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

    public static String getIP(){
        return ip;
    }

    public static int getPort(){
        return port;
    }

    public static ConnectionType getConnectionType(){
        return connectionType;
    }
}
