package cityguide.garren.com;

import org.junit.Test;

import cityguide.garren.com.utils.PermissionUtil;

import static junit.framework.Assert.*;

/**
 * Created by gsteigers on 3/4/18.
 */

public class PermissionUtilTests {
    @Test
    public void verifyNoPermissionTest() {
        Boolean response = PermissionUtil.verifyPermissions(null);
        assertFalse(response);
    }

    @Test
    public void verifySinglePermissionGrantedTest() {
        int[] permissions = {0};
        Boolean response = PermissionUtil.verifyPermissions(permissions);
        assertTrue(response);
    }

    @Test
    public void verifyMultiplePermissionGrantedTest() {
        int[] permissions = {0, 0, 0, 0, 0};
        Boolean response = PermissionUtil.verifyPermissions(permissions);
        assertTrue(response);
    }

    @Test
    public void verifySinglePermissionNotGrantedTest() {
        int[] permissions = {-1};
        Boolean response = PermissionUtil.verifyPermissions(permissions);
        assertFalse(response);
    }

    @Test
    public void verifyMultiplePermissionNotGrantedTest() {
        int[] permissions = {-1, -1, -1, -1, -1};
        Boolean response = PermissionUtil.verifyPermissions(permissions);
        assertFalse(response);
    }

    @Test
    public void verifyMultiplePermissionMixedGrantedTest() {
        int[] permissions = {0, -1, 0, -1, 0};
        Boolean response = PermissionUtil.verifyPermissions(permissions);
        assertFalse(response);
    }
}
