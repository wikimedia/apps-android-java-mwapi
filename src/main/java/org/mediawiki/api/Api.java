package org.mediawiki.api;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Client wrapper for connecting to a MediaWiki installation's API.
 *
 * - It aims to be fully reentrant.
 * - Uses JSON for everything
 */
public class Api {

    /**
     * The exact URL to which API requests are made.
     */
    private URL apiUrl;

    /**
     * Create an Api class with the default endpoint path.
     *
     * Uses https by default (isSecure is true).
     * Default endpoint path is /w/api.php.
     *
     * @param domain Domain name of the server with the wiki to connect to
     */
    public Api(final String domain) {
        this(domain, true);
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
     * @param endpointPath Path to the api.php file. Require preceding slash.
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
            // when an unknown protocal is given. http and https are guaranteed to be present,
            // according to http://docs.oracle.com/javase/6/docs/api/java/net/URL.html#URL(java.lang.String, java.lang.String, int, java.lang.String)
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
        return null;
    }
}

