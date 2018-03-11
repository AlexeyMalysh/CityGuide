package cityguide.garren.com;

import android.util.Pair;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import cityguide.garren.com.utils.DataParser;

import static org.junit.Assert.*;

/**
 * Created by gsteigers on 3/4/18.
 */

public class DataParserTests {
    @Test
    public void testParseNullResponse() {
        Pair<String, List<HashMap<String, String>>> result;
        DataParser parser = new DataParser();
        result = parser.parse(null);
        assertNull(result.first);
        assertNull(result.second);
    }

    @Test
    public void testParseEmptyResponse() {
        Pair<String, List<HashMap<String, String>>> result;
        DataParser parser = new DataParser();
        result = parser.parse("");
        assertNull(result.first);
        assertNull(result.second);
    }
}


