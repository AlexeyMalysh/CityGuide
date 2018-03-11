package cityguide.garren.com.utils;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gsteigers on 3/3/18.
 */

public class DataParser {
    public Pair<String, List<HashMap<String, String>>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        String nextPage = null;

        try {
            Log.d("GARREN", "parse");
            jsonObject = new JSONObject(jsonData);
            try {
                Log.d("GARREN", jsonObject.toString());
                nextPage = (String) jsonObject.get("next_page_token");
            } catch( Exception e) {
                Log.d("Places", "failed to get nextPageToken error");
            }
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }

        if(jsonArray == null) {
            jsonArray = new JSONArray();
        }
        return getPlaces(jsonArray, nextPage);
    }

    private Pair<String, List<HashMap<String, String>>> getPlaces(JSONArray jsonArray, String nextPage) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("Places", "Adding places");

            } catch (JSONException e) {
                Log.d("Places", "Error in Adding places");
                e.printStackTrace();
            }
        }
        return new Pair<>(nextPage, placesList);
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String rating = "-1";
        String latitude;
        String longitude;

        Log.d("getPlace", "Entered");

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getString("rating");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("rating", rating);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}

