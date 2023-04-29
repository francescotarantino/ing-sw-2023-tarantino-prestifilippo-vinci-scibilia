package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.StartUIListener;
import it.polimi.ingsw.viewmodel.GameDetailsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.listeners.Listener.notifyListeners;

public class StartUI implements Runnable {
    /**
     * List of all Listeners which listen to this class
     */
    private final List<StartUIListener> lst = new ArrayList<>();

    private String username;
    private int numberOfPlayers;
    private int numberOfCommonGoalCards;
    private int gameID;
    private List<String> playersNameList;

    @Override
    public void run() {
        askUsername();

        notifyListeners(lst, StartUIListener::refreshStartUI);

        showMenu();
    }

    /**
     * Asks the user to insert his username.
     */
    private void askUsername() {
        Scanner s = new Scanner(System.in);

        System.out.print("Insert your username: ");
        this.username = s.next();
    }

    /**
     * Shows the start menu and asks the user to select an option.
     * If the user selects an invalid option, the menu is shown again.
     */
    private void showMenu(){
        Scanner s = new Scanner(System.in);
        System.out.println("""
                    Select an option:
                     1. Create a new game
                     2. Join an existing game
                     3. Exit""");

        int choice = TextualUtils.nextInt(s);

        switch (choice) {
            case 1 -> createGame();
            case 2 -> joinGame();
            case 3 -> notifyListeners(lst, StartUIListener::exit);
            default -> {
                System.out.println("Invalid choice.");
                showMenu();
            }
        }
    }

    /**
     * Asks the user to insert the number of players and the number of common goal cards, then sends a CREATE event.
     */
    private void createGame(){
        Scanner s = new Scanner(System.in);
        do {
            System.out.print("How many players? ");
            numberOfPlayers = TextualUtils.nextInt(s);
            if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
                System.out.println("Number of players should be between " + Constants.playersLowerBound
                        + " and " + Constants.playersUpperBound + ".");
        } while(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound);

        do {
            System.out.print("How many common goal cards? ");
            numberOfCommonGoalCards = TextualUtils.nextInt(s);
            if(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards)
                System.out.println("Number of common goal cards should be between " + Constants.minCommonGoalCards
                        + " and " + Constants.maxCommonGoalCards + ".");
        } while(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards);

        try {
            notifyListeners(lst, startUIListener -> startUIListener.createGame(numberOfPlayers, numberOfCommonGoalCards, this.username));
        } catch (IllegalArgumentException e) {
            System.out.println("Error while creating the game.");
            System.err.println(e.getMessage());

            showMenu();
        }
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
    }

    /**
     * Shows the list of games on the server only if the user has inserted a username.
     * @param o array of strings representing the list of games on the server
     */
    public void showGamesList(List<GameDetailsView> o){
        if(this.username != null) {
            if (o.size() != 0) {
                System.out.println("List of games on the server:");

                for (GameDetailsView gameDetails : o) {
                    StringBuilder string = new StringBuilder();

                    string.append(gameDetails.gameID()).append("\t");
                    for(String playerName : gameDetails.playerUsernames()) {
                        string.append(playerName).append("\t");
                    }
                    if(gameDetails.isStarted()){
                        string.append("(STARTED)");
                    } else if(gameDetails.isFull()){
                        string.append("(FULL)");
                    }

                    System.out.println(string);
                }
            } else {
                System.out.println("There are no games on the server.");
            }
        }
    }

    /**
     * Shows an error message and the menu.
     * @param err error message to show
     */
    public void showError(String err) {
        System.err.println(err);

        showMenu();
    }

    /**
     * Shows the list of connected players.
     * @param o a list of players usernames
     */
    public void showPlayersList(List<String> o) {
        if (this.playersNameList == null){
            System.out.println("Game created successfully");
            System.out.println("List of connected players:");
            for(String s : o){
                System.out.println(s);
            }
        } else {
            o.stream().filter(s -> !this.playersNameList.contains(s)).forEach(System.out::println);
        }

        this.playersNameList = o;
    }

    public synchronized void addListener(StartUIListener o){
        if(!lst.contains(o)){
            lst.add(o);
        }
    }

    public synchronized void removeListener(StartUIListener o){
        lst.remove(o);
    }
}
