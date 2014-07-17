package org.mediawiki.api.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fluent interface to easily build up an API request from params.
 */
public class RequestBuilder {
    /**
     * LinkedHashMap used to hold the parameters with which to make the API call.
     */
    private final LinkedHashMap<String, String> params;

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
        params = new LinkedHashMap<String, String>(14, .8f, false);
        params.put("action", action); // put action first to match robots.txt whitelist of action=mobileview for app search indexing
        params.put("format", "json"); // Force everything to be JSON
    }

    /**
     * @return A copy of the current set of parameters for this request
     */
    public Map<String, String> getParams() {
        return new LinkedHashMap<String, String>(params);
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
