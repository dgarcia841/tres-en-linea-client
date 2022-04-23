package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity {

    TextView tvGameid;
    TextView tvRival;
    TextView tvTurn;
    TextView tvResult;

    boolean yourturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        tvGameid = (TextView) findViewById(R.id.tv_gameid);
        tvRival = (TextView) findViewById(R.id.tv_rival);
        tvTurn = (TextView) findViewById(R.id.tv_turn);
        tvResult = (TextView) findViewById(R.id.tv_result);

        // Obtener datos del juego
        Bundle data = getIntent().getExtras();

        tvGameid.setText(String.format("%s %s", getString(R.string.label_gameid), data.getString("gameid")));
        tvRival.setText(String.format("%s %s", getString(R.string.label_rival), data.getString("rivalname")));

        yourturn = data.getBoolean("yourturn");
        if(yourturn) {
            tvTurn.setText(getString(R.string.label_yourturn));
        }
        else {
            tvTurn.setText(getString(R.string.label_rivalturn));
        }

        GameServer.get().onGameEnded((a, b, c) -> onGameEnded(c));
    }

    void onGameEnded(String result) {
        this.runOnUiThread(() -> {
            int rid = -1;
            switch(result) {
                case "victory":
                    rid = R.string.label_result_winner;
                    break;
                case "defeat":
                    rid = R.string.label_result_looser;
                    break;
                case "draw":
                    rid = R.string.label_result_draw;
                    break;
            }
            if(rid == -1) {
                tvResult.setText("");
            }
            else {
                tvResult.setText(getString(rid));
            }
        });
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