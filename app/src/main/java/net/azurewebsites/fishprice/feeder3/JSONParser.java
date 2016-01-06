package net.azurewebsites.fishprice.feeder3;


import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Saral on 31-12-2015.
 */
public class JSONParser {
    static JSONArray jsonArray = null;

    // constructor
    public JSONParser() {
    }

    public String getResponseFromURL(String requestURL) {
        URL url = null;
        String response = "";
        // Making HTTP request
        try {

            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //do nothing for today :)
        }

        return response;
    }

    public JSONArray getJSONFromURL(String url)
    {
        // try parse the string to a JSON object
        try {
            jsonArray = new JSONArray(getResponseFromURL(url));
        } catch (JSONException e) {
            //ignore :)
        }
        // return JSON String
        return jsonArray;
    }
}
