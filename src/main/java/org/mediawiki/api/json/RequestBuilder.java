package org.mediawiki.api.json;

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
     * Sets up the request that has been constructed so far.
     *
     * Use {@link org.mediawiki.api.json.ApiResult#asArray()} or {@link org.mediawiki.api.json.ApiResult#asObject()} to
     * actually start the network request and get the response.
     *
     * @param method HTTP Method to use when performing the request
     * @return An {@link ApiResult} object which can be used to get the result of this query.
     */
    private ApiResult setupRequest(final int method) {
        return api.setupRequest(method, this);
    }

    /**
     * Sets up a GET request using the parameters so far specified.
     * Use {@link org.mediawiki.api.json.ApiResult#asArray()} or {@link org.mediawiki.api.json.ApiResult#asObject()} to
     * actually start the network request and get the response.
     *
     * @return An {@link ApiResult} object which can be used to get the result of this query.
     */
    public ApiResult get() {
        return setupRequest(Api.METHOD_GET);
    }

    /**
     * Sets up a POST request using the parameters so far specified.
     *
     * Use {@link org.mediawiki.api.json.ApiResult#asArray()} or {@link org.mediawiki.api.json.ApiResult#asObject()} to
     * actually start the network request and get the response.
     *
     * @return An {@link ApiResult} object which can be used to get the result of this query.
     */
    public ApiResult post() {
        return setupRequest(Api.METHOD_POST);
    }
}
