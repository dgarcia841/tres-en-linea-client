package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Componentes de UI con los que interactuar
    EditText inputUri;
    EditText inputUser;
    Button play;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Componentes de UI con los que interactuar
        inputUri = (EditText) findViewById(R.id.tv_uri);
        inputUser = (EditText) findViewById(R.id.tv_username);
        play = (Button) findViewById(R.id.bt_play);
        cancel = (Button) findViewById((R.id.bt_cancel));

        // Configurar eventos
        play.setOnClickListener(e -> onPlayClicked());
        cancel.setOnClickListener(e -> onCancelClicked());

        // Al iniciar la partida
        GameServer.get().onGameStarted((gameid, rivalname, yourturn) -> {
            // restaurar los botones
            this.runOnUiThread(() -> {
                setButtonsPlay();
            });

            onGameStarted(gameid, rivalname, yourturn);
        });

        // Al recibir un mensaje de error, mostrarlo en un Toast y restaurar los botones
        GameServer.get().onError((code, msg) -> {
            this.runOnUiThread(() -> {
                Toast.makeText(this, "Error " + code + ": " + msg, Toast.LENGTH_SHORT).show();
                setButtonsPlay();
            });
        });
    }

    /**
     * Ocurre cuando se encontró un rival y el juego inició
     * @param gameid ID del juego iniciado
     * @param rivalname nombre del rival
     * @param yourturn ¿Es turno del jugador (true) o del rival (false)?
     */
    void onGameStarted(String gameid, String rivalname, boolean yourturn) {

        // Crear intent con los datos del juego iniciado
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        Bundle data = new Bundle();

        data.putString("gameid", gameid);
        data.putString("rivalname", rivalname);
        data.putBoolean("yourturn", yourturn);

        intent.putExtras(data);

        // Lanzar actividad
        startActivity(intent);
    }

    /**
     * Ocurre cuando se hace click en el botón de cancelar
     */
    void onCancelClicked() {
        // Desconectar
        GameServer.get().getSocket().disconnect();
        setButtonsPlay();
    }

    /**
     * Ocurre cuando se hace click en el botón de click
     */
    void onPlayClicked() {
        // Obtener datos de conexión (URI del servidor y nombre de usuario)
        final String serverUri = inputUri.getText().toString();
        final String username = inputUser.getText().toString();

        setButtonsLoading();

        // Conectarse al servidor
        GameServer.get().onConnect((socket) -> {
            socket.emit("startGame", username);
        }).connect(serverUri);
    }


    /**
     * Poner los botones en el estado "Play"
     */
    void setButtonsPlay() {
        // Cambiar el texto de botón Play a Play
        play.setText(getString(R.string.button_play));
        // Marcar como activado el botón de play
        play.setEnabled(true);
        // Ocultar el botón de cancel
        cancel.setVisibility(View.INVISIBLE);
    }

    /**
     * Poner los botones en el estado "Loading"
     */
    void setButtonsLoading() {
        // Cambiar el texto de botón Play a Loading
        play.setText(getString(R.string.button_play_loading));
        // Marcar como desactivado el botón de play
        play.setEnabled(false);
        // Mostrar el botón de cancel
        cancel.setVisibility(View.VISIBLE);
    }
}