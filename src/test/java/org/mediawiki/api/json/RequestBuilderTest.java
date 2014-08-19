package org.mediawiki.api.json;

import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests that work on the RequestBuilder level.
 */
public class RequestBuilderTest {

    /** Tests that "action" is the first parameter for app indexing to work properly */
    @Test
    public void testActionIsFirstParam() throws Exception {
        RequestBuilder requestBuilder = new RequestBuilder(new Api("test.wikipedia.org"), "foo");
        final Iterator<Map.Entry<String, String>> entryIterator = requestBuilder.getParams().entrySet().iterator();
        assertEquals("action", entryIterator.next().getKey());
        assertEquals("format", entryIterator.next().getKey());
    }
}
