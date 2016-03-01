package org.mediawiki.api.json;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Client wrapper for connecting to a MediaWiki installation's API.
 *
 * - It aims to be fully reentrant.
 * - Uses JSON for everything
 */
public class Api {

    /**
     * Parameter to {@link #setupRequest(int, RequestBuilder)}, performs GET request.
     */
    public static final int METHOD_GET = 1;
    /**
     * Parameter to {@link #setupRequest(int, RequestBuilder)}, performs POST request.
     */
    public static final int METHOD_POST = 2;

    /**
     * The exact URL to which API requests are made.
     */
    private URL apiUrl;

    /**
     * Any custom headers, if specified by the appropriate constructor
     */
    private Map<String, String> customHeaders;

    /**
     * Default API endpoint
     */
    private static final String DEFAULT_ENDPOINT = "/w/api.php";
    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;

    /**
     * Create an Api object with given only hostname.
     *
     * Defaults to HTTPS
     * No custom headers to send with each request
     * Default endpoint (DEFAULT_ENDPOINT) will be used.
     *
     * @param domain Domain name of the MediaWiki API to connect to
     */
    public Api(final String domain) {
        this(domain, true, DEFAULT_ENDPOINT, null);
    }

    /**
     * Create an Api object with given only hostname and user agent.
     *
     * Defaults to HTTPS
     * Default endpoint (DEFAULT_ENDPOINT) will be used
     *
     * @param domain Domain name of the MediaWiki API to connect to
     * @param userAgent Custom User-Agent to simplify identification of consuming application
     */
    public Api(final String domain, final String userAgent) {
        this(domain, true, DEFAULT_ENDPOINT, new HashMap<String, String>());
        this.customHeaders.put("User-Agent", userAgent);
    }

    /**
     * Create an Api object with given only hostname, custom headers, and user agent.
     *
     * Defaults to HTTPS
     * Default endpoint (DEFAULT_ENDPOINT) will be used
     *
     * @param domain Domain name of the MediaWiki API to connect to
     * @param userAgent Custom User-Agent to simplify identification of consuming application
     * @param customHeaders Any extra headers to send with each request, e.g. User-Agent.
     */
    public Api(final String domain, final String userAgent, Map<String, String> customHeaders) {
        this(domain, true, DEFAULT_ENDPOINT, customHeaders);
        this.customHeaders.put("User-Agent", userAgent);
    }

    /**
     * Create an Api object with given only hostname and custom headers.
     *
     * Defaults to HTTPS
     * Default endpoint (DEFAULT_ENDPOINT) will be used
     *
     * @param domain Domain name of the MediaWiki API to connect to
     * @param customHeaders Any extra headers to send with each request, e.g. User-Agent.
     */
    public Api(final String domain, Map<String, String> customHeaders) {
        this(domain, true, DEFAULT_ENDPOINT, customHeaders);
    }

    public Api(final String domain, boolean useSecure, Map<String, String> customHeaders) {
        this(domain, useSecure, DEFAULT_ENDPOINT, customHeaders);
    }

    /**
     * Create an Api object with given only hostname and whether to use HTTPS or not.
     *
     * No custom headers to send with each request
     * Default endpoint (DEFAULT_ENDPOINT) will be used
     *
     * @param domain Domain name of the MediaWiki API to connect to
     * @param useSecure true to use https, false to use http
     */
    public Api(final String domain, final boolean useSecure) {
        this(domain, useSecure, DEFAULT_ENDPOINT, null);
    }

    /**
     * Create an Api object with given only hostname, whether to use HTTPS or not, and endpoint path.
     *
     * No custom headers to send with each request
     *
     * @param domain Domain name of the MediaWiki API to connect to
     * @param useSecure true to use https, false to use http
     * @param endpointPath Path to the api.php file. Require preceding slash.
     */
    public Api(final String domain, final boolean useSecure, final String endpointPath) {
        this(domain, useSecure, endpointPath, null);
    }

    public Api(final String domain, final boolean useSecure, final String endpointPath, Map<String, String> customHeaders) {
        this(domain, useSecure ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT, useSecure, endpointPath, customHeaders);
    }

    /**
     * Create an Api object
     *
     * @param domain Domain name of the MediaWiki API to connect to
     * @param port URL port number.
     * @param useSecure true to use https, false to use http
     * @param endpointPath Path to the api.php file. Require preceding slash.
     * @param customHeaders Any extra headers to send with each request, e.g. User-Agent.
     */
    public Api(final String domain, int port, final boolean useSecure, final String endpointPath, Map<String, String> customHeaders) {
        String protocol;
        if (useSecure) {
            protocol = "https";
        } else {
            protocol = "http";
        }

        try {
            if (useSecure && port != DEFAULT_HTTPS_PORT || !useSecure && port != DEFAULT_HTTP_PORT) {
                apiUrl = new URL(protocol, domain, port, endpointPath);
            } else {
                apiUrl = new URL(protocol, domain, endpointPath);
            }
        } catch (MalformedURLException e) {
            // This never actually is supposed to happen, since it is thrown only
            // when an unknown protocol is given. 'http' or 'https' are guaranteed to be present,
            // according to http://docs.oracle.com/javase/6/docs/json/java/net/URL.html#URL(java.lang.String, java.lang.String, int, java.lang.String)
            throw new RuntimeException(e);
        }
        this.customHeaders = customHeaders;
    }

    /**
     * Returns the full Url to which API requests are directed.
     *
     * @return Full URL to which requests are directed
     */
    public URL getApiUrl() {
        return apiUrl;
    }

    /**
     * Start building a request for a particular action.
     *
     * @param action The <a href="https://www.mediawiki.org/wiki/API:Main_page#The_action">action</a> to start building
     *               a request for.
     * @return A {@link RequestBuilder} instance that can be used to add parameters & execute the request.
     */
    public RequestBuilder action(final String action) {
        return new RequestBuilder(this, action);
    }

    /**
     * Sets up the HTTP request that needs to be made to produce results for this API query.
     *
     * Use {@link org.mediawiki.api.json.ApiResult#asArray()} or
     * {@link org.mediawiki.api.json.ApiResult#asObject()} to get the response.
     *
     * In the case of a GET request, the actual network transaction will only occur when you query
     * the returned {@link ApiResult} object. However, for a POST request, the network transaction
     * will occur immediately in this function, hence the possibility of an {@link ApiException}
     *
     * @param method HTTP method to use when performing the request
     * @param requestBuilder The requestBuilder to use to construct the request
     * @return An {@link ApiResult} object which can be used to get the result of this query.
     * @throws ApiException Thrown in the case of a network error.
     */
    public ApiResult setupRequest(final int method, final RequestBuilder requestBuilder) throws ApiException {
        HttpRequest request;
        switch(method) {
            case METHOD_GET:
                request = HttpRequest.get(getApiUrl().toString(), encodeParams(requestBuilder.getParams()), false);
                break;
            case METHOD_POST:
                request = HttpRequest.post(getApiUrl().toString());
                break;
            default:
                throw new IllegalArgumentException("Unknown argument passed for parameter method");
        }
        request.acceptGzipEncoding();
        request.uncompress(true);
        if (this.customHeaders != null) {
            request = request.headers(customHeaders);
        }
        if (method == METHOD_POST) {
            // catch network-related exceptions, since the form() function performs a
            // network request.
            try {
                request.form(requestBuilder.getParams());
            } catch (HttpRequest.HttpRequestException e) {
                throw new ApiException(e.getCause());
            } catch (SecurityException e) {
                throw new ApiException(e);
            }
        }
        return new ApiResult(this, request);
    }

    private Map<String, String> encodeParams(Map<String, String> params) {
        for (Map.Entry<String, String> entry: params.entrySet()) {
            try {
                entry.setValue(URLEncoder.encode(entry.getValue(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return params;
    }

    /**
     * Set the connection factory used to construct connections.
     *
     * @param factory The factory used to construct HTTP connections
     */
    public static void setConnectionFactory(HttpRequest.ConnectionFactory factory) {
        HttpRequest.setConnectionFactory(factory);
    }

    private OnHeaderCheckListener onHeaderCheckListener;

    public void setHeaderCheckListener(OnHeaderCheckListener listener) {
        onHeaderCheckListener = listener;
    }

    public void processHeaders(ApiResult result) {
        if (onHeaderCheckListener != null) {
            //give our listener a chance to look at the headers that we got back...
            onHeaderCheckListener.onHeaderCheck(result, apiUrl);
        }
    }
}
