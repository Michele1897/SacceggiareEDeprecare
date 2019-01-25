package com.example.michele.bozze.StrutturaApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.michele.bozze.R;

public class ConnectionActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Button connect = findViewById(R.id.pulsanteConnetti);
        connect.setOnClickListener(v -> {
            startActivity(new Intent("android.intent.action.MainActivity"));
        });

    }
}
