package org.mediawiki.api;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.*;

/**
 * Tests that actually hit the API to return something.
 */
public class ApiTests {

    @Test
    public void testBasicPost() {
        Api api = new Api("test.wikipedia.org");
        // We're just checking if the POST goes through, so does not
        // matter which username password we use
        String testUsername = "doesntmatter";
        String testPassword = "doesntreallymattertome";

        JSONObject resp = api.action("login")
                .param("lgname", testUsername)
                .param("lgpassword", testPassword)
                .post();
        assertEquals(resp.optJSONObject("login").optString("result"), "NeedToken");
        assertNull(resp.optJSONObject("error"));
    }

    @Test
    public void testWrongMethod() {
        Api api = new Api("test.wikipedia.org");
        JSONObject resp = api.action("login")
                .get();

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
                .get();
        assertEquals(resp.optJSONObject("parse").optJSONObject("wikitext").optString("*"), inputText);
    }
}
