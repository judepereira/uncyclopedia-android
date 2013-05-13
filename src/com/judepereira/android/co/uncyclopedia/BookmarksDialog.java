/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judepereira.android.co.uncyclopedia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;
import com.judepereira.android.co.uncyclopedia.db.Dao;
import com.judepereira.android.co.uncyclopedia.dto.Bookmark;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Jude Pereira
 */
public class BookmarksDialog extends DialogFragment {

    private Context context;
    private ArrayList<Bookmark> bookmarkList;
    private UncyclopediaWebViewClient client;
    private WebView wikiView;

    public UncyclopediaWebViewClient getClient() {
        return client;
    }

    public void setClient(UncyclopediaWebViewClient client) {
        this.client = client;
    }

    public WebView getWikiView() {
        return wikiView;
    }

    public void setWikiView(WebView wikiView) {
        this.wikiView = wikiView;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dao dao = new Dao(context);
        bookmarkList = dao.getBookmarkList();
        dao.close();
        ArrayList<String> titleList = new ArrayList<String>();
        Collections.reverse(bookmarkList);
        for (Bookmark b : bookmarkList) {
            titleList.add(b.getTitle());
        }
        String[] titleArray = titleList.toArray(new String[bookmarkList.size()]);

        builder.setTitle("Bookmarks")
                .setItems(titleArray,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int index) {
                        Bookmark b = bookmarkList.get(index);
                        client.setUniqueId(Math.random());
                        wikiView.loadUrl(b.getUrl());
                    }
                });
        Dialog d = builder.create();
        d.setCanceledOnTouchOutside(false);
        return d;
    }
}
