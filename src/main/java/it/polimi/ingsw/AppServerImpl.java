package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Server;
import it.polimi.ingsw.distributed.ServerImpl;
import it.polimi.ingsw.distributed.socket.middleware.ClientSkeleton;

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
                System.err.println("Cannot start RMI protocol.");
                e.printStackTrace();
            }
        });
        rmiThread.start();

        Thread socketThread = new Thread(() -> {
            try {
                startSocket(12345);
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
        System.out.println("Starting RMI server...");

        AppServerImpl server = getInstance();

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("myshelfie", server);
    }

    /**
     * This method is used to start the socket server.
     * @param port the port on which the server will listen
     */
    public static void startSocket(int port) throws RemoteException {
        System.out.println("Starting socket server...");

        AppServerImpl instance = getInstance();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                instance.executorService.submit(() -> {
                    try {
                        ClientSkeleton clientSkeleton = new ClientSkeleton(socket);
                        Server server = new ServerImpl();
                        server.register(clientSkeleton);
                        while (true) {
                            clientSkeleton.receive(server);
                        }
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from client. Closing this connection...");
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.err.println("Cannot close socket");
                        }
                    }
                });
            }
        } catch (IOException e) {
            throw new RemoteException("Cannot start socket server", e);
        }
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
