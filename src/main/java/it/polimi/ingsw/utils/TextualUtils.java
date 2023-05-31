package it.polimi.ingsw.utils;

import it.polimi.ingsw.model.Point;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.utils.Constants.livingRoomBoardY;

/**
 * This class contains some utility methods useful for the textual user interface.
 */
public abstract class TextualUtils {
    /**
     * This method gets an int from the scanner.
     * If the input is not an int, an error message is shown and the user is asked to try again.
     * @param in the scanner
     * @return the input integer
     */
    public static int nextInt(Scanner in){
        while (true) {
            try {
                return in.nextInt();
            } catch (InputMismatchException ex) {
                System.out.print("Input is not a number. Please retry: ");
                in.nextLine();
            }
        }
    }

    /**
     * This method gets an int from the scanner like {@link #nextInt(Scanner)}.
     * If the input is not in the range [lowerBound, higherBound], an error message is shown and the user can try again.
     * @param in the scanner
     * @param lowerBound the lower bound
     * @param upperBound the higher bound
     * @param errorMessage the error message to show
     * @return the input integer
     */
    public static int nextInt(Scanner in, int lowerBound, int upperBound, String errorMessage) {
        while (true) {
            int input = nextInt(in);
            if(input < lowerBound || input > upperBound) {
                System.out.println(errorMessage);
            } else {
                return input;
            }
        }
    }

    /**
     * This method gets an int from the scanner like {@link #nextInt(Scanner)}.
     * Instead of waiting for the input using the {@link Scanner#nextInt()} function,
     * the method checks every 100ms if there is an input available.
     * In this way, the method can be interrupted by the thread that is waiting for the input.
     * @param in the scanner
     * @return the input integer
     * @throws InterruptedException if the thread is interrupted while waiting for the input
     */
    public static int nextIntInterruptible(Scanner in) throws InterruptedException {
        while (true) {
            try {
                if(System.in.available() > 0){
                    try {
                        return in.nextInt();
                    } catch (InputMismatchException ex) {
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
    }

    /**
     * This method gets an integer from the scanner like {@link #nextIntInterruptible(Scanner)}.
     * If the input is not in the range [lowerBound, higherBound], an error message is shown and the user can try again.
     * @param in the scanner
     * @param lowerBound the lower bound
     * @param upperBound the higher bound
     * @param errorMessage the error message to show
     * @return the input integer
     * @throws InterruptedException if the thread is interrupted while waiting for the input
     */
    public static int nextIntInterruptible(Scanner in, int lowerBound, int upperBound, String errorMessage) throws InterruptedException {
        while (true) {
            int input = nextIntInterruptible(in);
            if(input < lowerBound || input > upperBound) {
                System.out.println(errorMessage);
            } else {
                return input;
            }
        }
    }

    /**
     * This method checks whether a string from the scanner is "Y" or "N" (case-insensitive).
     * @param in the scanner
     * @param errorMessage the error message to show if the input is not "Y" or "N"
     * @return true if the input is "Y" or "y", false if the input is "N" or "n"
     */
    public static boolean isN(Scanner in, String errorMessage) {
        while (true) {
            String line = in.next();
            if(line.equals("N") || line.equals("n")) {
                return true;
            }
            if(line.equals("Y") || line.equals("y")) {
                return false;
            }
            System.out.println(errorMessage);
        }
    }

    /**
     * This method prints a list of points, formatted like [x,y], [x,y], [x,y] and [x,y]
     * @param points the list of points to print
     */
    public static void printPoints(List<Point> points) {
        for (int i = 0; i < points.size(); i++) {
            System.out.print(" " + new Point(livingRoomBoardY - points.get(i).y(), points.get(i).x() + 1));
            if (i < points.size() - 2) {
                System.out.print(",");
            }
            else if (i == points.size() - 2) {
                System.out.print(" and");
            }
        }
    }

}