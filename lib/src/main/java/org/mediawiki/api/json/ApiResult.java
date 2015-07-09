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
     * @throws ApiException Thrown in the case of any error(s). Check the inner exception for details.
     */
    public JSONArray asArray() throws ApiException {
        try {
            if (resultArray == null) {
                extractResponseHeaders();
                assertSuccess();
                resultArray = new JSONArray(request.body());
            }
            return resultArray;
        } catch (HttpRequest.HttpRequestException e) {
            throw new ApiException(e.getCause());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    private JSONObject resultObject;
    /**
     * Start the network request & return the response as a JSON Object.
     * Locally cache the result too, so multiple calls to this method will succeeed.
     *
     * @return A {@link JSONObject} object with the results of the API query.
     * @throws ApiException Thrown in the case of any error(s). Check the inner exception for details.
     */
    public JSONObject asObject() throws ApiException {
        try {
            if (resultObject == null) {
                extractResponseHeaders();
                assertSuccess();
                resultObject = new JSONObject(request.body());
            }
            return resultObject;
        } catch (HttpRequest.HttpRequestException e) {
            throw new ApiException(e.getCause());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    private void extractResponseHeaders() {
        headers = request.headers();
        api.processHeaders(this);
    }

    private void assertSuccess() throws JSONException, ApiException {
        // check the http status code
        if (!request.ok()) {
            throw new ApiException(Integer.toString(request.code()), request.message());
        }
        // check for a header that indicates an error...
        final String apiErrorKey = "MediaWiki-API-Error";
        if (headers.containsKey(apiErrorKey)) {
            // unwrap the json response, and build an exception out of it.
            JSONObject error = (new JSONObject(request.body())).optJSONObject("error");
            if (error != null) {
                throw new ApiException(error.optString("code"), error.optString("info"));
            }
            // if the json response was malformed, then just use the code in the http header.
            throw new ApiException(headers.get(apiErrorKey).get(0), "");
        }
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
