package org.mediawiki.api.json;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.HashMap;

/**
 * Tests for different ways to construct the Api object.
 */
public class ApiConstructionTest {

    @Test
    public void testSecure() throws Exception {
        assertEquals(
                "https://test.wikipedia.org/w/api.php",
                new Api("test.wikipedia.org", true).getApiUrl().toString()
        );
        assertEquals(
                "http://test.wikipedia.org/w/api.php",
                new Api("test.wikipedia.org", false).getApiUrl().toString()
        );
    }
    @Test
    public void testFull() throws Exception {
        assertEquals(
                "https://test.wikipedia.org/api.php",
                new Api("test.wikipedia.org", true, "/api.php").getApiUrl().toString()
        );
        assertEquals(
                "https://test.wikipedia.org/w/api.php",
                new Api("test.wikipedia.org", "java-mwapi-UA").getApiUrl().toString()
        );
        HashMap<String,String> additionalHeaders = new java.util.HashMap<String,String>();
        additionalHeaders.put("X-Java-Mwapi-UnitTest", "java-mwapi-UA");
        assertEquals(
                "https://test.wikipedia.org/w/api.php",
                new Api("test.wikipedia.org", additionalHeaders).getApiUrl().toString()
        );
        assertEquals(
                "https://test.wikipedia.org/w/api.php",
                new Api("test.wikipedia.org", "java-mwapi-UA", additionalHeaders).getApiUrl().toString()
        );
        assertEquals(
                "http://test.wikipedia.org/api.php",
                new Api("test.wikipedia.org", false, "/api.php").getApiUrl().toString()
        );
    }

    @Test
    public void testBasic() throws Exception {
        assertEquals(
                "https://test.wikipedia.org/w/api.php",
                new Api("test.wikipedia.org").getApiUrl().toString()
        );
    }
}
