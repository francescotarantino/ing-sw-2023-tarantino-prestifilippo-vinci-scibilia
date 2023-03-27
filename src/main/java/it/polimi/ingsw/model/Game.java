package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Random;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.goal_cards.PersonalGoalCard;

public class Game {
    //attributes
    private final int gameID;
    private final Bookshelf[] bookshelves;
    private final int[] randomNumbers;
    private final Bag bag;
    private final LivingRoomBoard livingRoomBoard;

    public Game(int numberOfPlayers,Player newPlayer,int ID){
        if(numberOfPlayers<Constants.playersLowerBound || numberOfPlayers>Constants.playersUpperBound)
            throw new IllegalArgumentException("Illegal number of players. A game should have " + Constants.playersLowerBound + " to " + Constants.playersUpperBound + " players.");
        if(ID<=Constants.IDLowerBound)
            throw new IllegalArgumentException("Game ID must be greater than " + Constants.IDLowerBound);
        //game construction
        //assigning ID, generating a board and a bag
        this.gameID = ID;
        this.livingRoomBoard = new LivingRoomBoard(numberOfPlayers);
        this.bag = new Bag();
        //generating an adequate amount of random numbers, used as indexes for class PersonalGoalCards to generate cards with no repetition
        randomNumbers = extractRandomCardIDs(numberOfPlayers);
        //generating an adequate amount of bookshelves and creating the first bookshelf for the first player
        this.bookshelves = new Bookshelf[numberOfPlayers];
        this.addBookshelf(newPlayer);
    }

    public void addBookshelf(Player player){
        int i;
        for (i = 0; i < this.getTotalPlayersNumber(); i++) {
            if (this.bookshelves[i] != null){
                if(this.bookshelves[i].getPlayer().getUsername().equals(player.getUsername())) {
                    throw new IllegalArgumentException("Username already present in this game");
                }
            } else
                break;
        }
        if(i == this.getTotalPlayersNumber())
            throw new IllegalStateException("No free bookshelf available");
        else
            bookshelves[i] = new Bookshelf(player,new PersonalGoalCard(randomNumbers[i]));
    }

    //methods
    public int getGameID(){
        return this.gameID;
    }
    public int getTotalPlayersNumber(){
        return this.bookshelves.length;
    }
    public LivingRoomBoard getLivingRoomBoard(){
        return this.livingRoomBoard;
    }
    public Bag getBag(){
        return this.bag;
    }

    public Bookshelf[] getBookshelves() {
        return this.bookshelves;
    }

    public int[] extractRandomCardIDs(int amount){
        //extracts indexes for random personal goal cards with no duplicates
        ArrayList<ArrayList<String>> cards = Constants.getPersonalGoalCards();
        int bound = cards.size();
        int[] numbers = new int[amount];
        Random rand = new Random();
        boolean flag;
        for(int i=0;i<amount;i++){
            do{
                flag = true;
                numbers[i] = rand.nextInt(bound);
                for(int j=0;j<i;j++){
                    if (numbers[j] == numbers[i]) {
                        flag = false;
                        break;
                    }
                }
            }while(!flag);
        }
        return numbers;
    }
}