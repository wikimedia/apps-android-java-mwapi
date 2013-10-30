package org.mediawiki.api.json;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Fluent interface to easily build up an API request from params.
 */
public class RequestBuilder {
    /**
     * Hashmap used to hold the parameters with which to make the API call.
     */
    private final HashMap<String, String> params;

    /**
     * Api object with which the request being built is associated.
     */
    private final Api api;

    /**
     * Create a new RequestBuilder to build API requests.
     *
     * @param apiToUse The Api that will be used to perform the request.
     * @param action The action this API query is for
     */
    RequestBuilder(final Api apiToUse, final String action) {
        this.api = apiToUse;
        params = new HashMap<String, String>();
        params.put("format", "json"); // Force everything to be JSON
        params.put("action", action);
    }

    /**
     * @return A copy of the curent set of parameters for this request
     */
    public HashMap<String, String> getParams() {
        return new HashMap<String, String>(params);
    }

    /**
     * Add a parameter to the current request.
     *
     * @param key Parameter's name
     * @param value Parameter's value
     * @return The `this` object, so you can chain params together
     */
    public RequestBuilder param(final String key, final String value) {
        params.put(key, value);
        return this;
    }

    /**
     * Performs the request that has been built up so far and returns the JSON Object.
     *
     * @param method HTTP Method to use when performing the request
     * @throws ApiException In case of NetworkError or JSON parsing error
     * @return The result of the API request
     */
    private JSONObject makeRequest(final int method) throws ApiException {
        return api.makeRequest(method, this);
    }

    /**
     * Performs a GET request using the parameters so far specified.
     *
     * @throws ApiException In case of NetworkError or JSON parsing error
     * @return The result of the API request
     */
    public JSONObject get() throws ApiException {
        return makeRequest(Api.METHOD_GET);
    }

    /**
     * Performs a POST request using the parameters so far specified.
     *
     * @throws ApiException In case of NetworkError or JSON parsing error
     * @return The result of the API request
     */
    public JSONObject post() throws ApiException {
        return makeRequest(Api.METHOD_POST);
    }
}
