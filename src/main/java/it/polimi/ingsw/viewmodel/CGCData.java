package it.polimi.ingsw.viewmodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record CGCData(String description, int[] tokens) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString(){
        String temp = "";
        for(int token : tokens){
            temp = temp.concat("[" + token + "]");
        }
        return description + "\t" + temp;
    }
}
