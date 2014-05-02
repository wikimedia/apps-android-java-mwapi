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
                new Api("test.wikipedia.org", true).getApiUrl().toString(),
                "https://test.wikipedia.org/w/api.php"
        );
        assertEquals(
                new Api("test.wikipedia.org", false).getApiUrl().toString(),
                "http://test.wikipedia.org/w/api.php"
        );
    }
    @Test
    public void testFull() throws Exception {
        assertEquals(
                new Api("test.wikipedia.org", true, "/api.php").getApiUrl().toString(),
                "https://test.wikipedia.org/api.php"
        );
        assertEquals(
                new Api("test.wikipedia.org", "java-mwapi-UA").getApiUrl().toString(),
                "https://test.wikipedia.org/w/api.php"
        );
        HashMap<String,String> additionalHeaders = new java.util.HashMap<String,String>();
        additionalHeaders.put("X-Java-Mwapi-UnitTest", "java-mwapi-UA");
        assertEquals(
                new Api("test.wikipedia.org", additionalHeaders).getApiUrl().toString(),
                "https://test.wikipedia.org/w/api.php"
        );
        assertEquals(
                new Api("test.wikipedia.org", "java-mwapi-UA", additionalHeaders).getApiUrl().toString(),
                "https://test.wikipedia.org/w/api.php"
        );
        assertEquals(
                new Api("test.wikipedia.org", false, "/api.php").getApiUrl().toString(),
                "http://test.wikipedia.org/api.php"
        );
    }

    @Test
    public void testBasic() throws Exception {
        assertEquals(
                new Api("test.wikipedia.org").getApiUrl().toString(),
                "https://test.wikipedia.org/w/api.php"
                );
    }
}
