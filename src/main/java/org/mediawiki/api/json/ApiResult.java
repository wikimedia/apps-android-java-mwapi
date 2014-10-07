package org.mediawiki.api.json;

import com.github.kevinsawicki.http.HttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.List;

/**
 * Encapsulates the result of performing an API call.
 *
 * The network request is made *only* when {@link #asArray()} or {@link #asObject()}
 * is called.
 */
public class ApiResult {
    /**
     * Request for which this object holds the results.
     */
    private final HttpRequest request;
    private final Api api;
    private Map<String, List<String>> headers;

    /**
     * Create an APIResult object corresponding to this request object.
     *
     * @param request {@link HttpRequest} object which can perform the query for which this object holds the results.
     */
    ApiResult(final Api api, final HttpRequest request) {
        this.api = api;
        this.request = request;
    }

    /**
     * Disconnect the request if it is in progress.
     */
    public void cancel() {
        request.disconnect();
    }

    private JSONArray resultArray;
    /**
     * Start the network request & return the response as a JSON Array.
     * Locally cache the result too, so multiple calls to this method will succeeed.
     *
     * Use this when a JSON Array is returned by the API. So far, only action=opensearch.
     *
     * @return A {@link JSONArray} object with the results of the API query.
     * @throws ApiException Thrown in the case of a network error, or if the response is not a JSON Array.
     */
    public JSONArray asArray() throws ApiException {
        try {
            if (resultArray == null) {
                extractResponseHeaders();
                resultArray = new JSONArray(request.body());
            }
            return resultArray;
        } catch (JSONException e) {
            throw new ApiException(e);
        } catch (HttpRequest.HttpRequestException e) {
            throw new ApiException(e.getCause());
        } catch (SecurityException e) {
            throw new ApiException(e);
        }
    }

    private JSONObject resultObject;
    /**
     * Start the network request & return the response as a JSON Object.
     * Locally cache the result too, so multiple calls to this method will succeeed.
     *
     * @return A {@link JSONObject} object with the results of the API query.
     * @throws ApiException Thrown in the case of a network error, or if the response is not a JSON Object.
     */
    public JSONObject asObject() throws ApiException {
        try {
            if (resultObject == null) {
                extractResponseHeaders();
                resultObject = new JSONObject(request.body());
            }
            return resultObject;
        } catch (JSONException e) {
            throw new ApiException(e);
        } catch (HttpRequest.HttpRequestException e) {
            throw new ApiException(e.getCause());
        } catch (SecurityException e) {
            throw new ApiException(e);
        }
    }

    private void extractResponseHeaders() {
        headers = request.headers();
        api.processHeaders(this);
    }

    /**
     * Get the Map of headers returned for the response. Note that this must be called after asArray or asObject.
     *
     * @return Map<String, List<String>>
     * @throws NullPointerException
     */
    public Map<String, List<String>> getHeaders() throws NullPointerException {
        if (headers != null) {
                return headers;
        }
        throw new NullPointerException("getHeaders must be called after asArray or asObject");
    }
}
