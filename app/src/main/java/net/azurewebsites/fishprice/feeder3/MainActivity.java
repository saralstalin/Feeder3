package net.azurewebsites.fishprice.feeder3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    JSONArray jsonArray;
    Spinner routeSpinner = null;
    Spinner scheduleSpinner = null;
    static String selectedRoute = "";
    static String selectedSchedule = "";
    feederReplyReceiver myReceiver = null;
    Boolean myReceiverIsRegistered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        routeSpinner = (Spinner) findViewById(R.id.SpinnerRoutes);
        scheduleSpinner = (Spinner) findViewById(R.id.SpinnerSchedule);
        myReceiver = new feederReplyReceiver();


        if (savedInstanceState != null) {
            routeSpinner.setSelection(savedInstanceState.getInt("mySpinner", 0));
        }
        routeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mySpinner", routeSpinner.getSelectedItemPosition());
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

        if (parentView.getId() == R.id.SpinnerRoutes) {
            SpinnerClass d = (SpinnerClass) parentView.getItemAtPosition(position);
            int selectedRouteInfoId = d.getId();
            selectedRoute = routeSpinner.getSelectedItem().toString();
            new populateSchedule().execute(getString(R.string.schedule_url) + "?rId=" + Integer.toString(selectedRouteInfoId));
        }
        if (parentView.getId() == R.id.SpinnerSchedule) {
            selectedSchedule = scheduleSpinner.getSelectedItem().toString();
            startService(position);
            ListView listView = (ListView) findViewById(R.id.ListViewStatus);
            listView.setVisibility(View.INVISIBLE);
        }
    }

    public void startService(Integer position) {
        SpinnerClass d = (SpinnerClass) ((Spinner) findViewById(R.id.SpinnerSchedule)).getItemAtPosition(position);
        int selectedScheduleId = d.getId();
        Intent intent = new Intent(MainActivity.this, FeederService.class);
        intent.putExtra("scheduleId", Integer.toString(selectedScheduleId));
        startService(intent);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {

    }

    @Override
    public void onStart() {
        super.onStart();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //<ToDO>Should we use android device id? People are not familiar with android id and may find difficult to get it, if asked for enrolling new device.
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        new populateRoutes().execute(getString(R.string.routes_url) + "?dId=" + telephonyManager.getDeviceId());

        registerReceiver(myReceiver, new IntentFilter("FeedResponse"));
        myReceiverIsRegistered = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (!myReceiverIsRegistered) {
            registerReceiver(myReceiver, new IntentFilter("FeedResponse"));
            myReceiverIsRegistered = true;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (myReceiverIsRegistered) {
            unregisterReceiver(myReceiver);
            myReceiverIsRegistered = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class populateRoutes extends AsyncTask<String, String, JSONArray> {
        protected JSONArray doInBackground(String... rurl) {
            try {
                JSONParser jsonParser = new JSONParser();
                jsonArray = jsonParser.getJSONFromURL(rurl[0]);

                return jsonArray;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            // TODO Auto-generated method stub
            //access UI elements here..
            TextView txtAccessMessage = (TextView) findViewById(R.id.textViewAccessMessage);
            // Spinner adapter
            try {
                final String[] items = new String[result.length()];
                if (result == null || result.length() == 0) {
                    txtAccessMessage.setText(getString(R.string.NoAccessMessage));
                    TableLayout tableLayout = (TableLayout) findViewById(R.id.tableRoutes);
                    tableLayout.setVisibility(View.GONE);

                } else {
                    ArrayAdapter<SpinnerClass> listItemAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item);
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject c = result.getJSONObject(i);
                        listItemAdapter.add(new SpinnerClass(
                                Integer.parseInt(c.getString("RouteInfoId")),
                                c.getString("RouteName")));
                    }
                    routeSpinner.setAdapter(listItemAdapter);
                    if (selectedRoute != "") {
                        setSpinnerValue(routeSpinner, selectedRoute);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class populateSchedule extends AsyncTask<String, String, JSONArray> {
        protected JSONArray doInBackground(String... rurl) {
            try {
                JSONParser jsonParser = new JSONParser();
                jsonArray = jsonParser.getJSONFromURL(rurl[0]);

                return jsonArray;
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                ArrayAdapter<SpinnerClass> listItemAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject c = result.getJSONObject(i);
                    listItemAdapter.add(new SpinnerClass(
                            Integer.parseInt(c.getString("ScheduleId")),
                            c.getString("ScheduleName")));
                    scheduleSpinner.setAdapter(listItemAdapter);
                    //at first we start service with default route and schedule
                    //This way application works without user input
                    //<ToDo>Look at option of user training and get actual route and schedule from driver.
                    if (selectedRoute != "") {
                        startService(0);
                    }
                    setSpinnerValue(scheduleSpinner, selectedSchedule);
                    scheduleSpinner.setOnItemSelectedListener(MainActivity.this);
                    startService(scheduleSpinner.getSelectedItemPosition());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setSpinnerValue(Spinner spin, String value) {

        for (int i = 0; i < spin.getCount(); i++) {
            if (spin.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spin.setSelection(i);
                break;
            }
        }
    }

    public class feederReplyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String reponseMessage = intent.getStringExtra("FeedResult");
            LoadResults(reponseMessage);
        }
    }

    public  void LoadResults(String responseMessage)
    {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(responseMessage);
        } catch (JSONException e) {
            return;
        }
        ArrayList<FeedResults> feedResultses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                FeedResults fr = new FeedResults();
                fr.setDestinationName(jsonObject.getString("DestinationName"));
                fr.setScheduledDeparture(jsonObject.getString("Scheduled Departure"));
                fr.setStatus(jsonObject.getString("Status"));
                feedResultses.add(fr);
            } catch (JSONException e) {
                //ignore
            }
        }
        ListView listView = (ListView) findViewById(R.id.ListViewStatus);
        listView.setVisibility(View.VISIBLE);
        CustomAdaptor adapter = new CustomAdaptor(MainActivity.this, feedResultses);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}

