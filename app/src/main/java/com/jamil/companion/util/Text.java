package com.jamil.companion.util;

/**
 * Static class of useful string functions that can be used throughout the app.
 */

public class Text {

    public static String removeUnicodeReplacementCharacter(String str)
    {
        return str.replaceAll("\uFFFD", "'").replaceAll("ï¿½", "'");
    }


}
