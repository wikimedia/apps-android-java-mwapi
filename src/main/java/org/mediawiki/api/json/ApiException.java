package org.mediawiki.api.json;

/**
 * Wrapper exception thrown whenever there's an unrecoverable error from the Api.
 */
public class ApiException extends Exception {
    public ApiException(final Exception cause) {
        super(cause);
    }
}
