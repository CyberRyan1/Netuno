package com.github.cyberryan1.netuno.guis.report;

import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class ReportUtils {

    public static final int MAX_REASONS_SIZE = 18;
    public static final List<String> AVAILABLE_REASONS = new ArrayList<>();

    public static void updateAvailableReasons() {
        AVAILABLE_REASONS.clear();
        AVAILABLE_REASONS.addAll( Settings.REPORT_REASONS_LIST.arraylist() );
        checkAndLogErrors();
    }

    /**
     * Checks for any errors and logs them in console
     * @return true if errors were found, false otherwise
     */
    public static boolean checkAndLogErrors() {
        if ( AVAILABLE_REASONS.isEmpty() ) {
            CyberLogUtils.logError( "CONFIG ERROR >> No reasons have been configured for reports!" );
            return true;
        }
        else if ( AVAILABLE_REASONS.size() > MAX_REASONS_SIZE ) {
            CyberLogUtils.logError( "CONFIG ERROR >> The list \"" + Settings.REPORT_REASONS_LIST.getPath()
                    + "\" is greater than the limit of " + MAX_REASONS_SIZE + ". Reports will not work until this is fixed." );
            return true;
        }
        return false;
    }
}
