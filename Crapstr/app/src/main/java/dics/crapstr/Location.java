package dics.crapstr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniel on 11/16/2017.
 *
 */

class Location {
    private double avg;
    private double lat;
    private double lon;
    private String placeId;

    /*public Location(double avg, double lat, double lon, String placeId) {
        this.avg = avg;
        this.lat = lat;
        this.lon = lon;
        this.placeId = placeId;
    }*/

    //public double getAvg() { return avg; }
    double getLat() { return lat; }
    double getLon() { return lon; }
    String getPlaceId() { return placeId; }

    private Location(JSONObject object) {
        try {
            this.avg = object.getDouble("avg");
            this.lat = object.getDouble("lat");
            this.lon = object.getDouble("lon");
            this.placeId = object.getString("placeId");
        } catch (JSONException je) {
            //TODO
        }
    }

    static ArrayList<Location> fromJson(JSONArray array) {
        ArrayList<Location> locations = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                locations.add(new Location(array.getJSONObject(i)));
            }
        } catch (JSONException je) {
            //TODO
        }
        return locations;
    }

    BitmapDescriptor getMarkerIcon(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.toilet);
        icon.setColorFilter((int)Review.reviewToColor(avg), PorterDuff.Mode.MULTIPLY);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        icon.setBounds(0,0,icon.getIntrinsicWidth(),icon.getIntrinsicHeight());
        icon.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
