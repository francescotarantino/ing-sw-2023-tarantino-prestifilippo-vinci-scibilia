package it.polimi.ingsw.view.textual;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.util.Observable;

import java.util.Scanner;

public class StartUI extends Observable<StartUI.Event> implements Runnable {
    public enum Event {
        CREATE,
        JOIN,
        REFRESH
    }
    private String username;
    private int numberOfPlayers;
    private int numberOfCommonGoalCards;
    private int gameID;

    public String getUsername() {
        return username;
    }
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
    public int getNumberOfCommonGoalCards() {
        return numberOfCommonGoalCards;
    }
    public int getGameID() {
        return gameID;
    }

    @Override
    public void run() {
        askUsername();

        setChangedAndNotify(Event.REFRESH);

        showMenu();
    }

    private void askUsername() {
        Scanner s = new Scanner(System.in);

        System.out.print("Inserisci il tuo username: ");
        this.username = s.next();
    }

    private void showMenu(){
        Scanner s = new Scanner(System.in);
        System.out.println("""
                    Scegli l'azione da compiere:
                     1. Creare una nuova partita
                     2. Entrare in una partita
                     3. Esci""");

        int choice = s.nextInt();

        switch (choice) {
            case 1 -> createGame();
            case 2 -> joinGame();
            case 3 -> System.exit(0);
            default -> System.out.println("Invalid choice");
        }
    }

    private void createGame(){
        Scanner s = new Scanner(System.in);
        do {
            System.out.print("Quanti giocatori? ");
            numberOfPlayers = s.nextInt();
            if(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound)
                System.out.println("Il numero di giocatori deve essere compreso tra " + Constants.playersLowerBound
                        + " e " + Constants.playersUpperBound + ".");
        } while(numberOfPlayers < Constants.playersLowerBound || numberOfPlayers > Constants.playersUpperBound);

        do {
            System.out.print("Quante carte obiettivo comuni? ");
            numberOfCommonGoalCards = s.nextInt();
            if(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards)
                System.out.println("Il numero di carte obiettivo comuni deve essere compreso tra " + Constants.minCommonGoalCards
                        + " e " + Constants.maxCommonGoalCards + ".");
        } while(numberOfCommonGoalCards < Constants.minCommonGoalCards || numberOfCommonGoalCards > Constants.maxCommonGoalCards);

        try {
            setChangedAndNotify(Event.CREATE);
        } catch (IllegalArgumentException e) {
            System.out.println("Errore nella creazione della partita.");
            System.err.println(e.getMessage());

            showMenu();
        }
    }

    private void joinGame(){
        Scanner s = new Scanner(System.in);
        do {
            System.out.print("Quale partita? ");
            gameID = s.nextInt();
            if(gameID <= Constants.IDLowerBound)
                System.out.println("L'ID partita è un numero maggiore di zero!");
        } while(gameID <= Constants.IDLowerBound);

        try {
            setChangedAndNotify(Event.JOIN);
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage(), false);
        }
    }

    public void showGamesList(String[] o){
        if(o != null && this.username != null){
            System.out.println("Lista delle partite disponibili:");
            for (String game : o) {
                System.out.println(game);
            }
        }
    }

    public void showError(String err, boolean exit) {
        System.err.println(err);

        if(exit) {
            System.exit(0);
        } else {
            showMenu();
        }
    }
}
