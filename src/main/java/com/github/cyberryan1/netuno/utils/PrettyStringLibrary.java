package com.github.cyberryan1.netuno.utils;

import java.util.List;

/**
 * Methods to help prettyify strings
 *
 * @author Ryan
 */
public class PrettyStringLibrary {

    /**
     * Creates a grammatically correct, comma separated list of
     * the elements, with the word "and" added for the last
     * element. Examples are shown below:
     * <ul>
     *     <li>"One" -> "One"</li>
     *     <li>"One", "Two" -> "One and Two"</li>
     *     <li>"One", "Two", "Three" -> "One, Two and Three"</li>
     *     <li>"One", "Two", "Three", "Four" -> "One, Two, Three and Four"</li>
     * </ul>
     *
     * @param list The list
     * @return A comma separated list
     */
    public static String getNonOxfordCommaList( List<String> list ) {
        return getNonOxfordCommaList( list, list.size() );
    }

    /**
     * Alias to {@link #getNonOxfordCommaList(List)}, but adds a
     * maximum limit on how many elements will be added to the
     * string.
     *
     * @param list The list
     * @param max  Maximum number of elements to add to the
     *             returned string
     * @return A comma separated list
     * @see #getNonOxfordCommaList(List)
     */
    public static String getNonOxfordCommaList( List<String> list, int max ) {
        // ? According to previous me, there was an issue with this code,
        // ?    but I cannot find the test case for it anymore
        if ( list.isEmpty() ) return "";

        String toReturn = list.get( 0 );
        for ( int index = 1; index < list.size(); index++ ) {
            if ( index >= max ) break;

            // Adding " and " instead of ", "
            if ( index + 1 == max || index + 1 == list.size() ) {
                toReturn += " and " + list.get( index );
            }
            else {
                toReturn += ", " + list.get( index );
            }
        }

        return toReturn;
    }

    /**
     * Similar to {@link #getNonOxfordCommaList(List, int)}, but
     * when the <code>max - 1</code> is reached, adds
     * <code>"and (list size - max) more"</code> to the end.
     * <br>
     * Examples shown below are given with the max being set to
     * 3
     * <ul>
     *     <li>"One" -> "One"</li>
     *     <li>"One", "Two" -> "One and Two"</li>
     *     <li>"One", "Two", "Three" -> "One, Two and 1 more"</li>
     *     <li>"One", "Two", "Three", "Four" -> "One, Two and 2 more"</li>
     * </ul>
     *
     * @param list The list
     * @param max  The maximum number of elements to add to the
     *             returned string
     * @return A comma separated list
     */
    public static String getNonOxfordCommaListWithRemainder( List<String> list, int max ) {
        String toReturn = getNonOxfordCommaList( list, max );
        if ( list.size() <= max ) return toReturn;

        toReturn = toReturn.substring( 0, toReturn.indexOf( " and" ) );
        int numberRemainingElements = list.size() - max + 1;
        toReturn += " and " + numberRemainingElements + " more";
        return toReturn;
    }
}