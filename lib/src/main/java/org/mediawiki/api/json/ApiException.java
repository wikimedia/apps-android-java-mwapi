package org.mediawiki.api.json;

/**
 * Wrapper exception thrown whenever there's an unrecoverable error from the Api.
 */
public class ApiException extends Exception {
    private final String code;
    private final String info;

    public ApiException(final Exception cause) {
        super(cause);
        code = null;
        info = null;
    }

    public ApiException(String code, String info) {
        super();
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        String str = "";
        if (code != null) {
            str += code;
        }
        if (info != null) {
            if (str.length() > 0) {
                str += ": ";
            }
            str += info;
        }
        return str.length() > 0 ? str : super.toString();
    }
}
