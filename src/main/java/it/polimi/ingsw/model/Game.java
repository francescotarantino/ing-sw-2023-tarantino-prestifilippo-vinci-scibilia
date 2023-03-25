package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Random;
import it.polimi.ingsw.Constants;

public class Game {
    //attributes
    private final int gameID;
    private Bookshelf[] bookshelves;
    private int[] randomNumbers;
    private Bag bag;
    private LivingRoomBoard livingRoomBoard;

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
        bookshelves[0] = new Bookshelf(newPlayer,new PersonalGoalCard(randomNumbers[0]));
    }
    public void addShelf(Player player){
        int found = -1;
        for (int i = 0; i < this.getTotalPlayersNumber() && found==-1; i++) {
            if (this.bookshelves[i] != null){
                found = i;
                if(this.bookshelves[i].getPlayer().getUsername().equals(player.getUsername())) {
                    throw new IllegalArgumentException("Username already present in this game");
                }
            }
        }
        if(found == -1)
            throw new IllegalStateException("No free bookshelf available");
        else
            bookshelves[found] = new Bookshelf(player,new PersonalGoalCard(randomNumbers[found]));
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

    public Bookshelf[] getBookshelf() {
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