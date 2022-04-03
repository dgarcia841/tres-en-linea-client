package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView test = (ImageView) findViewById(R.id.iv_test);
        Glide.with(this).load("http://192.168.0.192:3000").into(test);


        Socket socket = IO.socket(URI.create("http://192.168.0.192:4500"));
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("SOCKET", "connected!");
        });
        socket.connect();
    }
}