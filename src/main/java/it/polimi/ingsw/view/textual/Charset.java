package it.polimi.ingsw.view.textual;

import com.google.gson.Gson;
import it.polimi.ingsw.utils.Utils;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Set of characters used by {@link TextualGameUI} to print tables.
 */
public class Charset {
    /** Name of the charset */
    public String name = "Default";

    /** Vertical wall of the table */
    public String wall = "|";

    /** Ceiling of the table (5 characters) */
    public String fiveCeilings = "-----";

    /** Corner top left of the table */
    public String cornerTopLeft = "/";

    /** Corner top right of the table */
    public String cornerTopRight = "\\";

    /** Corner bottom left of the table */
    public String cornerBottomLeft = "\\";

    /** Corner bottom left (alternative) of the table */
    public String cornerBottomLeftAlternative = "!";

    /** Corner bottom right of the table */
    public String cornerBottomRight = "/";

    /** Corner bottom right (alternative) of the table */
    public String cornerBottomRightAlternative = "!";

    /** Edge top of the table */
    public String edgeTop = "-";

    /** Edge bottom of the table */
    public String edgeBottom = "-";

    /** Edge left of the table */
    public String edgeLeft = "|";

    /** Edge right of the table */
    public String edgeRight = "|";

    /** Intersection of the table */
    public String cross = "+";

    /**
     * This method parses all the available charsets from the json files in the resources directory.
     * @return a list of all the available charsets
     */
    public static List<Charset> getUnicodeCharsets() {
        List<Charset> charsets = new ArrayList<>();

        Utils.iterateInResourceDirectory("json/unicode_charsets", (path) -> {
            InputStream fileInputStream = Charset.class.getResourceAsStream(path);
            assert fileInputStream != null;
            charsets.add(new Gson().fromJson(new InputStreamReader(fileInputStream), Charset.class));
        });

        return charsets;
    }
}