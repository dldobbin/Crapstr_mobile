package dics.crapstr;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniel on 11/3/2016.
 * Handles all map operations
 */

public class MapHandler implements GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, PlaceSelectionListener {



    private GoogleMap mMap;
    private LatLng lastLoadedLatLng;
    private ReviewsAdapter adapter;
    private View bottomSheet;
    private GeoDataClient geoDataClient;

    MapHandler(GoogleMap map, ReviewsAdapter adapter, View bottomSheet, GeoDataClient geoDataClient) {
        this.mMap = map;
        this.adapter = adapter;
        this.bottomSheet = bottomSheet;
        this.geoDataClient = geoDataClient;

        //Make clicks to bottom sheet do nothing so they don't propagate to the search bar
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ((ListView)bottomSheet.findViewById(R.id.list)).setAdapter(this.adapter);
    }

    void markLocations(LatLng target) {
        String URL = Utility.baseURL + "/location?lat=" + target.latitude + "&lon=" + target.longitude;
        new JSONHandler(new JSONHandler.Callback() {
            @Override
            public void call(Object o) {
                ArrayList<Location> locations = Location.fromJson((JSONArray)o);
                for (final Location location : locations) {
                    final LatLng position = new LatLng(location.getLat(), location.getLon());
                    final Outgoing outgoing = new Outgoing("/reviews", location.getPlaceId(), position);
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(Utility.getInstance().getMarkerIcon(location.getAvg()))).setTag(outgoing);
                }
                lastLoadedLatLng = mMap.getCameraPosition().target;
            }
        }).execute(URL, "GET");
    }

    private void showReviews(final Outgoing outgoing) {
        showReviews(outgoing.getPlaceId());
        ButtonWrapper buttonWrapper = bottomSheet.findViewById(R.id.add_button);
        buttonWrapper.setOutgoing(outgoing);
    }

    void showReviews(final String placeId) {
        String URL = Utility.baseURL + "/reviews/" + placeId;
        new JSONHandler(new JSONHandler.Callback() {
            @Override
            public void call(Object o) {
                final Reviews reviews = new Reviews((JSONObject)o);
                geoDataClient.getPlaceById(reviews.getPlaceId()).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
                    @Override
                    public void onSuccess(@NonNull PlaceBufferResponse places) {
                        ((TextView)bottomSheet.findViewById(R.id.place_name)).setText(places.get(0).getName());
                        int padding = Utility.getInstance().getPadding(reviews.getAverage());
                        ImageView avgRatingView = bottomSheet.findViewById(R.id.rating);
                        avgRatingView.setPadding(padding, 0, 0, 0);
                        avgRatingView.setColorFilter(Utility.getInstance().reviewToColor(reviews.getAverage()), PorterDuff.Mode.MULTIPLY);
                        adapter.clear();
                        adapter.addAll(reviews.getReviews());
                        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
            }
        }).execute(URL, "GET");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showReviews((Outgoing)marker.getTag());
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
        }
    }

    @Override
    public void onPlaceSelected(final Place place) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
        String URL = Utility.baseURL + "/reviews/" + place.getId();
        new JSONHandler(new JSONHandler.Callback() {
            @Override
            public void call(Object o) {
                Reviews reviews = new Reviews((JSONObject)o);
                if (reviews.getReviews().size() == 0) {
                    Outgoing outgoing = new Outgoing("/location", place.getId(), place.getLatLng());
                    mMap.addMarker(new MarkerOptions()
                            .position(place.getLatLng())
                            .icon(Utility.getInstance().getMarkerIcon(0))).setTag(outgoing);
                }
            }
        }).execute(URL, "GET");
        /**/
    }

    @Override
    public void onError(Status status) {
        // TODO
        Log.i(MapsActivity.LOG_TAG, "An error occurred: " + status);
    }
}
