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
    private HashMap<String,String> customHeaders;

    /**
     * Create an Api class with the default endpoint path.
     *
     * Uses https by default (isSecure is true).
     * Default endpoint path is /w/json.php.
     *
     * @param domain Domain name of the server with the wiki to connect to
     */
    public Api(final String domain) {
        this(domain, true);
    }

    /**
     * Create an Api class with the default endpoint path using custom User-Agent.
     *
     * Uses https by default (isSecure is true).
     * Default endpoint path is /w/json.php.
     *
     * @param domain Domain name of the server with the wiki to connect to
     * @param userAgent Custom User-Agent to simplify identification of consuming application
     */
    public Api(final String domain, final String userAgent) {
        this(domain, true);
        if (userAgent != null) {
            this.customHeaders = new HashMap<String,String>();
            this.customHeaders.put("User-Agent", userAgent);
        }
    }

    /**
     * Create an Api class with the default endpoint path using custom User-Agent
     * and additional headers.
     *
     * Uses https by default (isSecure is true).
     * Default endpoint path is /w/json.php.
     * If User-Agent key is set in customHeaders it will override the userAgent parameter.
     *
     * @param domain Domain name of the server with the wiki to connect to
     * @param userAgent Custom User-Agent to simplify identification of consuming application
     * @param customHeaders Additional custom headers to enrich request
     */
    public Api(final String domain, final String userAgent, HashMap<String,String> customHeaders) {
        this(domain, userAgent);
        if (customHeaders != null) {
            if (this.customHeaders == null) {
                this.customHeaders = customHeaders;
            } else {
                this.customHeaders.putAll(customHeaders);
            }
        }
    }

    /**
     * Create an Api class with the default endpoint path using custom headers.
     *
     * Uses https by default (isSecure is true).
     * Default endpoint path is /w/json.php.
     *
     * @param domain Domain name of the server with the wiki to connect to
     * @param customHeaders Additional custom headers to enrich request
     */
    public Api(final String domain, HashMap<String,String> customHeaders) {
        this(domain, true);
        if (customHeaders != null) {
            this.customHeaders = customHeaders;
        }
    }

    /**
     * Create an Api class with the default endpoint path.
     *
     * @param domain Domain name of the mediawiki installation to connect to
     * @param useSecure true to use https, false to use http
     */
    public Api(final String domain, final boolean useSecure) {
        this(domain, useSecure, "/w/api.php");
    }

    /**
     * Create an Api class with given hostname and endpoint path.
     *
     * @param domain Domain name of the mediawiki installation to connect to
     * @param useSecure true to use https, false to use http
     * @param endpointPath Path to the json.php file. Require preceding slash.
     */
    public Api(final String domain, final boolean useSecure, final String endpointPath) {
        String protocol;
        if (useSecure) {
            protocol = "https";
        } else {
            protocol = "http";
        }

        try {
            apiUrl = new URL(protocol, domain, endpointPath);
        } catch (MalformedURLException e) {
            // This never actually is supposed to happen, since it is thrown only
            // when an unknown protocol is given. http and https are guaranteed to be present,
            // according to http://docs.oracle.com/javase/6/docs/json/java/net/URL.html#URL(java.lang.String, java.lang.String, int, java.lang.String)
            throw new RuntimeException(e);
        }
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
     * Use {@link org.mediawiki.api.json.ApiResult#asArray()} or {@link org.mediawiki.api.json.ApiResult#asObject()} to
     * actually start the network request and get the response.
     *
     * Supports GET and POST only currently, since that is all that the MW API supports.
     *
     * @param method HTTP method to use when performing the request
     * @param requestBuilder The requestBuilder to use to construct the request
     * @return An {@link ApiResult} object which can be used to get the result of this query.
     */
    public ApiResult setupRequest(final int method, final RequestBuilder requestBuilder) {
        HttpRequest request;
        switch(method) {
            case METHOD_GET:
                request = HttpRequest.get(getApiUrl().toString(), encodeParams(requestBuilder.getParams()), false);
                if (this.customHeaders != null) {
                    request = request.headers(customHeaders);
                }
                break;
            case METHOD_POST:
                request = HttpRequest.post(getApiUrl().toString());
                if (this.customHeaders != null) {
                    request = request.headers(customHeaders);
                }
                request.form(requestBuilder.getParams());
                break;
            default:
                throw new IllegalArgumentException("Unknown argument passed for parameter method");
        }
        return new ApiResult(request);
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
}

