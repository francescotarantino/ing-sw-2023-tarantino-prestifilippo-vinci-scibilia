package it.polimi.ingsw.view.textual;

import java.io.IOException;
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

    /**
     * This method gets an integer from the scanner like {@link #nextInt(Scanner)}.
     * Instead of waiting for the input using the {@link Scanner#nextInt()} function,
     * the method checks every 100ms if there is an input available.
     * In this way, the method can be interrupted by the thread that is waiting for the input.
     * @param in the scanner
     * @return the input integer
     */
    public static int nextIntInterruptible(Scanner in) throws InterruptedException {
        int input = -1;
        boolean valid = false;

        while(!valid){
            try {
                if(System.in.available() > 0){
                    try {
                        valid = true;
                        input = in.nextInt();
                    } catch (InputMismatchException ex) {
                        valid = false;
                        System.out.print("Input is not a number. Please retry: ");
                        in.nextLine();
                    }
                } else {
                    Thread.sleep(100);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return input;
    }
}
