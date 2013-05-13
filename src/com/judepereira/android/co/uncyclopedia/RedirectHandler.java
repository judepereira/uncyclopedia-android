/*
 * This work is licensed under the
 * Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * 
 * To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/3.0/.
 */
package com.judepereira.android.co.uncyclopedia;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

/**
 *
 * @author Jude Pereira
 */
public class RedirectHandler extends DefaultRedirectHandler {

    private URI lastRedirectedUri;

    public URI getLastRedirectedUri() {
        return lastRedirectedUri;
    }

    public void setLastRedirectedUri(URI lastRedirectedUri) {
        this.lastRedirectedUri = lastRedirectedUri;
    }

    @Override
    public boolean isRedirectRequested(HttpResponse response,
            HttpContext context) {
        return super.isRedirectRequested(response, context);
    }

    @Override
    public URI getLocationURI(HttpResponse response, HttpContext context)
            throws ProtocolException {
        lastRedirectedUri = super.getLocationURI(response, context);
        return lastRedirectedUri;
    }
}
