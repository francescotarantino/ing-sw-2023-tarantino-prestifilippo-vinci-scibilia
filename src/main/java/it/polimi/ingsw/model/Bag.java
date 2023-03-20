package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Random;
public class Bag {
    private ArrayList<Tile> tiles;
    private int capacity;
    public Bag(){
        this.capacity = 132;
    }
    public void fillBag(ArrayList<Tile> newTiles){
        if(newTiles.size()>this.capacity)
            this.tiles = new ArrayList<Tile>(newTiles);
        else
            throw new IndexOutOfBoundsException();
    }
    public Tile popTile(int index){
        if(index>=0 && index<this.tiles.size()){
            Tile returnTile = new Tile(this.tiles.get(index));
            this.tiles.remove(index);
            return returnTile;
        }
        else
            throw new IndexOutOfBoundsException("Invalid tile index");
    }
    public Tile getRandomTile(){
        if(this.tiles == null || this.tiles.size()<=0)
            throw new IllegalStateException("Bag is empty");
        Random rand = new Random();
        int randomIndex = rand.nextInt(this.tiles.size());
        return new Tile(this.popTile(randomIndex));
    }
}
