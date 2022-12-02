package com.laizercorp.lrbstatus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    ImageView ivBack, ivSend;
    EditText etFeedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ivBack = findViewById(R.id.iv_back_feedback);
        ivSend = findViewById(R.id.iv_send);
        etFeedback = findViewById(R.id.et_feedback);

        ivBack.setOnClickListener(view->{
            finish();
        });

        // disable send button
        ivSend.setEnabled(false);
        ivSend.setColorFilter(getResources().getColor(R.color.white));

        // called whenever the text changes
        etFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing for now
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(FeedbackActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();
                if(s.length() > 3){
                    // enable the send button
                    ivSend.setEnabled(true);
                    ivSend.setColorFilter(getResources().getColor(R.color.light_green));
                }
                else{
                    // disable the send button
                    ivSend.setEnabled(false);
                    ivSend.setColorFilter(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do nothing for now
            }
        });

        // send the feedback to server
        ivSend.setOnClickListener(view->{
            sendFeedback(etFeedback.getText().toString());
        });
    }

    public void sendFeedback(String message){
        ProgressDialog progressDialog = new ProgressDialog(FeedbackActivity.this);
        progressDialog.setMessage("Sending feedback, please wait...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://boomboomboom.pythonanywhere.com/feedback?feedback="+message,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.hide();
                Toast.makeText(FeedbackActivity.this, "Thank you for the feedback.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(FeedbackActivity.this, "An error occurred!", Toast.LENGTH_LONG).show();
                Log.d("kosa langu", error.getMessage());
            }
        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> data = new HashMap<>();
//                data.put("feedback", message);
//                return data;
//            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
        requestQueue.add(jsonObjectRequest);
    }
}