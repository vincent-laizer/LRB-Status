package com.laizercorp.lrbstatus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class HowItWorksActivity extends AppCompatActivity {

    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);

        ivBack = findViewById(R.id.iv_back_how);

        ivBack.setOnClickListener(view->{
            finish();
        });
    }
}