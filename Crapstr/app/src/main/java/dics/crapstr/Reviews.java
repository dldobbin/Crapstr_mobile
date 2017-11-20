package dics.crapstr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniel on 11/16/2017.
 *
 */

class Reviews {
    private double average;
    private ArrayList<Review> reviews;

    /*public Reviews(double average, ArrayList<Review> reviews) {
        this.average = average;
        this.reviews = reviews;
    }*/

    double getAverage() { return average; }
    ArrayList<Review> getReviews() { return reviews; }

    Reviews(JSONObject object) {
        try {
            this.average = object.getDouble("avg");
            this.reviews = Review.fromJson(object.getJSONArray("reviews"));
        } catch (JSONException je) {
            //TODO
        }
    }
}