package dics.crapstr;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by daniel on 11/16/2017.
 *
 */

class Review {
    private int rating;
    private String description;

    /*public Review(int rating, String description) {
        this.rating = rating;
        this.description = description;
    }*/

    int getRating() { return rating; }
    String getDescription() { return description; }

    private Review(JSONObject object) {
        try {
            this.rating = object.getInt("rating");
            this.description = object.getString("description");
        } catch (JSONException je) {
            //TODO
        }
    }

    static ArrayList<Review> fromJson(JSONArray jsonObjects) {
        ArrayList<Review> reviews = new ArrayList<>();
        for (int i=0; i<jsonObjects.length(); i++) {
            try {
                reviews.add(new Review(jsonObjects.getJSONObject(i)));
            } catch (JSONException je) {
                //TODO
            }
        }
        return reviews;
    }

    static double reviewToColor(double rating) {
        if (rating < 3) {
            return (Color.YELLOW-Color.RED)/2*rating + (3*Color.RED-Color.YELLOW)/2;
        } else {
            return (Color.GREEN-Color.YELLOW)/2*rating + (-3*Color.GREEN+5*Color.YELLOW)/2;
        }
    }
}
