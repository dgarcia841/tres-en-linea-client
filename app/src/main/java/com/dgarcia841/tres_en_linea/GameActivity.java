package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    TextView tvGameid;
    TextView tvRival;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvGameid = (TextView) findViewById(R.id.tv_gameid);
        tvRival = (TextView) findViewById(R.id.tv_rival);

        // Obtener datos del juego
        Bundle data = getIntent().getExtras();

        tvGameid.setText(getString(R.string.label_gameid) + " " + data.getString("gameid"));
        tvRival.setText(getString(R.string.label_rival) + " " + data.getString("rivalname"));
    }

    /**
     * Disconnect client on activity pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        finish();
        GameServer.get().getSocket().disconnect();
    }
}