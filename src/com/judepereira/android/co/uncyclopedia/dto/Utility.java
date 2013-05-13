/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judepereira.android.co.uncyclopedia.dto;

/**
 *
 * @author Jude Pereira
 */
public class Utility {

    public final String RANDOM_FEATURED_URL = "http://en.uncyclopedia.co/wiki/Main_Page";
    public final String SEARCH_BASE_URL = "http://en.uncyclopedia.co/w/index.php?title=Special:Search&mobileaction=toggle_view_mobile&search=";
    public final String WIKI_DOMAIN = "http://en.uncyclopedia.co";

    public final String getGetUrl(String url) {
        // if it's a special page, such as search or random in category featured, go directly
        if (url.contains("Special")) {
            return url;
        } else if (!url.contains("mobileaction=toggle_view_mobile")) {
            String[] parts = url.split("/");
            String fUrl = "http://en.uncyclopedia.co/w/index.php?title=" + parts[parts.length - 1] + "&mobileaction=toggle_view_mobile";
            return fUrl;
        } else {
            return url;
        }
    }
}
