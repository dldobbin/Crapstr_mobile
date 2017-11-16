package dics.crapstr;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniel on 11/3/2016.
 * Handles all map operations
 */

public class MapHandler implements GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private static final String baseURL = "http://crapstr-backend.herokuapp.com";

    private GoogleMap mMap;
    private Context context;
    private LatLng lastLoadedLatLng;

    MapHandler(Context context, GoogleMap map) {
        this.mMap = map;
        this.context = context;
    }

    public abstract class Callback {
        public abstract void call(Object o);
    }

    private void markLocations(LatLng target) {
        String URL = baseURL + "/location?lat=" + target.latitude + "&lon=" + target.longitude;
        new JSONHandler(new Callback() {
            @Override
            public void call(Object o) {
                ArrayList<Location> locations = Location.fromJson((JSONArray)o);
                for (Location location : locations) {
                    LatLng position = new LatLng(location.getLat(), location.getLon());
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(location.getMarkerIcon(context))).setTag(location.getPlaceId());
                }
                lastLoadedLatLng = mMap.getCameraPosition().target;
            }
        }).execute(URL);
    }

    private void showReviews(final Marker marker) {
        String URL = baseURL + "/reviews/" + marker.getTag();
        new JSONHandler(new Callback() {
            @Override
            public void call(Object o) {
                Reviews reviews = new Reviews((JSONObject)o);
                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(reviews.getAverage())).append("\n");
                for (Review review : reviews.getReviews()) {
                    sb.append(review.getDescription()).append(" ").append(review.getRating()).append("\n");
                }
                marker.setSnippet(sb.toString());
                marker.setTitle("TEST");
                marker.showInfoWindow();
            }
        }).execute(URL);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showReviews(marker);
        return false;
    }

    @Override
    public void onCameraIdle() {
        if (lastLoadedLatLng == null) {
            markLocations(mMap.getCameraPosition().target);
        } else {
            float[] distance = new float[1];
            android.location.Location.distanceBetween(lastLoadedLatLng.latitude, lastLoadedLatLng.longitude, mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, distance);
            if (distance[0] > 5000) {
                mMap.clear();
                markLocations(mMap.getCameraPosition().target);
            }
            Log.d(MapsActivity.LOG_TAG, "onCameraIdle");
            Log.d(MapsActivity.LOG_TAG, String.valueOf(distance[0]));
        }
    }
}
