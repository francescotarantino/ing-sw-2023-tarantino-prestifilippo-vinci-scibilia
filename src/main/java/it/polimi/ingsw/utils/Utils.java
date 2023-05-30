package it.polimi.ingsw.utils;

import it.polimi.ingsw.view.textual.Charset;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    /**
     * Extracts random numbers without duplicates from 0 to bound-1
     * @param amount the number of random numbers to be extracted
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
                    // The game is not running from jar (e.g., it's running with IntelliJ)
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