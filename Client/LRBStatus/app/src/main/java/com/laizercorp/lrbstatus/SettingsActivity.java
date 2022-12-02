package com.laizercorp.lrbstatus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    // declare variables to hold widgets
    ImageView ivBackSettings;
    CardView cvHow, cvJoin, cvShare, cvFeedback, cvSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // link variables with xml widgets
        ivBackSettings = findViewById(R.id.iv_back_settings);
        cvHow = findViewById(R.id.cv_how);
        cvJoin = findViewById(R.id.cv_join);
        cvShare = findViewById(R.id.cv_share);
        cvFeedback = findViewById(R.id.cv_feedback);
        cvSource = findViewById(R.id.cv_source);

        // when clicked, go back to parent activity
        ivBackSettings.setOnClickListener(view->{
            // a simple call to finish does the trick, it ends the current activity.
            finish();
        });

        // when clicked navigate to how it works activity
        cvHow.setOnClickListener(view->{
            Intent intent = new Intent(SettingsActivity.this, HowItWorksActivity.class);
            startActivity(intent);
        });

        // when click navigate to join activity
        cvJoin.setOnClickListener(view->{
            Intent intent = new Intent(SettingsActivity.this, JoinActivity.class);
            startActivity(intent);
        });

        // when clicked open intent for sharing the app
        cvShare.setOnClickListener(view->{
            // share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share App with Friends");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "link to app will be available soon!");
            startActivity(Intent.createChooser(shareIntent, "Share Via"));
        });

        // when clicked, navigate to feedback activity
        cvFeedback.setOnClickListener(view->{
            Intent intent = new Intent(SettingsActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        // when clicked, open in browser link to github repository with source code
        cvSource.setOnClickListener(view->{
            // browser intent
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sdasongs.pythonanywhere.com"));
            startActivity(urlIntent);
        });
    }
}