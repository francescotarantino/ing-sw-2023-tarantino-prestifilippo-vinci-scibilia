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
     * If the input is not in the range [lowerBound, higherBound], an error message is shown and user can try again.
     * @param input the scanner
     * @param lowerBound the lower bound
     * @param higherBound the higher bound
     * @param errorMessage the error message to show
     * @return the input integer
     */
    public static int nextInt(Scanner input, int lowerBound, int higherBound, String errorMessage){
        int temp;
        do {
            temp = nextInt(input);
            if(temp < lowerBound || temp > higherBound)
                System.out.println(errorMessage);
        } while(temp < lowerBound || temp > higherBound);
        return temp;
    }

    /**
     * This method gets an integer from the scanner like {@link #nextInt(Scanner)}.
     * Instead of waiting for the input using the {@link Scanner#nextInt()} function,
     * the method checks every 100ms if there is an input available.
     * In this way, the method can be interrupted by the thread that is waiting for the input.
     * @param in the scanner
     * @return the input integer
     * @throws InterruptedException if the thread is interrupted while waiting for the input
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

    /**
     * This method gets an integer from the scanner like {@link #nextIntInterruptible(Scanner)}.
     * If the input is not in the range [lowerBound, higherBound], an error message is shown and user can try again.
     * @param input the scanner
     * @param lowerBound the lower bound
     * @param higherBound the higher bound
     * @param errorMessage the error message to show
     * @return the input integer
     * @throws InterruptedException if the thread is interrupted while waiting for the input
     */
    public static int nextIntInterruptible(Scanner input, int lowerBound, int higherBound, String errorMessage) throws InterruptedException {
        int temp;
        do {
            temp = nextIntInterruptible(input);
            if(temp < lowerBound || temp > higherBound)
                System.out.println(errorMessage);
        } while(temp < lowerBound || temp > higherBound);
        return temp;
    }
}
