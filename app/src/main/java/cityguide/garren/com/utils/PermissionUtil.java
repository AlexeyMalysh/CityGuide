package cityguide.garren.com.utils;

import android.content.pm.PackageManager;

/**
 * Created by gsteigers on 3/2/18.
 */

public abstract class PermissionUtil {

    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults == null) {
            return false;
        }
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}