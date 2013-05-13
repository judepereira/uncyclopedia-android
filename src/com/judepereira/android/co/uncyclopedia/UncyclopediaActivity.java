package com.judepereira.android.co.uncyclopedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.judepereira.android.co.uncyclopedia.db.Dao;
import com.judepereira.android.co.uncyclopedia.dto.ArticleHistory;
import com.judepereira.android.co.uncyclopedia.dto.Utility;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 *
 * @author Jude Pereira
 */
public class UncyclopediaActivity extends FragmentActivity {

    private WebView wikiView;
    private EditText searchText;
    private UncyclopediaWebViewClient client;
    private Stack<ArticleHistory> history;
    private Utility c = new Utility();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        searchText = (EditText) findViewById(R.id.search_edittext);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView tv, int i, KeyEvent ke) {
                if ((ke != null) && ke.getAction() == KeyEvent.ACTION_DOWN) {
                    search(null);
                }
                if (ke == null) {
                    search(null);
                }
                return true;
            }
        });
        wikiView = (WebView) findViewById(R.id.webview);
        client = new UncyclopediaWebViewClient(this, wikiView);
        wikiView.setWebViewClient(client);
        if (savedInstanceState == null) {
            history = new Stack<ArticleHistory>();
            client.setHistory(history);
            client.setUniqueId(Math.random());
            client.setFeatured(true);
            wikiView.loadUrl(c.RANDOM_FEATURED_URL);
        }
    }

    public void search(View view) {
        String text = searchText.getText().toString();
        if (text.equals("")) {
            return;
        }
        String searchUrl;
        searchUrl = c.SEARCH_BASE_URL
                + text;
        client.setUniqueId(Math.random());
        wikiView.loadUrl(searchUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            client.setUniqueId(Math.random());
            ArticleHistory back;
            try {
                history.pop();
                back = history.peek();
            } catch (EmptyStackException ex) {
                finish();
                return true;
            }
            client.setGoingBack(true);
            wikiView.loadDataWithBaseURL(back.getUrl(),
                    back.getContentHtml(), null, "utf-8", back.getUrl());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("history", history);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        history = (Stack<ArticleHistory>) savedInstanceState.getSerializable("history");
        client.setHistory(history);
        client.setUniqueId(Math.random());
        client.setFeatured(false);
        try {
            client.setRestoring(true);
            wikiView.loadUrl(history.peek().getUrl());
        } catch (EmptyStackException ex) {
            history = new Stack<ArticleHistory>();
            client.setHistory(history);
            client.setUniqueId(Math.random());
            client.setFeatured(true);
            wikiView.loadUrl(c.RANDOM_FEATURED_URL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey,\n"
                        + "Check out this article on Uncyclopedia: \n\n"
                        + wikiView.getTitle() + "\n"
                        + history.peek().getUrl()
                        + "\n\nSent via the Uncyclopedia App for Android";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, wikiView.getTitle());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.save:
                ArticleHistory article = history.peek();
                if (bookmarkArticle(wikiView.getTitle(), article.getUrl())) {
                    Toast.makeText(this, "Successfully bookmarked",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "An error occured. please try again",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.saved:
                FragmentManager fragmentManager = getSupportFragmentManager();
                BookmarksDialog dFrag = new BookmarksDialog();
                dFrag.setContext(this);
                dFrag.setWikiView(wikiView);
                dFrag.setClient(client);
                dFrag.show(fragmentManager, "Bookmarks");
                break;
        }
        return true;
    }

    private boolean bookmarkArticle(String title, String url) {
        String mainTitle;
        mainTitle = title.replace(" - Uncyclopedia, the content-free encyclopedia", "");
        Dao dao = new Dao(this);
        if (dao.insertBookmark(mainTitle, c.getGetUrl(url))) {
            dao.close();
            return true;
        } else {
            dao.close();
            return false;
        }
    }
}
