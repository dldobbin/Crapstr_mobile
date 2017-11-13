package dics.crapstr;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daniel on 11/3/2016.
 */

public class MapHandler implements GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private static final String baseURL = "http://crapstr.herokuapp.com";

    private GoogleMap mMap;
    private LatLng lastLoadedLatLng;

    // Make sure the default constructor is never used
    private MapHandler() {}

    MapHandler(GoogleMap map) {
        this.mMap = map;
    }

    private void markLocations(LatLng target) {
        String URL = baseURL + "/location?lat=" + target.latitude + "&lon=" + target.longitude;
        String method = "locations";
        new JSONHandler(this).execute(URL,method);
    }

    void markLocations(JSONArray locations) throws JSONException {
        for (int i=0; i<locations.length(); i++) {
            JSONObject location = locations.getJSONObject(i);
            LatLng position = new LatLng(location.getDouble("lat"), location.getDouble("lon"));
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.graylet))).setTag(location.getString("placeId"));
        }
        lastLoadedLatLng = mMap.getCameraPosition().target;
    }

    private void showReviews(String placeId) {
        String URL = baseURL + "/reviews" + placeId;
        String method = "reviews";
        new JSONHandler(this).execute(URL,method);
    }

    void showReviews(JSONObject reviews) throws JSONException {
        double avg = reviews.getDouble("avg");
        Log.d(MapsActivity.LOG_TAG, String.valueOf(avg));
        JSONArray revs = reviews.getJSONArray("reviews");
        for (int i=0; i<revs.length(); i++) {
            String review = revs.getJSONObject(i).getString("description");
            int rating = revs.getJSONObject(i).getInt("rating");
            Log.d(MapsActivity.LOG_TAG, review + " " + rating);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showReviews((String)marker.getTag());
        return false;
    }

    @Override
    public void onCameraIdle() {
        if (lastLoadedLatLng == null) {
            markLocations(mMap.getCameraPosition().target);
        } else {
            float[] distance = new float[1];
            Location.distanceBetween(lastLoadedLatLng.latitude, lastLoadedLatLng.longitude, mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, distance);
            if (distance[0] > 5000) {
                mMap.clear();
                markLocations(mMap.getCameraPosition().target);
            }
            Log.d(MapsActivity.LOG_TAG, "onCameraIdle");
            Log.d(MapsActivity.LOG_TAG, String.valueOf(distance[0]));
        }
    }
}
