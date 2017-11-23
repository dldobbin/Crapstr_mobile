package dics.crapstr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by daniel on 11/16/2017.
 *
 */

class Review implements Serializable {
    private int rating;
    private String description;

    public Review(int rating, String description) {
        this.rating = rating;
        this.description = description;
    }

    int getRating() { return rating; }
    void setRating(int rating) { this.rating = rating; }
    String getDescription() { return description; }
    void setDescription(String description) { this.description = description; }

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
}
