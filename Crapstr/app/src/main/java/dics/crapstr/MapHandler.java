package dics.crapstr;

import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
    private LatLng lastLoadedLatLng;
    private ReviewsAdapter adapter;
    private BottomSheetBehavior mBottomSheetBehavior;

    MapHandler(GoogleMap map, ReviewsAdapter adapter, View bottomSheet) {
        this.mMap = map;
        this.adapter = adapter;
        this.mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        this.mBottomSheetBehavior.setHideable(true);
        this.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ((ListView)bottomSheet.findViewById(R.id.list)).setAdapter(this.adapter);
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
                            .icon(Utility.getInstance().getMarkerIcon(location.getAvg()))).setTag(location.getPlaceId());
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
                adapter.clear();
                adapter.addAll(reviews.getReviews());
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
