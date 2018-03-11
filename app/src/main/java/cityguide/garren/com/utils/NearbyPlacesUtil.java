package cityguide.garren.com.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import cityguide.garren.com.R;

/**
 * Created by gsteigers on 3/4/18.
 */

public class NearbyPlacesUtil {
    public String TAG = "NearbyPlacesUtil";

    private String TYPE_BAR = "bar";
    private String TYPE_BISTRO = "restaurant";
    private String TYPE_CAFE = "cafe";

    public String getNearbyPlacesRequestUrl(Location location, String nearbyPlaceType, int radius, String pageToken, Context context) {
        StringBuilder googlePlacesUrl = new StringBuilder(context.getString(R.string.google_web_api_base_url));
        googlePlacesUrl.append("location=" + location.getLatitude() + "," + location.getLongitude());
        googlePlacesUrl.append("&radius=" + radius * 1000);
        googlePlacesUrl.append("&type=" + nearbyPlaceType);
        if(pageToken != null) {
            googlePlacesUrl.append("&pagetoken=" + pageToken);
        }
        googlePlacesUrl.append("&key=" + context.getString(R.string.google_web_api_key));
        Log.d(TAG, "Google API url: " + googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public String getNearbyTypeAsString(int type) {
        if(type == 0) {
            return TYPE_BAR;
        } else if (type == 1) {
            return TYPE_BISTRO;
        } else {
            return TYPE_CAFE;
        }
    }

    public int getNearbyTypeAsInt(String type) {
        if(type == TYPE_BAR) {
           return  0;
        } else if (type == TYPE_BISTRO) {
            return 1;
        } else {
            return 2;
        }
    }
}
