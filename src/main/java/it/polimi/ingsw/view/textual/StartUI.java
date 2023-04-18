package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.listeners.StartUIListener;

import java.util.ArrayList;
import java.util.Scanner;

import static it.polimi.ingsw.listeners.Utils.notifyListeners;

public class StartUI implements Runnable {
    /**
     * List of all Listeners which listen to this class
     */
    private final ArrayList<StartUIListener> lst = new ArrayList<>();

    private String username;
    private int numberOfPlayers;
    private int numberOfCommonGoalCards;
    private int gameID;
    private ArrayList<String> playersNameList;

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

        int choice = s.nextInt();

        switch (choice) {
            case 1 -> createGame();
            case 2 -> joinGame();
            case 3 -> System.exit(0);
            default -> {
                System.err.println("Invalid choice.");
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
            numberOfPlayers = s.nextInt();
            if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
                System.err.println("Number of players should be between " + Constants.playersLowerBound
                        + " and " + Constants.playersUpperBound + ".");
        } while(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound);

        do {
            System.out.print("How many common goal cards? ");
            numberOfCommonGoalCards = s.nextInt();
            if(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards)
                System.err.println("Number of common goal cards should be between " + Constants.minCommonGoalCards
                        + " and " + Constants.maxCommonGoalCards + ".");
        } while(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards);

        try {
            notifyListeners(lst, startUIListener -> startUIListener.createGame(numberOfPlayers, numberOfCommonGoalCards, this.username));
            System.out.println("Game created successfully");
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
            gameID = s.nextInt();
            if(gameID <= Constants.IDLowerBound)
                System.out.println("GameID is a positive number!");
        } while(gameID <= Constants.IDLowerBound);

        try {
            notifyListeners(lst, startUIListener -> startUIListener.joinGame(gameID, this.username));
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage(), false);
        }
    }

    /**
     * Shows the list of games on the server only if the user has inserted a username.
     * @param o array of strings representing the list of games on the server
     */
    public void showGamesList(String[] o){
        if(this.username != null){
            if(o == null){
                System.out.println("There are no games on the server.");
            } else {
                System.out.println("List of games on the server:");
                for (String game : o) {
                    System.out.println(game);
                }
            }
        }
    }

    /**
     * Shows an error message and exits the program if exit is true.
     * @param err error message to show
     * @param exit if true, client is stopped
     */
    public void showError(String err, boolean exit) {
        System.err.println(err);

        if(exit) {
            System.exit(0);
        } else {
            showMenu();
        }
    }

    /**
     * Shows the list of connected players.
     * @param o a list of players usernames
     */
    public void showPlayersList(ArrayList<String> o) {
        if (this.playersNameList == null){
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
