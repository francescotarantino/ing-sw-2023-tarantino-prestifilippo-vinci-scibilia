package it.polimi.ingsw.model;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.Constants.TileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Bag {
    private ArrayList<Tile> tiles;

    public Bag(){
        tiles = new ArrayList<>();
        for(TileType tile: Arrays.stream(TileType.values()).filter(x -> x != TileType.PLACEHOLDER).toArray(TileType[]::new)){
            for(int i=0;i<Constants.tilesPerType;i++){
                tiles.add(new Tile(tile, i % Constants.tileVariants));
            }
        }
    }

    public Tile popTile(int index){
        if(index>=0 && index<this.tiles.size()){
            Tile returnTile = this.tiles.get(index);
            this.tiles.remove(index);
            return returnTile;
        }
        else
            throw new IndexOutOfBoundsException("Invalid tile index");
    }

    public Tile getRandomTile(){
        if(this.tiles == null || this.tiles.size() == 0)
            throw new IllegalStateException("Bag is empty");

        Random rand = new Random();
        int randomIndex = rand.nextInt(this.tiles.size());
        return this.popTile(randomIndex);
    }
}
