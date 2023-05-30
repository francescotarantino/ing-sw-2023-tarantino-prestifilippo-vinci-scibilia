package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.ServerExecutor;
import it.polimi.ingsw.distributed.ServerImpl;
import it.polimi.ingsw.distributed.socket.middleware.ClientSkeleton;
import it.polimi.ingsw.utils.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppServerImpl extends UnicastRemoteObject implements AppServer {
    /**
     * The port on which the socket server will listen.
     */
    private static int socketPort = Constants.defaultSocketPort;
    private static AppServerImpl instance;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * AppServerImpl fake constructor, so it cannot be instantiated from outside.
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

    /**
     * Starts the server application.
     * @param args socket port
     */
    public static void main(String[] args) {
        System.out.println("Starting server...");

        if (args.length > 0) {
            try {
                socketPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid socket port. Using default port " + Constants.defaultSocketPort + "...");
            }
        }

        Thread rmiThread = new Thread(() -> {
            try {
                startRMI();
            } catch (RemoteException e) {
                System.err.println("Cannot start RMI protocol.");
                e.printStackTrace();
            }
        });
        rmiThread.start();

        Thread socketThread = new Thread(() -> {
            try {
                startSocket();
            } catch (RemoteException e) {
                System.err.println("Cannot start socket protocol.");
                e.printStackTrace();
            }
        });
        socketThread.start();

        try {
            rmiThread.join();
            socketThread.join();
        } catch (InterruptedException e) {
            System.err.println("No connection protocol available. Exiting...");
        }
    }

    /**
     * This method is used to start the RMI server.
     */
    private static void startRMI() throws RemoteException {
        System.out.println("RMI > Starting RMI server...");

        AppServerImpl server = getInstance();

        Registry registry;
        try {
            System.out.println("RMI > Creating a new RMI registry on port " + Constants.defaultRMIRegistryPort + "...");

            registry = LocateRegistry.createRegistry(Constants.defaultRMIRegistryPort);
        } catch (RemoteException e) {
            System.err.println("RMI > Cannot create RMI registry.");
            System.out.println("RMI > Trying to get already existing RMI registry...");

            registry = LocateRegistry.getRegistry();
        }

        registry.rebind(Constants.defaultRMIName, server);
    }

    /**
     * This method is used to start the socket server.
     */
    public static void startSocket() throws RemoteException {
        System.out.println("SOCKET > Starting socket server on port " + socketPort + "...");

        AppServerImpl instance = getInstance();
        try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("SOCKET > Accepting connection from " + socket.getInetAddress() + "...");
                instance.executorService.submit(() -> {
                    try {
                        ClientSkeleton clientSkeleton = new ClientSkeleton(socket);
                        Server server = new ServerExecutor(new ServerImpl());
                        server.register(clientSkeleton);
                        while (true) {
                            clientSkeleton.receive(server);
                        }
                    } catch (RemoteException e) {
                        System.err.println("SOCKET > Client " + socket.getInetAddress() + " seems to have closed the connection. Closing...");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("SOCKET > Unexpected error. Closing connection with" + socket.getInetAddress() + "...");
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.err.println("SOCKET > Cannot close socket");
                        }
                    }
                });
            }
        } catch (IOException e) {
            throw new RemoteException("SOCKET > Cannot start socket server", e);
        }
    }

    @Override
    public Server connect() throws RemoteException {
        return new ServerExecutor(new ServerImpl());
    }
}
