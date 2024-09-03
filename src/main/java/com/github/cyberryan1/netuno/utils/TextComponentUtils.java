package com.github.cyberryan1.netuno.utils;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashMap;
import java.util.Map;

public class TextComponentUtils {

    public static TextComponent toTextComponent( String text ) {
        text = CyberColorUtils.reverseColor( text );
        final char split[] = text.toCharArray();
        TextComponent toReturn = Component.empty();

        // Color of the current chat component
        TextColor currentColor = NamedTextColor.WHITE;
        // Decorations (bold, italic, etc.) of the current chat component
        Map<TextDecoration, TextDecoration.State> currentDecorations = new HashMap<>();

        for ( int index = 0; index < split.length; index++ ) {
            char currentChar = split[index];

            // Checking for color/decoration codes
            if ( currentChar == '&' && index + 1 <= split.length ) {
                final char nextChar = split[index + 1];
                int codeLength = 0;

                // Using a standard color code from &0 to &9 or from &a to &f
                if ( ( nextChar >= '0' && nextChar <= '9' ) || ( nextChar >= 'a' && nextChar <= 'f' ) ) {
                    codeLength = 1;

                    // Resetting current decorations
                    currentDecorations.put( TextDecoration.BOLD, TextDecoration.State.NOT_SET );
                    currentDecorations.put( TextDecoration.ITALIC, TextDecoration.State.NOT_SET );
                    currentDecorations.put( TextDecoration.UNDERLINED, TextDecoration.State.NOT_SET );
                    currentDecorations.put( TextDecoration.STRIKETHROUGH, TextDecoration.State.NOT_SET );
                    currentDecorations.put( TextDecoration.OBFUSCATED, TextDecoration.State.NOT_SET );

                    currentColor = switch ( nextChar ) {
                        case '0' -> NamedTextColor.BLACK;
                        case '1' -> NamedTextColor.DARK_BLUE;
                        case '2' -> NamedTextColor.DARK_GREEN;
                        case '3' -> NamedTextColor.DARK_AQUA;
                        case '4' -> NamedTextColor.DARK_RED;
                        case '5' -> NamedTextColor.DARK_RED;
                        case '6' -> NamedTextColor.GOLD;
                        case '7' -> NamedTextColor.GRAY;
                        case '8' -> NamedTextColor.DARK_GRAY;
                        case '9' -> NamedTextColor.BLUE;
                        case 'a' -> NamedTextColor.GREEN;
                        case 'b' -> NamedTextColor.AQUA;
                        case 'c' -> NamedTextColor.RED;
                        case 'd' -> NamedTextColor.LIGHT_PURPLE;
                        case 'e' -> NamedTextColor.YELLOW;
                        case 'f' -> NamedTextColor.WHITE;
                        default -> throw new IllegalArgumentException( "Invalid color code \"&" + nextChar + "\"" );
                    };
                }

                // Using a standard decoration code from &k to &o
                else if ( nextChar >= 'k' && nextChar <= 'o' ) {
                    codeLength = 1;
                    currentDecorations.put( switch ( nextChar ) {
                        case 'k' -> TextDecoration.OBFUSCATED;
                        case 'l' -> TextDecoration.BOLD;
                        case 'm' -> TextDecoration.STRIKETHROUGH;
                        case 'n' -> TextDecoration.UNDERLINED;
                        case 'o' -> TextDecoration.ITALIC;
                        default -> throw new IllegalArgumentException( "Invalid color code \"&" + nextChar + "\"" );
                    }, TextDecoration.State.TRUE );
                }

                // Using a hex code (length of these is 6 characters after the hash)
                //      index + 8 must be less than split.length otherwise we will go out of bounds
                else if ( nextChar == '#' && index + 8 < split.length ) {
                    char hexCodes[] = new char[6];
                    for ( int i = 0; i < 6; i++ ) { hexCodes[i] = split[index + i + 2]; }

                    String hexStr = String.valueOf( hexCodes );
                    int hexValue = Integer.parseInt( hexStr, 16 );
                    currentColor = TextColor.color( hexValue );

                    codeLength = 7;
                    currentDecorations.clear();
                }

                // Adding the & to the final message
                else {
                    toReturn = toReturn.append( Component.text( currentChar )
                            .color( currentColor )
                            .decorations( currentDecorations )
                    );
                }

                index += codeLength;
            }

            // Adding the current character to the final message, with colors and decorations
            else {
                toReturn = toReturn.append( Component.text( currentChar )
                        .color( currentColor )
                        .decorations( currentDecorations )
                );
            }
        }

        return toReturn;
    }
}