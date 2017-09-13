package dics.crapstr;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by daniel on 11/3/2016.
 */

public class JSONHandler extends AsyncTask<String, Void, String> {

    private MapHandler mapHandler;
    private Object JSON;

    public JSONHandler(MapHandler mapHandler) {
        super();
        this.mapHandler = mapHandler;
    }

    @Override
    /* params[0] is url
     * params[1] is method
     */
    protected String doInBackground(String... params) {
        try {
            String jsonStr = downloadUrl(params[0]);
            if (jsonStr != null) {
                switch (params[1]) {
                    case "locations":
                        JSON = new JSONArray(jsonStr);
                        break;
                    case "reviews":
                        JSON = new JSONObject(jsonStr);
                }
            }
            return params[1];
        } catch (IOException ioe) {
            return "Unable to retrieve web page. URL may be invalid.";
        } catch (JSONException e) {
            //TODO: do something with this
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            switch (result) {
                case "locations":
                    mapHandler.markLocations((JSONArray)JSON);
                    break;
                case "reviews":
                    mapHandler.showReviews((JSONObject)JSON);
                    break;
            }

        } catch (JSONException e) {
            //TODO: do something with this
        }
    }

    private String downloadUrl(String url_S) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(url_S);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
