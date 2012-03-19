package com.tapad.adserving;

import com.tapad.tracking.Logging;
import com.tapad.util.IoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the resource interface to the ad service.
 * Fetches ad markup using a HTTP Client.
 * <p/>
 * <p/>
 * Single-threaded access only.
 */
class AdResource {
    private static String TAG = "AdResource";
    
    private static final String RESOURCE_URL = "http://swappit.tapad.com/swappit/ad";

    // Just a sanity check so we don't waste too much bandwidth in case of some
    // markup error / glitch.
    private static final int MAX_CONTENT_LENGTH = 1024 * 8;

    public static final String PARAM_DEVICE_ID = "uid";
    public static final String PARAM_AD_SIZE = "adSize";
    public static final String PARAM_CONTEXT_TYPE = "contextType";
    public static final String PARAM_PUBLISHER_ID = "pub";
    public static final String PARAM_PROPERTY_ID = "prop";
    public static final String PARAM_PLACEMENT_ID = "placementId";
    public static final String PARAM_WRAP_HTML = "wrapHtml";

    private String deviceId;
    private String propertyId;
    private String publisherId;
    private DefaultHttpClient client;

    AdResource(String deviceId, String publisherId, String propertyId, String userAgent) {
        this.deviceId = deviceId;
        this.publisherId = publisherId;
        this.propertyId = propertyId;
        this.client = com.tapad.util.HttpClientUtil.createClient(userAgent);
    }

    protected AdResponse get(AdRequest req) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(PARAM_DEVICE_ID, deviceId));
        params.add(new BasicNameValuePair(PARAM_PUBLISHER_ID, publisherId));
        params.add(new BasicNameValuePair(PARAM_AD_SIZE, req.getSize().asString()));
        if (propertyId != null) params.add(new BasicNameValuePair(PARAM_PROPERTY_ID, propertyId));
        params.add(new BasicNameValuePair(PARAM_PLACEMENT_ID, req.getPlacementId()));
        params.add(new BasicNameValuePair(PARAM_WRAP_HTML, req.isWrapWithHtml() ? "true" : "false"));
        params.add(new BasicNameValuePair(PARAM_CONTEXT_TYPE, "app"));


        String query = URLEncodedUtils.format(params, "UTF-8");
        String uri = RESOURCE_URL + "?" + query;
        Logging.info(TAG, "Final URL is " + uri);

        try {
            HttpResponse response = client.execute(new HttpGet(uri));

            HttpEntity entity = response.getEntity();
            switch (response.getStatusLine().getStatusCode()) {
                case 204:
                    return AdResponse.noAdAvailable();
                case 200:
                    long contentLength = entity.getContentLength() == -1L ? 2048 : entity.getContentLength();
                    if (contentLength > MAX_CONTENT_LENGTH) {
                        entity.consumeContent();
                        throw new IOException("Content length of " + contentLength + " bytes is unacceptably long. Rejecting it.");
                    } else if (contentLength == 0) {
                        return AdResponse.noAdAvailable();
                    } else {
                        String markup = IoUtil.inputStreamToString(entity.getContent(), (int) contentLength);
                        return AdResponse.success(markup);
                    }
                default:
                    throw new IOException("Got HTTP response error: " + response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            Logging.error(TAG, "IO Error: " + e.getMessage());
            return AdResponse.error(e.getMessage());
        }
    }


}
