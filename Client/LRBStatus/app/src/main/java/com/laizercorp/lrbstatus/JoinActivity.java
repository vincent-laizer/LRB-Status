package com.laizercorp.lrbstatus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class JoinActivity extends AppCompatActivity {

    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ivBack = findViewById(R.id.iv_back_join);

        ivBack.setOnClickListener(view->{
            finish();
        });
    }
}