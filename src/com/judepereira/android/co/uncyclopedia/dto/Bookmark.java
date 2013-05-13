/*
 * This work is licensed under the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * 
 * To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.judepereira.android.co.uncyclopedia.dto;

/**
 *
 * @author Jude Pereira
 */
public class Bookmark {

    private String title, url;

    public Bookmark(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
