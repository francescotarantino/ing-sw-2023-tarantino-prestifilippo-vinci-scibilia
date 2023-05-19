package it.polimi.ingsw;

import it.polimi.ingsw.model.Point;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.view.textual.Charset;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    /**
     * Checks if the matrix has the correct size of the bookshelf (Constants.bookshelfX x Constants.bookshelfY)
     * @param matrix matrix to check
     * @param <T> type of the matrix
     */
    public static <T> void checkMatrixSize(T[][] matrix) {
        if (matrix.length != Constants.bookshelfX)
            throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
        for (T[] tiles : matrix)
            if (tiles.length != Constants.bookshelfY)
                throw new IllegalArgumentException("Matrix must be " + Constants.bookshelfX + "x" + Constants.bookshelfY);
    }

    /**
     * @return is the size of the group found starting from the tile in position (x,y) in the matrix
     * The method is recursive, and it builds the biggest possible block by checking the adjacent tiles in all directions
     */
    public static int findGroup(Point point, Tile[][] matrix, boolean[][] done) {
        if (done[point.getX()][point.getY()]) {
            return 0;
        }
        done[point.getX()][point.getY()] = true;
        if(matrix[point.getX()][point.getY()] == null) {
            return 0;
        }
        int currentSize = 1;
        for (Constants.Direction direction : Constants.Direction.values()) {
            Tile tile = checkAdjacentTile(point, matrix, direction);
            if (tile != null && tile.sameType(matrix[point.getX()][point.getY()])) {
                switch (direction) {
                    case UP -> currentSize += findGroup(new Point(point.getX(), point.getY() + 1), matrix, done);
                    case RIGHT -> currentSize += findGroup(new Point(point.getX() + 1, point.getY()), matrix, done);
                    case DOWN -> currentSize += findGroup(new Point(point.getX(), point.getY() - 1), matrix, done);
                    case LEFT -> currentSize += findGroup(new Point(point.getX() - 1, point.getY()), matrix, done);
                }
            }
        }
        return currentSize;
    }

    /**
     * @param direction is the direction in which the tile in the matrix is checked
     * @return the tile in the matrix in the direction specified
     */
    public static Tile checkAdjacentTile(Point point, Tile[][] matrix, Constants.Direction direction) {
        int xSize = matrix.length;
        int ySize = matrix[0].length;
        if (point.getX() < 0 || point.getX() >= xSize || point.getY() < 0 || point.getY() >= ySize) {
            return null;
        }
        switch (direction) {
            case UP -> {
                if(point.getY() == ySize - 1) return null;
                return matrix[point.getX()][point.getY() + 1];
            }
            case RIGHT -> {
                if(point.getX() == xSize - 1) return null;
                return matrix[point.getX() + 1][point.getY()];
            }
            case DOWN -> {
                if(point.getY() == 0) return null;
                return matrix[point.getX()][point.getY() - 1];
            }
            case LEFT -> {
                if(point.getX() == 0) return null;
                return matrix[point.getX() - 1][point.getY()];
            }
            default -> throw new IllegalStateException("Invalid direction: " + direction);
        }
    }

    /**
     * This method checks if the tiles at the given positions can be taken from the board matrix.
     * @param boardMatrix the matrix of the board
     * @param points the positions of the tiles to check
     * @return true if the tiles can be taken, false otherwise
     */
    public static boolean checkIfTilesCanBeTaken(Tile[][] boardMatrix, Point... points) {
        if(points.length < Constants.minPick || points.length > Constants.maxPick){
            return false;
        }
        // Check if the tiles are not null or placeholders
        for (Point point : points) {
            if (boardMatrix[point.getX()][point.getY()] == null || boardMatrix[point.getX()][point.getY()].isPlaceholder()) return false;
        }

        // Check if tiles are adjacent
        if(points.length != 1){
            if(Arrays.stream(points).mapToInt(Point::getX).allMatch(x -> x == points[0].getX()) && !checkContiguity(Point::getY, points)) return false;

            if(Arrays.stream(points).mapToInt(Point::getY).allMatch(y -> y == points[0].getY()) && !checkContiguity(Point::getX, points)) return false;

            if(!Arrays.stream(points).mapToInt(Point::getX).allMatch(x -> x == points[0].getX()) &&
               !Arrays.stream(points).mapToInt(Point::getY).allMatch(y -> y == points[0].getY())) return false;
        }

        // Check if tiles have at least one free side
        for (Point point : points) {
            boolean flag = false;
            // Up
            if (point.getX() != 0) {
                if (boardMatrix[point.getX() - 1][point.getY()] == null ||
                        boardMatrix[point.getX() - 1][point.getY()].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Down
            if (point.getX() != Constants.livingRoomBoardX - 1) {
                if (boardMatrix[point.getX() + 1][point.getY()] == null ||
                        boardMatrix[point.getX() + 1][point.getY()].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Left
            if (point.getY() != 0) {
                if (boardMatrix[point.getX()][point.getY() - 1] == null ||
                        boardMatrix[point.getX()][point.getY() - 1].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            // Right
            if (point.getY() != Constants.livingRoomBoardY - 1) {
                if (boardMatrix[point.getX()][point.getY() + 1] == null ||
                        boardMatrix[point.getX()][point.getY() + 1].getType().equals(Constants.TileType.PLACEHOLDER))
                    flag = true;
            } else flag = true;

            if (!flag) return false;
        }

        return true;
    }

    /**
     * This method checks if the tiles at the given are contiguous.
     * @param lambda the lambda function used to get the x or y coordinate of the tile
     * @param points the positions of the tiles to check
     * @return true if the tiles are contiguous, false otherwise
     */
    private static boolean checkContiguity(java.util.function.ToIntFunction<Point> lambda, Point... points) {
        int[] tmp = Arrays.stream(points)
                .mapToInt(lambda)
                .sorted()
                .toArray();
        for (int i = 1; i < tmp.length; i++) {
            if (tmp[i] != (tmp[i - 1] + 1))
                return false;
        }
        return true;
    }

    /**
     * This method checks if the column of the bookshelf where the user wants to place the tiles has enough space.
     * @param bookshelfMatrix the matrix of the bookshelf
     * @param column the column of the bookshelf
     * @param tilesNum the number of tiles to place
     * @return true if the column has enough space, false otherwise
     */
    public static boolean checkIfColumnHasEnoughSpace(Tile[][] bookshelfMatrix, int column, int tilesNum) {
        int counter = 0;
        for (int i = Constants.bookshelfY - 1; i >= 0; i--) {
            if (bookshelfMatrix[column][i] == null) {
                counter++;
                if (counter == tilesNum)
                    return true;
            } else return false;
        }
        return false;
    }

    /**
     * Extracts random numbers without duplicates from 0 to bound-1
     * @param amount the amount of random numbers to be extracted
     * @param bound the upper bound (exclusive) of the random numbers to be extracted
     */
    public static int[] extractRandomIDsWithoutDuplicates(int amount, int bound){
        if(amount > bound)
            throw new IllegalArgumentException("Amount cannot be greater than bound");

        int[] numbers = new int[amount];
        Random rand = new Random();

        boolean flag;
        for(int i = 0; i < amount; i++){
            do {
                flag = true;
                numbers[i] = rand.nextInt(bound);
                for(int j=0;j<i;j++){
                    if (numbers[j] == numbers[i]) {
                        flag = false;
                        break;
                    }
                }
            } while(!flag);
        }

        return numbers;
    }

    /**
     * This method can be used to iterate in a specific resource directory.
     * In particular, given a resource path, it will iterate in all the files contained in that directory recursively.
     * For each file, the consumer will be called with the path of the file as parameter.
     * The consumer can access the file using the {@link Class#getResourceAsStream(String)} method.
     * <br>
     * The code is partly inspired by <a href="https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file">this StackOverflow post</a>.
     * @param resourcePath the path of the resource directory
     * @param consumer the consumer to be called for each file
     * @see Charset#getUnicodeCharsets() for an example of usage
     */
    public static void iterateInResourceDirectory(String resourcePath, Consumer<String> consumer) {
        try {
            // Get the code source
            CodeSource src = Utils.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();

                if(jar.toString().endsWith(".jar")) {
                    // The game is running from a jar file

                    // Open the jar file
                    ZipInputStream zip = new ZipInputStream(jar.openStream());

                    // For each entry in the jar file
                    ZipEntry e = zip.getNextEntry();
                    while (e != null) {
                        String name = e.getName();
                        // If the file name starts with the resource path, and it's not a directory
                        if (name.startsWith(resourcePath) && !name.endsWith("/")) {
                            // Call the consumer
                            consumer.accept("/" + name);
                        }

                        e = zip.getNextEntry();
                    }

                    zip.close();
                } else {
                    // The game is not running from jar (e.g. it's running with IntelliJ)
                    Path path = Paths.get(Objects.requireNonNull(Utils.class.getResource("/" + resourcePath)).toURI());
                    try(Stream<Path> stream = Files.walk(path)){
                        stream
                                .filter(Files::isRegularFile)
                                .forEach(file -> consumer.accept("/" + resourcePath + "/" + path.relativize(file)));
                    }
                }
            } else {
                // Something went wrong
                throw new RuntimeException("Unable to read from filesystem");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}