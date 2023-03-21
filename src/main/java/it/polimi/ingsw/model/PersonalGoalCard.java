package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import javax.sound.midi.SysexMessage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class PersonalGoalCard extends GoalCard {


    public PersonalGoalCard(int index) {
        //LEGGE DA FILE E STAMPA, MA NON SO DOVE STA LA LOGICA PER POTER FARE MAPPING SU UNA CLASSE LOCALE
    try {
         Gson gson = new Gson();
         Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/PersonalGoalCardsData.json"));
         Map<?,?> map;
         map = gson.fromJson(reader, Map.class);

         for (Map.Entry<?,?> entry : map.entrySet()){
             System.out.println(entry.getKey() + "=" + entry.getValue());

         }
         reader.close();

     }catch (Exception FileReadError){
         FileReadError.printStackTrace();
     }
    }

            // USA DIRETTAMENTE UNA CLASSE PER FARE MAPPING TRA FILE E DATI LOCALI, MA RITORNA SEMPRE NULL.
      /*  try {
            Gson gson = new Gson();
            PGC_Matrix MyMatrix = gson.fromJson(new FileReader("src/main/resources/PersonalGoalCardsData.json"), PGC_Matrix.class);
            //String [][][] matrix = MyMatrix.getMatrix();
            System.out.println(MyMatrix.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    // private TileType[][] matrix;

    private int[] scoringTokenStack;

   /* public TileType[][] getMatrix() {
        return matrix;
    }*/

    @Override
    public int GetId() {
        return this.ID;
    }

    @Override
    public int CheckValidity(Bookshelf B) {
        return 0; // TODO: once we have bookshelf class coded
    }
}


