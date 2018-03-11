package cityguide.garren.com;

import org.json.JSONObject;
import org.junit.Test;

import cityguide.garren.com.utils.DownloadUrl;

import static org.junit.Assert.*;

/**
 * Created by gsteigers on 3/4/18.
 */

public class DownloadUrlTests {
    @Test
    public void readInvalidUrlTest() {
        DownloadUrl downloadUrl = new DownloadUrl();
        Boolean failed = false;

        String result = null;
        try {
            result = downloadUrl.readUrl("fakeurl");
        } catch (Exception e) {
            failed = true;
        }

        assertNull(result);
        assertEquals(failed, true);
    }

    @Test
    public void readValidUrlTest() {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=47.751119,-122.0239852&radius=50000&types=%22bar%22&sensor=true&key=AIzaSyAN9A_M6Fb84fPCQNibK_sQJwuHf1qgpgw";
        DownloadUrl downloadUrl = new DownloadUrl();
        Boolean failed = false;

        String result = null;
        try {
            result = downloadUrl.readUrl(url);
        } catch (Exception e) {
            failed = true;
        }

        assertNotNull(result);
        assertEquals(failed, false);
    }

    @Test
    public void readMissingAPIKey() {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=47.751119,-122.0239852&radius=50000&types=%22bar%22&";
        DownloadUrl downloadUrl = new DownloadUrl();
        Boolean failed = false;

        String result = null;
        try {
            result = downloadUrl.readUrl(url);
        } catch (Exception e) {
            failed = true;
        }

        assertNotNull(result);
        assertEquals(failed, false);
    }
}