package org.mediawiki.api.json;

import static org.junit.Assert.*;

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
        assertEquals(resp.optJSONObject("login").optString("result"), "NeedToken");
        assertNull(resp.optJSONObject("error"));
    }

    @Test
    public void testWrongMethod() throws Exception {
        Api api = getApi();
        JSONObject resp = api.action("login")
                .get().asObject();

        assertEquals(resp.optJSONObject("error").optString("code"), "mustbeposted");
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
        assertEquals(resp.optJSONObject("parse").optJSONObject("wikitext").optString("*"), inputText);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMethod() throws Exception {
        Api api = getApi();
        api.setupRequest(404, null);
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
            api.action("somethingdoesnmtatter").param("format", "xml").get().asObject();
        } catch (ApiException e) {
            assertTrue(e.getCause() instanceof JSONException);
            return;
        }
        assertTrue("This means no exception was thrown, and hence test fails", false);
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
            api.action("somethingdoesnmtatter").param("format", "xml").get().asObject();
        } catch (ApiException e) {
            assertTrue(e.getCause() instanceof IOException);
            return;
        }
        assertTrue("This means no exception was thrown, and hence test fails", false);
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
    @Test(expected=NullPointerException.class)
    public void testGetHeadersOutOfOrder() throws Exception {
        boolean exceptionWasCaught = false;
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
        HashMap<String,String> getApi = new HashMap<String,String>();
        getApi.put("X-Java-Mwapi-UnitTest", "java-mwapi-UA");
        Api api = new Api("test.wikipedia.org", "java-mwapi-UA", getApi);
        return api;
    }
}
