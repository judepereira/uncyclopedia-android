/*
 * This work is licensed under the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * 
 * To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.judepereira.android.co.uncyclopedia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.judepereira.android.co.uncyclopedia.dto.ArticleHistory;
import com.judepereira.android.co.uncyclopedia.dto.Utility;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.EmptyStackException;
import java.util.Stack;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Jude Pereira
 */
public class UncyclopediaWebViewClient extends WebViewClient {

    private final Activity act;
    private final WebView wikiView;
    private double uniqueId;
    private double lastUniqueId = 0;
    private boolean featured = false;
    private Stack<ArticleHistory> history;
    private boolean goingBack = false;
    private boolean restoring = false;
    private String errorReport;
    private Utility c = new Utility();

    public boolean isRestoring() {
        return restoring;
    }

    public void setRestoring(boolean restoring) {
        this.restoring = restoring;
    }

    public void setHistory(Stack<ArticleHistory> history) {
        this.history = history;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public double getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(double uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UncyclopediaWebViewClient(Activity act, WebView wikiaView) {
        this.act = act;
        this.wikiView = wikiaView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains("wiki")) {
            lastUniqueId = 0;
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        act.startActivity(intent);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (lastUniqueId != uniqueId) {
            lastUniqueId = new Double(uniqueId);
            wikiView.loadDataWithBaseURL("", "", null, "utf-8", "");
            new UpdateContentAsyncTask(wikiView, url, act).execute("");
        }
    }

    private String getWikiaContentOnly(String url) {
        String contentHtml = "<h3>An error has occurred. Please restart the app after sending the error report.</h3><br>";
        String getUrl = url;
        if (url.startsWith("/wiki")) {
            getUrl = c.WIKI_DOMAIN + url;
        }

        // check if the head of the queue is what we want
        try {
            ArticleHistory headArticle = history.peek();
            String headArticleUrl = headArticle.getUrl();
            if (headArticleUrl.equals(url)) {
                cleanupAndAddIfRequired(url, contentHtml);
                return headArticle.getContentHtml();
            }
        } catch (EmptyStackException ex) {
            // if we get here, it doesn't really matter
        }

        // dirty hack
        String replacement = "<style type=\"text/css\">\n"
                + "#mw-mf-language-section, #footer, #mw-mf-header, .header, .escapeOverlay {\n"
                + "display: none !important;\n"
                + "}\n"
                + "</style>\n"
                + "</head>\n";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet;
            httpGet = new HttpGet(c.getGetUrl(getUrl));
            WebSettings settings = wikiView.getSettings();
            RedirectHandler handler = new RedirectHandler();
            httpClient.setRedirectHandler(handler);
            httpGet.setHeader("User-Agent", settings.getUserAgentString());
            httpGet.setHeader("Cookie", "mf_mobileFormat=true");
            httpGet.setHeader("Accept-Encoding", "gzip");
            HttpResponse response = httpClient.execute(httpGet);
            // this successfully gets the last redirected URL :)
            if (handler.getLastRedirectedUri() != null) {
                url = handler.getLastRedirectedUri().toString();
            }
            HttpEntity entity = response.getEntity();
//            if (url.endsWith(".ico")
//                    || url.endsWith(".svg")
//                    || url.endsWith(".SVG")
//                    || url.endsWith(".png")
//                    || url.endsWith(".PNG")
//                    || url.endsWith(".jpg")
//                    || url.endsWith(".JPG")
//                    || url.endsWith(".gif")
//                    || url.endsWith(".GIF")
//                    || url.endsWith(".jpeg")
//                    || url.endsWith(".JPEG")) {
//                contentHtml = "<html><body><img src=\"" + url + "\" /></body></html>";
//            } else {
                contentHtml = EntityUtils.toString(new GzipDecompressingEntity(entity));
//            }
            // wicked, dirty filters
            contentHtml = contentHtml.replace("</head>", replacement);
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            errorReport = "While viewing " + url + " with GET URL as " + c.getGetUrl(url) + "\n\n";
            errorReport += "The following error occurred: \n\n" + sw.toString() + "\n\n";
            // send the error report
            String uriText =
                    "mailto:judepereira@7terminals.com"
                    + "?subject=" + URLEncoder.encode("Uncyclopedia App(Android) error report")
                    + "&body=" + URLEncoder.encode(errorReport);
            Uri uri = Uri.parse(uriText);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setData(uri);
            act.startActivity(Intent.createChooser(sendIntent, "Send error report"));
        }
        cleanupAndAddIfRequired(url, contentHtml);
        return contentHtml;
    }

    private void cleanupAndAddIfRequired(String url, String contentHtml) {
        if (featured) {
            featured = false;
        }
        if (goingBack) {
            goingBack = false;
        } else {
            if (!restoring) {
                history.push(new ArticleHistory(url, contentHtml));
            } else {
                restoring = false;
            }
        }
    }

    public void setGoingBack(boolean b) {
        this.goingBack = b;
    }

    private class UpdateContentAsyncTask extends AsyncTask<String, Void, String> {

        private final Activity act;
        private final WebView wikiView;
        private final String url;
        private ProgressDialog progress;

        private UpdateContentAsyncTask(WebView wikiView, String url, Activity act) {
            super();
            this.act = act;
            this.url = url;
            this.wikiView = wikiView;
        }

        @Override
        protected String doInBackground(String... params) {
            String contentHtml = getWikiaContentOnly(url);
            wikiView.loadDataWithBaseURL(url, contentHtml, null, "utf-8", url);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
        }

        @Override
        protected void onPreExecute() {
            String message;
            if (featured) {
                message = "Retrieving today's featured story";
            } else {
                message = "Retrieving";
            }
            progress = ProgressDialog.show(act,
                    "",
                    message);
        }
    }
}
