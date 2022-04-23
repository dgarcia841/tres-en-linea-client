package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


        // Componentes de UI con los que interactuar
        final EditText inputUri = (EditText) findViewById(R.id.tv_uri);
        final EditText inputUser = (EditText) findViewById(R.id.tv_username);
        final Button play = (Button) findViewById(R.id.bt_play);
        final Button cancel = (Button) findViewById((R.id.bt_cancel));

        // Iniciar conexión al presionar el botón de Play
        play.setOnClickListener((e) -> {

            // Obtener texto actual del botón de Play
            final String text = play.getText().toString();
            // Obtener datos de conexión (URI del servidor y nombre de usuario)
            final String serverUri = inputUri.getText().toString();
            final String username = inputUser.getText().toString();

            // Cambiar el texto de botón Play a Loading
            play.setText(getString(R.string.button_play_loading));
            // Marcar como desactivado el botón de play
            play.setEnabled(false);
            // Mostrar el botón de cancel
            cancel.setVisibility(View.VISIBLE);
            // Cancelar conexión al pulsar el botón de cancel
            cancel.setOnClickListener((f) -> {
                GameServer.get().getSocket().disconnect();
                // Restaurar botones
                play.setText(text);
                play.setEnabled(true);
                cancel.setVisibility(View.INVISIBLE);
            });

            // Conectarse al servidor
            GameServer.get().onConnect((socket) -> {
                socket.emit("startGame", username);
            }).connect(serverUri);

            // Al iniciar la partida
            GameServer.get().onGameStarted((gameid, rivalname, yourturn) -> {
                this.runOnUiThread(() -> {
                    // Restaurar botones
                    play.setText(text);
                    play.setEnabled(true);
                    cancel.setVisibility(View.INVISIBLE);
                });
                Log.d("GAME STARTED: ", "gameid: " + gameid + ", rivalname: " + rivalname + ", your turn: " + (yourturn?"yes":"no"));
            });
        });

        Log.d("GAME", "App started");
    }
}