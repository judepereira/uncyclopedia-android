/*
 * This work is licensed under the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * 
 * To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.judepereira.android.co.uncyclopedia.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * @author Jude Pereira
 */
public class ArticleHistory implements Parcelable {

    private String url, contentHtml;

    public ArticleHistory(String url, String contentHtml) {
        this.url = url;
        this.contentHtml = contentHtml;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getUrl());
        out.writeString(getContentHtml());
    }
    public static final Parcelable.Creator<ArticleHistory> CREATOR = new Parcelable.Creator<ArticleHistory>() {
        public ArticleHistory createFromParcel(Parcel in) {
            return new ArticleHistory(in);
        }

        public ArticleHistory[] newArray(int size) {
            return new ArticleHistory[size];
        }
    };

    private ArticleHistory(Parcel in) {
        setUrl(in.readString());
        setContentHtml(in.readString());
    }
}
