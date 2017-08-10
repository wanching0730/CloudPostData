package com.example.cloudpostdata;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by James Ooi on 9/8/2017.
 */

public class PostJsonTask extends AsyncTask<Void, Void, JSONObject> {
    //private static final String JSON_URL = "https://labs.jamesooi.com/android-post.php";
    private static final String JSON_URL = "https://labs.jamesooi.com/android-post2.php";

    private MainActivity activity;

    public PostJsonTask(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected JSONObject doInBackground(Void... v) {
        JSONObject response = null;

        try {
            response = postJson();
        }
        catch (IOException ex) {
            Log.e("IO_EXCEPTION", ex.toString());
        }

        return response;
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        try {
            Log.d("TIMESTAMP", Long.toString(response.getLong("timestamp")));
            Log.d("SUCCESS", Boolean.toString(response.getBoolean("success")));
            Log.d("JSON_RECEIVED_BY_SCRIPT", response.getString("dataReceived"));
        }
        catch (Exception ex) {
            Log.e("JSON_EXCEPTION", ex.toString());
        }
    }

    private JSONObject postJson() throws IOException {
        InputStream is = null;
        OutputStream os = null;

        try {
            JSONObject postData = new JSONObject();
            postData.put("name", activity.etName.getText().toString());
            postData.put("email", activity.etEmail.getText().toString());
            postData.put("phone", activity.etPhone.getText().toString());

            URL url = new URL(JSON_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Starts the query
            os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postData));
            writer.flush();
            writer.close();
            os.close();


            int responseCode = conn.getResponseCode();
            if(responseCode == 200) {
                is = conn.getInputStream();

                // Convert the InputStream into ArrayList<Person>
                return readInputStream(is);
            }
            else {
                Log.e("HTTP_ERROR", Integer.toString(responseCode));
                return null;
            }
        }
        catch (Exception ex) {
            Log.e("EXCEPTION", ex.toString());
            return null;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public JSONObject readInputStream(InputStream is)
            throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder builder = new StringBuilder();

        String input;
        while ((input = reader.readLine()) != null)
            builder.append(input);

        return new JSONObject(builder.toString());
    }

    private String getPostDataString(JSONObject data) throws Exception {

        StringBuilder result = new StringBuilder();

        //data=<json_string>
        //encode into php format
        //Log.d("URL DATA", result.toString());
        result.append(URLEncoder.encode("data", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(data.toString(), "UTF-8"));
        Log.d("URL DATA", result.toString());

        return result.toString();
    }
}
