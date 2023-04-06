package it.polimi.ingsw;

import it.polimi.ingsw.distributed.ClientImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.util.Scanner;

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
        Scanner s = new Scanner(System.in);
        Registry registry = LocateRegistry.getRegistry();
        AppServer server = (AppServer) registry.lookup("myshelfie");

        System.out.println("Inserisci il tuo username:");
        String username = s.next();

        int input;
        do{
            for (String gamesDatum : server.getGamesString()) {
                System.out.println(gamesDatum);
            }

            System.out.println("""
                    Scegli l'azione da compiere:
                     1. Creare una nuova partita
                     2. Entrare in una partita
                     3. Ricaricare la lista delle partite""");
            input = s.nextInt();
        } while(input != 1 && input != 2);


        ClientImpl client = new ClientImpl();
        switch(input) {
            case 1 -> {
                int numberOfPlayers;
                do {
                    System.out.println("Quanti giocatori?");
                    numberOfPlayers = s.nextInt();
                    if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
                        System.out.println("Il numero di giocatori deve essere compreso tra " + Constants.playersLowerBound
                            + " e " + Constants.playersUpperBound + ".");
                } while(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound);

                client.create(server.connect(), numberOfPlayers, username);
            }
            case 2 -> {
                //TODO: se la partita si è riempita prima di riuscire ad entrare, ricominciare da capo
                int gameID;
                do {
                    System.out.println("Quale partita?");
                    gameID = s.nextInt();
                    if(gameID <= Constants.IDLowerBound)
                        System.out.println("L'ID partita è un numero maggiore di zero!");

                    // TODO: metodo privato per stampare di nuovo la lista partite

                } while(gameID <= Constants.IDLowerBound);

                client.join(server.connect(), gameID, username);
            }
        }

        client.run();
    }
}
