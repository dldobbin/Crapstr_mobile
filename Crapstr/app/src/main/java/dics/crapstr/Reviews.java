package dics.crapstr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 11/16/2017.
 *
 */

class Reviews implements Serializable {
    private String placeId;
    private double average;
    private ArrayList<Review> reviews;

    public Reviews(String placeId) {
        this.placeId = placeId;
        this.reviews = new ArrayList<>();
    }

    String getPlaceId() { return placeId; }
    double getAverage() { return average; }
    ArrayList<Review> getReviews() { return reviews; }

    Reviews(JSONObject object) {
        try {
            this.placeId = object.getString("placeId");
            this.average = object.getDouble("avg");
            this.reviews = Review.fromJson(object.getJSONArray("reviews"));
        } catch (JSONException je) {
            //TODO
        }
    }
}
