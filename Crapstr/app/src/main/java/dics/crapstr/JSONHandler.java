package dics.crapstr;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by daniel on 11/3/2016.
 * This class handles communication between the app and the backend
 *
 */

public class JSONHandler extends AsyncTask<String, Void, Object> {

    private MapHandler.Callback callback;

    JSONHandler(MapHandler.Callback callback) {
        super();
        this.callback = callback;
    }

    @Override
    /* params[0] is url
     */
    protected Object doInBackground(String... params) {
        try {
            String jsonStr = downloadUrl(params[0]);
            return new JSONTokener(jsonStr).nextValue();
        } catch (IOException ioe) {
            return "Unable to retrieve web page. URL may be invalid.";
        } catch (JSONException je) {
            //TODO: something???????
            return "";
        }
    }

    @Override
    protected void onPostExecute(Object result) {
            this.callback.call(result);
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
                sb.append(line).append("\n");
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
