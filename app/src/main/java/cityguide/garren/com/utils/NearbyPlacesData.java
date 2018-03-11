package cityguide.garren.com.utils;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cityguide.garren.com.activities.GuideActivity;
import cityguide.garren.com.models.Result;

/**
 * Created by gsteigers on 3/3/18.
 */

public class NearbyPlacesData extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    GuideActivity mContext;
    String url;
    int type;
    Location mLastKnownLocation;
    Boolean isDone;

    @Override
    protected String doInBackground(Object... params) {
        while(!isCancelled()) {
            isDone = false;
            try {
                Log.d("GetNearbyPlacesData", "doInBackground entered");
                mContext = (GuideActivity) params[0];
                mContext.setProgressVisibility(true);
                url = (String) params[1];
                type = (int) params[2];

                Log.d("GARREN", "url: " + url);
                Log.d("GARREN", "type: " + type);
                mLastKnownLocation = (Location) params[3];
                DownloadUrl downloadUrl = new DownloadUrl();
                googlePlacesData = downloadUrl.readUrl(url);
                Log.d("GooglePlacesReadTask", "doInBackground Exit");
            } catch (Exception e) {
                Log.d("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        mContext.setProgressVisibility(true);
        Pair<String, List<HashMap<String, String>>> nearbyPlacesPair = null;
        DataParser dataParser = new DataParser();
        if(result != null) {
            nearbyPlacesPair = dataParser.parse(result);
        }



        String nextToken = null;
        List<HashMap<String, String>> nearbyPlacesList = null;
        if(nearbyPlacesPair != null) {
            nextToken = nearbyPlacesPair.first;
            nearbyPlacesList = nearbyPlacesPair.second;
        }

        if (nextToken != null) {
            Log.d("GARREN", "Call Next Token");
            ArrayList<Result> resultsList = setupNearbyPlaces(nearbyPlacesList);
            mContext.setCompiledResults(resultsList);
            mContext.getNextNearbyPlaces(type, nextToken);
        } else {
            Log.d("GARREN", "No Next Token - Finalize");
            ArrayList<Result> resultsList = setupNearbyPlaces(nearbyPlacesList);
            mContext.setCompiledResults(resultsList);
            ArrayList<Result> finalizedList = mContext.getCompiledResults();
            ShowNearbyPlaces(finalizedList);
        }

    }

    @Override
    protected void onCancelled() {
        this.cancel(true);
        super.onCancelled();
    }

    private ArrayList<Result> setupNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        Log.d("GARREN", "SetupNearbyPlaces");
        int size = 0;

        Log.d("Garren", "isCancelled = " + this.isCancelled());

        if(nearbyPlacesList != null) {
            size = nearbyPlacesList.size();
        }
        Result[] results = new Result[size];
        for (int i = 0; i < size; i++) {
            Log.d("onPostExecute","Entered into showing locations");

            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            int rating = (int) Double.parseDouble(googlePlace.get("rating"));
            String placeName = googlePlace.get("place_name");
            LatLng latLng = new LatLng(lat, lng);

            Location placeLocation = new Location(placeName);
            placeLocation.setLatitude(latLng.latitude);
            placeLocation.setLongitude(latLng.longitude);

            float distance = mLastKnownLocation.distanceTo(placeLocation);
            double distInMiles = (distance * 0.00062137);

            results[i] = new Result(placeName, type, distInMiles, rating, placeLocation);
        }

        ArrayList<Result> resultsList = new ArrayList<>(Arrays.asList(results));

        Collections.sort(resultsList, new Comparator<Result>(){
            public int compare(Result obj1, Result obj2) {
                return Double.valueOf(obj1.getDistanceValue()).compareTo(obj2.getDistanceValue());
            }
        });

        return resultsList;
    }

    private void ShowNearbyPlaces(ArrayList<Result> resultsList) {
        ArrayList<Result> results = filterDownList(resultsList);


        Collections.sort(results, new Comparator<Result>() {
            public int compare(Result obj1, Result obj2) {
                return Double.valueOf(obj1.getDistanceValue()).compareTo(obj2.getDistanceValue());
            }
        });

        if(results.size() > 0) {
            mContext.setupResults(results);
        }
    }

    private ArrayList<Result> filterDownList (ArrayList<Result> resultsList) {
        Iterator<Result> resultIterator = resultsList.iterator();
        while (resultIterator.hasNext()) {
            Result r = resultIterator.next();
            if (r.getType() != type) {
                resultIterator.remove();
            }
        }

        return resultsList;
    }
}
