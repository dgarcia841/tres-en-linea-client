package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView test = (ImageView) findViewById(R.id.iv_test);
        Glide.with(this).load("http://192.168.0.192:3000").into(test);
    }
}