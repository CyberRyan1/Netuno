package com.github.cyberryan1.netuno.managers;

import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;

public class ChatslowManager {

    private static final Database DATA = Utils.getDatabase();
    private static final String DATA_KEY = "chatslow-amount";

    private static int slow;

    public ChatslowManager() {
        if ( DATA.otherCheckKeyExists( DATA_KEY ) == false ) {
            slow = YMLUtils.getConfig().getInt( "chatslow.default-value" );
            DATA.addOther( DATA_KEY, slow + "" );
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
