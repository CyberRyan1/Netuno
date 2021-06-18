package com.github.cyberryan1.netuno.managers;

public class MutechatManager {

    private static boolean chatMuted = false;

    public static boolean chatIsMuted() {
        return chatMuted;
    }

    public static void setChatMuted( boolean cm ) {
        chatMuted = cm;
    }

}
