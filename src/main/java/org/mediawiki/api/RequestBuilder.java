package org.mediawiki.api;

import org.json.JSONObject;

/**
 * Fluent interface to easily build up an API request from params.
 */
public class RequestBuilder {
    /**
     * Create a new RequestBuilder to build API requests.
     *
     * @param api The Api that will be used to perform the request.
     * @param action The action this API query is for
     */
    RequestBuilder(final Api api, final String action) {
    }

    /**
     * Add a parameter to the current request.
     *
     * @param key Parameter's name
     * @param value Parameter's value
     * @return The `this` object, so you can chain params together
     */
    public RequestBuilder param(final String key, final String value) {
        return null;
    }

    /**
     * Performs the request that has been built up so far and returns the JSON Object.
     *
     * @param method HTTP Method to use when performing the request
     * @return The result of the API request
     */
    private JSONObject makeRequest(final String method) {
        return null;
    }

    /**
     * Performs a GET request using the parameters so far specified.
     *
     * @return The result of the API request
     */
    public JSONObject get() {
        return null;
    }

    /**
     * Performs a POST request using the parameters so far specified.
     *
     * @return The result of the API request
     */
    public JSONObject post() {
        return null;
    }
}
