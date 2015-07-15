package org.mediawiki.api.json;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import java.io.IOException;

/**
 * Tests that actually hit the API to return something.
 */
public class ApiTest {
    @Test
    public void testBasicPost() throws Exception {
        Api api = getApi();
        // We're just checking if the POST goes through, so does not
        // matter which username password we use
        String testUsername = "doesntmatter";
        String testPassword = "doesntreallymattertome";

        JSONObject resp = api.action("login")
                .param("lgname", testUsername)
                .param("lgpassword", testPassword)
                .post().asObject();
        assertEquals("NeedToken", resp.optJSONObject("login").optString("result"));
        assertNull(resp.optJSONObject("error"));
    }

    @Test
    public void testWrongMethod() throws Exception {
        try {
            getApi().action("login").get().asObject();
            // shouldn't reach this point:
            fail("Expected ApiException wasn't thrown.");
        } catch (ApiException e) {
            assertEquals("mustbeposted", e.getCode());
        }
    }

    @Test
    public void testBasicGet() throws Exception {
        Api api = getApi();
        String inputText = "Test String";
        String inputTitle = "Test Title";
        JSONObject resp = api.action("parse")
                .param("title", inputTitle)
                .param("text", inputText)
                .param("prop", "wikitext")
                .get().asObject();
        assertEquals(inputText, resp.optJSONObject("parse").optJSONObject("wikitext").optString("*"));
    }

    /**
     * Test to verify that parameter values with and ampersand get encoded properly.
     * Inspired by https://bugzilla.wikimedia.org/show_bug.cgi?id=66152
     */
    @Test
    public void testGetWithAmpersand() throws Exception {
        Api api = getApi();
        JSONObject resp = api.action("mobileview")
                .param("page", "Ampersand_&_title")
                .param("prop", "text|sections")
                .param("onlyrequestedsections", "1")
                .param("sections", "0")
                .param("sectionprop", "toclevel|line|anchor")
                .param("noheadings", "true")
                .get().asObject();
        assertEquals(
                "<p>Testing a page with &amp; in the title</p>",
                resp.optJSONObject("mobileview").optJSONArray("sections").getJSONObject(0).optString("text")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMethod() throws Exception {
        Api api = getApi();
        api.setupRequest(HttpURLConnection.HTTP_NOT_FOUND, null);
    }

    /**
     * This tests for responses that aren't JSON, but something else.
     *
     * Usually this happens when a backend apache times out and we get the
     * timeout HTML page from the frontend caches. Since that is not
     * reliably reproducible, and we aren't using network mocks, I can
     * simulate it by simply requesting for the XML format.
     */
    @Test
    public void testJSONException() {
        Api api = getApi();
        try {
            api.action("somethingdoesntmatter").param("format", "xml").get().asObject();
        } catch (ApiException e) {
            assertTrue(e.getCause() instanceof JSONException);
            return;
        }
        fail("This means no exception was thrown, and hence test fails");
    }

    /**
     * Test for Network failure throwing the proper exception.
     *
     * Usually this happens due to various network failures, but since we can not
     * reproduce that reliably without network mocks, I am simply calling into a
     * random hopefully nonexistant hostname to get the network failure.
     */
    @Test
    public void testNetworkException() {
        Api api = new Api("blashblahblahdoesnotexist", "java-mwapi-UA");
        try {
            api.action("somethingdoesntmatter").param("format", "xml").get().asObject();
        } catch (ApiException e) {
            assertTrue(e.getCause() instanceof IOException);
            return;
        }
        fail("This means no exception was thrown, and hence test fails");
    }

    /**
     * Test to verify that headers are properly received.
     *
     * Verifies that headers were received in the response to a well formed request.
     * Note that the result object needs to be acquired so that headers can be pulled down,
     * hence the ordering here.
     */
    @Test
    public void testGetHeaders() throws Exception {
        Api api = getApi();
        String inputText = "Test String";
        String inputTitle = "Test Title";
        ApiResult result = api.action("parse")
                .param("title", inputTitle)
                .param("text", inputText)
                .param("prop", "wikitext")
                .get();
        JSONObject jsonObject = result.asObject();
        Map<String, List<String>> m = result.getHeaders();
        assertNotNull(m);
        assertFalse(m.isEmpty());
        assertTrue(m.containsKey("Content-Type"));
    }

    /**
     * Test to verify that accessing headers before asObject throws.
     */
    @Test(expected = NullPointerException.class)
    public void testGetHeadersOutOfOrder() throws Exception {
        Api api = getApi();
        String inputText = "Test String";
        String inputTitle = "Test Title";
        ApiResult result = api.action("parse")
                .param("title", inputTitle)
                .param("text", inputText)
                .param("prop", "wikitext")
                .get();
        Map<String, List<String>> m = result.getHeaders();
    }

    /**
     *
     * @return API with test-friendly construction
     */
    public Api getApi() {
        HashMap<String, String> getApi = new HashMap<>();
        getApi.put("X-Java-Mwapi-UnitTest", "java-mwapi-UA");
        return new Api("test.wikipedia.org", "java-mwapi-UA", getApi);
    }
}
