package org.mediawiki.api;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.*;

/**
 * Tests that actually hit the API to return something.
 */
public class ApiTests {

    Api api;

    @Before
    public void setUp() throws Exception {
        api = new Api("test.wikipedia.org");
    }

    @Test
    public void testBasicParse() throws Exception {
        String inputText = "Test String";
        String inputTitle = "Test Title";
        JSONObject resp = api.action("parse").param("title", inputTitle).param("text", inputText).param("prop", "wikitect").get();
        assertEquals(resp.optJSONObject("parse").optJSONObject("wikitext").optString("*"), inputText);
    }
}
