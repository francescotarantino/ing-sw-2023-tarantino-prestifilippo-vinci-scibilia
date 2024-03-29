package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.utils.Constants;
import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.utils.TextualUtils;
import it.polimi.ingsw.view.StartUI;
import it.polimi.ingsw.viewmodel.GameDetailsView;
import it.polimi.ingsw.viewmodel.PlayerInfo;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Main class for the textual version of the TextualUI.
 */
public class TextualStartUI extends StartUI {
    /**
     * The username inserted by the user.
     */
    private String username;
    /**
     * The number of players inserted by the user when creating a new game.
     */
    private int numberOfPlayers;
    /**
     * The number of common goal cards inserted by the user when creating a new game.
     */
    private int numberOfCommonGoalCards;
    /**
     * The game ID inserted by the user when joining an existing game.
     */
    private int gameID = -1;
    /**
     * If true, the user is waiting for other players to join the game.
     */
    private boolean waitingForPlayers = false;
    /**
     * The list of players in the game the user is waiting for.
     */
    private List<String> playersNameList;

    @Override
    public void run() {
        AnsiConsole.systemInstall();

        askUsername();

        System.out.print(ansi().eraseScreen(Ansi.Erase.BACKWARD).cursor(1, 1).reset());

        notifyListeners(lst, StartUIListener::refreshStartUI);

        Scanner s = new Scanner(System.in);
        int choice = TextualUtils.nextInt(s);
        switch (choice) {
            case 1 -> createGame();
            case 2 -> joinGame();
            default -> {
                System.out.println("Invalid choice.");
                notifyListeners(lst, StartUIListener::exit);
            }
        }
    }

    /**
     * Asks the user to insert his username.
     */
    private void askUsername() {
        Scanner s = new Scanner(System.in);

        System.out.print(ansi().bold().fg(Ansi.Color.GREEN).a("Insert your username: ").reset());
        this.username = s.next();
    }

    /**
     * Shows the start menu and asks the user to select an option.
     * If the user selects an invalid option, the menu is shown again.
     */
    private void showMenu(){
        System.out.println(ansi().fg(Ansi.Color.BLUE).a("Select an option:").reset());
        System.out.println(" 1. Create a new game");
        System.out.println(" 2. Join an existing game");
    }

    /**
     * Asks the user to insert the number of players and the number of common goal cards, then sends a CREATE event.
     */
    private void createGame(){
        Scanner s = new Scanner(System.in);
        do {
            System.out.print("How many players? (" + Constants.playersLowerBound + " to " + Constants.playersUpperBound
                + ") ");
            numberOfPlayers = TextualUtils.nextInt(s);
            if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
                System.out.println("Number of players should be between " + Constants.playersLowerBound
                        + " and " + Constants.playersUpperBound + ".");
        } while(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound);

        do {
            System.out.print("How many common goal cards? (" + Constants.minCommonGoalCards +
                    " for a beginner game, " + Constants.maxCommonGoalCards + " for a normal game) ");
            numberOfCommonGoalCards = TextualUtils.nextInt(s);
            if(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards)
                System.out.println("Number of common goal cards should be between " + Constants.minCommonGoalCards
                        + " and " + Constants.maxCommonGoalCards + ".");
        } while(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards);

        notifyListeners(lst, startUIListener -> startUIListener.createGame(numberOfPlayers, numberOfCommonGoalCards, this.username));
        waitingForPlayers = true;
    }

    /**
     * Asks the user to insert the ID of the game to join, then sends a JOIN event.
     */
    private void joinGame(){
        Scanner s = new Scanner(System.in);
        do {
            System.out.print("Which game do you want to join? ");
            gameID = TextualUtils.nextInt(s);
            if(gameID <= Constants.IDLowerBound)
                System.out.println("GameID is a positive number!");
        } while(gameID <= Constants.IDLowerBound);

        try {
            notifyListeners(lst, startUIListener -> startUIListener.joinGame(gameID, this.username));
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
        waitingForPlayers = true;
    }

    /**
     * Shows the list of games on the server only if the user has inserted a username.
     * @param o array of strings representing the list of games on the server
     */
    @Override
    public void showGamesList(List<GameDetailsView> o){
        if(this.username != null && !waitingForPlayers) {
            System.out.print(ansi().eraseScreen(Ansi.Erase.BACKWARD).cursor(1, 1).reset());

            System.out.println(ansi().fg(Ansi.Color.YELLOW).a(
                            """
                                     ____    ____ ____  ____  _______ ____  ____ _________ _____    _________ _____ _________\s
                                    |_   \\  /   _|_  _||_  _|/  ___  |_   ||   _|_   ___  |_   _|  |_   ___  |_   _|_   ___  |
                                      |   \\/   |   \\ \\  / / |  (__ \\_| | |__| |   | |_  \\_| | |      | |_  \\_| | |   | |_  \\_|
                                      | |\\  /| |    \\ \\/ /   '.___\\-.  |  __  |   |  _|  _  | |   _  |  _|     | |   |  _|  _\s
                                     _| |_\\/_| |_   _|  |_  |\\\\____) |_| |  | |_ _| |___/ |_| |__/ |_| |_     _| |_ _| |___/ |
                                    |_____||_____| |______| |_______.'____||____|_________|________|_____|   |_____|_________| v""" + Constants.version + "\n")
                    .reset());

            if (o.size() != 0) {
                System.out.println(ansi().fg(Ansi.Color.BLUE).a("List of games on the server:").reset());
                System.out.println(ansi().fg(Ansi.Color.BLUE).a("ID:\tPlayers:").reset());
                for (GameDetailsView gameDetails : o) {
                    StringBuilder string = new StringBuilder();

                    string.append(" ").append(gameDetails.gameID()).append("\t");
                    for(PlayerInfo playerInfo : gameDetails.playersInfo()) {
                        if(playerInfo.isConnected())
                            string.append(playerInfo.username()).append("\t");
                        else
                            string.append(ansi().fgBrightBlack().a(playerInfo.username()).reset()).append("\t");
                    }
                    if(gameDetails.isStarted()){
                        string.append(ansi().fg(Ansi.Color.YELLOW).a("(STARTED)").reset());
                    } else if(gameDetails.isFull()){
                        string.append(ansi().fg(Ansi.Color.RED).a("(FULL)").reset());
                    }

                    System.out.println(string);
                }
            } else {
                System.out.println(ansi().fg(Ansi.Color.BLUE).a("There are no games on the server.").reset());
            }
            showMenu();
        }
    }

    /**
     * Shows an error message and the menu.
     * @param err error message to show
     */
    @Override
    public void showError(String err) {
        System.out.println(ansi().bold().fg(Ansi.Color.RED).a(err).reset());

        notifyListeners(lst, StartUIListener::exit);
    }

    /**
     * Shows the list of connected players.
     * @param o a list of player usernames
     */
    @Override
    public void showPlayersList(List<String> o) {
        if (this.playersNameList == null){
            System.out.print(ansi().eraseScreen(Ansi.Erase.BACKWARD).cursor(1, 1).reset());

            if(gameID == -1){
                System.out.println(ansi().fg(Ansi.Color.GREEN).a("Game created successfully.").reset());
            } else {
                System.out.println(ansi().fg(Ansi.Color.GREEN).a("Game #" + gameID).reset());
            }
            System.out.println(ansi().fg(Ansi.Color.BLUE).a("List of connected players:").reset());
            for(String s : o){
                System.out.println(" " + s);
            }
        } else {
            o.stream().filter(s -> !this.playersNameList.contains(s)).forEach(x -> System.out.println(" " + x));
        }

        this.playersNameList = o;
    }

    /**
     * This method erases the screen.
     */
    @Override
    public void close(){
        System.out.print(ansi().eraseScreen(Ansi.Erase.BACKWARD).cursor(1, 1).reset());
    }
}
