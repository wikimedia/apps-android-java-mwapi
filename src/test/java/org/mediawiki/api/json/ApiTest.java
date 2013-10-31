package org.mediawiki.api.json;

import static org.junit.Assert.*;

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
        Api api = new Api("test.wikipedia.org");
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
        Api api = new Api("test.wikipedia.org");
        JSONObject resp = api.action("login")
                .get().asObject();

        assertEquals(resp.optJSONObject("error").optString("code"), "mustbeposted");
    }

    @Test
    public void testBasicGet() throws Exception {
        Api api = new Api("test.wikipedia.org");
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
        Api api = new Api("test.wikipedia.org");
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
        Api api = new Api("test.wikipedia.org");
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
        Api api = new Api("blashblahblahdoesnotexist");
        try {
            api.action("somethingdoesnmtatter").param("format", "xml").get().asObject();
        } catch (ApiException e) {
            assertTrue(e.getCause() instanceof IOException);
            return;
        }
        assertTrue("This means no exception was thrown, and hence test fails", false);
    }

}
