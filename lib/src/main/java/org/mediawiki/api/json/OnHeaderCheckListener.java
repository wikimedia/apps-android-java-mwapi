package org.mediawiki.api.json;

import java.net.URL;

public interface OnHeaderCheckListener {
    void onHeaderCheck(ApiResult result, URL apiUrl);
}
