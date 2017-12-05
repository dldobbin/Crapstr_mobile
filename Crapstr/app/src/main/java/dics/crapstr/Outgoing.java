package dics.crapstr;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by daniel on 12/4/2017.
 *
 */

public class Outgoing implements Serializable {
    private String URL;
    private String placeId;
    private double lat;
    private double lon;
    private int rating;
    private String description;

    Outgoing(String URL, String placeId, LatLng latLng) {
        this.URL = URL;
        this.placeId = placeId;
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }

    String getURL() {
        return URL;
    }

    String getPlaceId() {
        return placeId;
    }

    LatLng getLatLng() {
        return new LatLng(lat, lon);
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String prepForPost() {
        return "placeId=" + placeId + "&rating=" + rating + "&description=" + description + "&lon=" + lon + "&lat=" + lat;
    }
}
