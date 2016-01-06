package net.azurewebsites.fishprice.feeder3;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Saral on 30-12-2015.
 * Need to create it as an extension of Service rather than extends IntentService
 * implementation using IntentSerice will try to kill the service after onHandleIntent
 * But it will fail since the mGoogleApiClient is not disconnected and the service will continue to run
 */
public class FeederService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private String imeiNumber;
    public static boolean isFeederServiceRunning = false;
    public static Integer scheduleId;

    public FeederService() {
        super("FeederService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        scheduleId = Integer.parseInt(intent.getExtras().getString("scheduleId"));
        //Prevent duplication of intent service
        if (!isFeederServiceRunning) {
            isFeederServiceRunning = true;
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApiIfAvailable(LocationServices.API)
                    .addApiIfAvailable(AppIndex.API).build();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imeiNumber = telephonyManager.getDeviceId();
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dId", imeiNumber);
        map.put("lat", String.valueOf(location.getLatitude()));
        map.put("lon", String.valueOf(location.getLongitude()));
        map.put("dt", String.valueOf(java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())));
        map.put("sId", scheduleId.toString());
        feeder locfeeder = new feeder(this);
        locfeeder.execute(getString(R.string.feeder_url) + "?" + getPostDataString(map));
        Toast.makeText(this, "Updated: " + mLastUpdateTime, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(15000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Stopping Syncing", Toast.LENGTH_SHORT).show();
        // mGoogleApiClient.disconnect(); <ToDo> enable it after changing service implementation to 'extends Service'
    }


    public String getPostDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            //do nothing
        }
        return result.toString();
    }

}

class feeder extends AsyncTask<String, String, String> {
    private Context mContext;
    protected String doInBackground(String... rurl) {
        String response = "";
        try {
            JSONParser jsonParser = new JSONParser();
            response  = jsonParser.getResponseFromURL(rurl[0]);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public feeder(Context context) {
        mContext = context;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("FeedResponse");
        //broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("FeedResult", result);
        mContext.sendBroadcast(broadcastIntent);
    }
}
