package dics.crapstr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private double reviewToColor(double review) {
        if (review < 3) {
            return (Color.YELLOW-Color.RED)/2*review + (3*Color.RED-Color.YELLOW)/2;
        } else {
            return (Color.GREEN-Color.YELLOW)/2*review + (-3*Color.GREEN+5*Color.YELLOW)/2;
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(double avg) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.toilet);
        icon.setColorFilter((int)reviewToColor(avg), PorterDuff.Mode.MULTIPLY);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());
        icon.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void markLocations(LatLng target) {
        String URL = baseURL + "/location?lat=" + target.latitude + "&lon=" + target.longitude;
        new JSONHandler(new Callback() {
            @Override
            public void call(Object o) {
                JSONArray locations = (JSONArray)o;
                try {
                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject location = locations.getJSONObject(i);
                        LatLng position = new LatLng(location.getDouble("lat"), location.getDouble("lon"));
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(getMarkerIconFromDrawable(location.getDouble("avg")))).setTag(location.getString("placeId"));
                    }
                    lastLoadedLatLng = mMap.getCameraPosition().target;
                } catch (JSONException je) {
                    //TODO
                }
            }
        }).execute(URL);
    }

    private void showReviews(final Marker marker) {
        String URL = baseURL + "/reviews/" + marker.getTag();
        new JSONHandler(new Callback() {
            @Override
            public void call(Object o) {
                JSONObject reviews = (JSONObject)o;
                try {
                    double avg = reviews.getDouble("avg");
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.valueOf(avg)).append("\n");
                    JSONArray revs = reviews.getJSONArray("reviews");
                    for (int i = 0; i < revs.length(); i++) {
                        String review = revs.getJSONObject(i).getString("description");
                        int rating = revs.getJSONObject(i).getInt("rating");
                        sb.append(review).append(" ").append(rating).append("\n");
                    }
                    marker.setSnippet(sb.toString());
                    marker.setTitle("TEST");
                    marker.showInfoWindow();
                } catch (JSONException je) {
                    //TODO
                }
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
