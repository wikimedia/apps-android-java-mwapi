package org.mediawiki.api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for different ways to construct the Api object.
 */
public class ApiConstructionTests {

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
