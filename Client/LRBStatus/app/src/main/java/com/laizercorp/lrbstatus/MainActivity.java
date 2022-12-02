package com.laizercorp.lrbstatus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // define variable for holding the ImageView widget
    ImageView ivStatus;
    CardView cvSettings;
    // swipe refresh layout, allows to reload when a user initiates a vertical pull on the screen
    SwipeRefreshLayout swipeRefreshLayout;
    // define variables for text views
    TextView tvStatus, tvConfidence, tvLastChecked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // link ImageView widget with Java code
        cvSettings = findViewById(R.id.cv_settings);
        ivStatus = findViewById(R.id.iv_status);
        // link swipe refresh layout widget with java code
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.light_green));
        // link text view widget with java code
        tvStatus = findViewById(R.id.tv_status);
        tvConfidence = findViewById(R.id.tv_confidence);
        tvLastChecked = findViewById(R.id.tv_last_checked);

        // create a listener, whenever the icon is clicked, the app moves to a new activity
        cvSettings.setOnClickListener(view->{
            // intent definition
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // fetching data from the internet using volley
        swipeRefreshLayout.setRefreshing(true);
        fetchData();

        // callback for swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if(isConnected()){
                    fetchData();
                }
                else{
                    Toast.makeText(MainActivity.this, "Not Internet Connection!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // function to check if their is internet connection on the device
    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    // get data from the server using volley json request
    public void fetchData(){
        String url = "https://boomboomboom.pythonanywhere.com/consume";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                populateWidgets(response);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Connection Error!", Toast.LENGTH_LONG).show();
                Log.d("kosa langu: ", error.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    // method to fill data fetched from the server to the widgets for display
    public void populateWidgets(JSONObject data){
        try {
            tvStatus.setText(data.getString("status"));
            tvLastChecked.setText(data.getString("last_checked"));
            tvConfidence.setText(String.format("%s%%", data.getString("confidence")));

            int confidence = Integer.parseInt(data.getString("icon_value"));
            if(confidence == 2){
                ivStatus.setImageDrawable(getDrawable(R.drawable.ic_connected));
            }
            else if(confidence == 1){
                ivStatus.setImageDrawable(getDrawable(R.drawable.ic_not_sure));
            }
            else{
                ivStatus.setImageDrawable(getDrawable(R.drawable.ic_no_connection));
            }

        } catch (JSONException e) {
            Toast.makeText(this, "An Internal Error has Occurred!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
