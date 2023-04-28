package it.polimi.ingsw.view.textual;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TextualUtils {
    /**
     * This method gets an integer from the scanner.
     * If the input is malformed, an error message is shown and user is asked to try again.
     * @param in the scanner
     * @return the input integer
     */
    public static int nextInt(Scanner in){
        int input = -1;
        boolean valid;

        do {
            try {
                valid = true;
                input = in.nextInt();
            } catch (InputMismatchException ex) {
                valid = false;
                System.out.print("Input is not a number. Please retry: ");
                in.nextLine();
            }
        } while(!valid);

        return input;
    }
}
