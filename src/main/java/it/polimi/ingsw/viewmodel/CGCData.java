package it.polimi.ingsw.viewmodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is used to send common goal cards data (such as description) to the client.
 */
public record CGCData(String description, int[] tokens, String image_path) implements Serializable {
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
