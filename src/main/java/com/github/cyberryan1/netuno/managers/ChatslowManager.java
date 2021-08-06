package com.github.cyberryan1.netuno.managers;

import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;

public class ChatslowManager {

    private static final Database DATA = Utils.getDatabase();
    private static final String DATA_KEY = "chatslow-amount";

    private static int slow;

    public ChatslowManager() {
        if ( DATA.otherCheckKeyExists( "chatslow-amount" ) == false ) {
            setSlow( ConfigUtils.getInt( "chatslow.default-value" ) );
        }
        else {
            setSlow( Integer.parseInt( DATA.getOther( DATA_KEY ) ) );
        }
    }

    public static void updateDatabase() {
        DATA.updateOther( DATA_KEY, "" + slow );
    }

    public static int getSlow() { return slow; }

    public static void setSlow( int newSlow ) {
        slow = newSlow;
        updateDatabase();
    }
}
