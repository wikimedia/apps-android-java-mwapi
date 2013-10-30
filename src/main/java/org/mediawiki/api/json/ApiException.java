package org.mediawiki.api.json;

import org.json.JSONException;

import java.io.IOException;

/**
 * Wrapper exception thrown whenever there's an unrecoverable error from the Api.
 *
 * With sadness in my heart, I make this a checked exception, since this does
 * need to be handled, and it is encapsulating a couple of other checked
 * exceptions.
 */
public class ApiException extends Exception {
    /**
     * Create a wrapper around an IOException.
     *
     * @param cause IOException around which this is wrapped.
     */
    public ApiException(final IOException cause) {
        super(cause);
    }

    /**
     * Create a wrapper around a JSONException.
     *
     * @param cause JSONException around which this is wrapped.
     */
    public ApiException(final JSONException cause) {
        super(cause);
    }
}
