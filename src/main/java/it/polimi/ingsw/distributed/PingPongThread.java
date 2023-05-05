package it.polimi.ingsw.distributed;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.GameList;

import java.rmi.RemoteException;

/**
 * This thread is used by the server to check if the client is still connected.
 * It is started when the client registers.
 * The server sends a ping to the client and waits for a pong. If the pong is not received within a certain time, which
 * is defined in {@link Constants#pingpongTimeout}, the client is considered disconnected.
 */
public class PingPongThread extends Thread {
    /**
     * This boolean is used in the ping-pong mechanism to check if the client is still connected to the server.
     * It is set to false before the ping is sent. If the server receives a pong, this should be set as true using {@link #pongReceived()}.
     */
    private boolean pong;
    private final ServerImpl server;

    /**
     * Constructor of the thread.
     * @param server the server that will start the ping-pong mechanism
     */
    public PingPongThread(ServerImpl server) {
        super("PingPongThread");

        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            pong = false;
            try {
                server.client.ping();
            } catch (RemoteException e) {
                break;
            }

            try {
                sleep(Constants.pingpongTimeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!pong) {
                break;
            }
        }

        this.handlePlayerDisconnection();
    }

    /**
     * This method is called by the thread when the client is considered disconnected.
     */
    private void handlePlayerDisconnection(){
        // Checks if the client is already in a game
        if (server.model != null) {
            // Client is linked to a game
            System.out.println("Player " + server.model.getPlayer(server.playerIndex).getUsername() + " disconnected in game " + server.model.getGameID() + ".");

            if(!server.model.isStarted() || server.model.getConnectedPlayersNumber() == 1) {
                // If the game is not started or there is only one player left, the game is removed from the server

                System.out.println("The game is removed.");

                server.model.removeListener(server);
                GameList.getInstance().removeListener(server);
                GameList.getInstance().removeGame(server.model);
            } else {
                // If the game is started and there are more than one player left, the game continues

                System.out.println("The game will continue.");
                server.model.removeListener(server);

                server.controller.handlePlayerDisconnection(server.playerIndex);
            }
        } else {
            // Client is not linked to a game
            System.out.println("A client disconnected.");

            GameList.getInstance().removeListener(server);
        }
    }

    /**
     * This method should be called by the server where a pong is received from the client.
     */
    public void pongReceived() {
        this.pong = true;
    }
}
