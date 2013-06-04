package com.tapad.tracking;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import com.tapad.util.Logging;

public class DeviceInfo {

    /**
     * Gets the device's browser user agent.
     */
    public static String getUserAgent(Context context) {
        try {
            StringBuffer buffer = new StringBuffer();
            // Add version
            final String version = Build.VERSION.RELEASE;
            if (version.length() > 0) {
                buffer.append(version);
            } else {
                // default to "1.0"
                buffer.append("1.0");
            }
            buffer.append("; ");
            // default to "en"
            buffer.append("en");
            buffer.append(";");
            // add the model for the release build
            if ("REL".equals(Build.VERSION.CODENAME)) {
                final String model = Build.MODEL;
                if (model.length() > 0) {
                    buffer.append(" ");
                    buffer.append(model);
                }
            }
            final String id = Build.ID;
            if (id.length() > 0) {
                buffer.append(" Build/");
                buffer.append(id);
            }
            int webUserAgentTargetContentResourceId = Resources.getSystem().getIdentifier("web_user_agent_target_content", "string", "android");
            String mobile = "";
            if (webUserAgentTargetContentResourceId > 0) {
                mobile = context.getResources().getText(webUserAgentTargetContentResourceId).toString();
            }
            int webUserAgentResourceId = Resources.getSystem().getIdentifier("web_user_agent", "string", "android");
            final String base = context.getResources().getText(webUserAgentResourceId).toString();
            final String userAgent = String.format(base, buffer, mobile);

            Logging.info("DeviceInfo", "UserAgent successfully constructed: " + userAgent);
            return userAgent;
        }
        catch (Throwable t) {
            Logging.error("DeviceInfo", "Exception caught while generating user agent: " + t.getMessage());
            return "Android";
        }
    }
}
