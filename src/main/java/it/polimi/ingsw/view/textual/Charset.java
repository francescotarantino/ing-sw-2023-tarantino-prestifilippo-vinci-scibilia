package it.polimi.ingsw.view.textual;

import com.google.gson.Gson;
import it.polimi.ingsw.Utils;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Set of characters used by {@link it.polimi.ingsw.view.textual.GameUI} to print tables.
 */
public class Charset {
    /** Name of the charset */
    public String name = "Default";

    /** Character used by the UI to display part of a table */
    public String wall = "|";

    /** Character used by the UI to display part of a table */
    public String fiveCeilings = "-----";

    /** Character used by the UI to display part of a table */
    public String cornerTopLeft = "/";

    /** Character used by the UI to display part of a table */
    public String cornerTopRight = "\\";

    /** Character used by the UI to display part of a table */
    public String cornerBottomLeft = "\\";

    /** Character used by the UI to display part of a table */
    public String cornerBottomLeftAlternative = "!";

    /** Character used by the UI to display part of a table */
    public String cornerBottomRight = "/";

    /** Character used by the UI to display part of a table */
    public String cornerBottomRightAlternative = "!";

    /** Character used by the UI to display part of a table */
    public String edgeTop = "-";

    /** Character used by the UI to display part of a table */
    public String edgeBottom = "-";

    /** Character used by the UI to display part of a table */
    public String edgeLeft = "|";

    /** Character used by the UI to display part of a table */
    public String edgeRight = "|";

    /** Character used by the UI to display part of a table */
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