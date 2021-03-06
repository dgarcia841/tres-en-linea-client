package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Componentes de UI con los que interactuar
    EditText inputUri;
    EditText inputUser;
    Button play;
    Button cancel;

    RadioButton rbPvp;
    RadioButton rbIa;

    RecyclerView rvLeaderboard;
    LeaderboardAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Componentes de UI con los que interactuar
        inputUri = (EditText) findViewById(R.id.tv_uri);
        inputUser = (EditText) findViewById(R.id.tv_username);
        play = (Button) findViewById(R.id.bt_play);
        cancel = (Button) findViewById((R.id.bt_cancel));
        rvLeaderboard = (RecyclerView) findViewById(R.id.rv_leaderboard);
        rbPvp = (RadioButton) findViewById(R.id.rb_pvp);
        rbIa = (RadioButton) findViewById(R.id.rb_ia);

        // Configurar eventos
        play.setOnClickListener(e -> onPlayClicked());
        cancel.setOnClickListener(e -> onCancelClicked());

        // Al iniciar la partida
        GameServer.get().onGameStarted((gameid, rivalname, yourturn, id) -> {
            // restaurar los botones
            this.runOnUiThread(() -> {
                setButtonsPlay();
            });

            onGameStarted(gameid, rivalname, yourturn, id);
        });

        // Al recibir un mensaje de error, mostrarlo en un Toast y restaurar los botones
        GameServer.get().onError((code, msg) -> {
            this.runOnUiThread(() -> {
                Toast.makeText(this, "Error " + code + ": " + msg, Toast.LENGTH_SHORT).show();
                setButtonsPlay();
            });
        });

        LinearLayoutManager layout = new LinearLayoutManager(this);
        leaderboardAdapter = new LeaderboardAdapter(new GameServer.IPlayer[0]);

        rvLeaderboard.setAdapter(leaderboardAdapter);
        rvLeaderboard.setLayoutManager(layout);

        // Al actualizarse el leaderboard
        GameServer.get().onLeaderboard((board) -> {
            this.runOnUiThread(() -> {
                Log.d("COUNT #1: ", String.valueOf(board.length));
                leaderboardAdapter.board = board;
                leaderboardAdapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * Ocurre cuando se encontr?? un rival y el juego inici??
     * @param gameid ID del juego iniciado
     * @param rivalname nombre del rival
     * @param yourturn ??Es turno del jugador (true) o del rival (false)?
     */
    void onGameStarted(String gameid, String rivalname, boolean yourturn, int id) {

        // Crear intent con los datos del juego iniciado
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        Bundle data = new Bundle();

        data.putString("gameid", gameid);
        data.putString("rivalname", rivalname);
        data.putBoolean("yourturn", yourturn);
        data.putInt("id", id);

        intent.putExtras(data);

        // Lanzar actividad
        startActivity(intent);
    }

    /**
     * Ocurre cuando se hace click en el bot??n de cancelar
     */
    void onCancelClicked() {
        // Desconectar
        GameServer.get().getSocket().disconnect();
        setButtonsPlay();
    }

    /**
     * Ocurre cuando se hace click en el bot??n de click
     */
    void onPlayClicked() {
        // Obtener datos de conexi??n (URI del servidor y nombre de usuario)
        final String serverUri = inputUri.getText().toString();
        final String username = inputUser.getText().toString();
        final boolean checkedPvp = rbPvp.isChecked();
        final boolean checkedIa = rbIa.isChecked();
        setButtonsLoading();

        final GameServer.GAMEMODE gm = checkedPvp ? GameServer.GAMEMODE.pvp: GameServer.GAMEMODE.ia;

        // Conectarse al servidor
        GameServer.get().onConnect((e) -> {
            GameServer.get().startGame(username, gm);
        }).connect(serverUri);
    }


    /**
     * Poner los botones en el estado "Play"
     */
    void setButtonsPlay() {
        // Cambiar el texto de bot??n Play a Play
        play.setText(getString(R.string.button_play));
        // Marcar como activado el bot??n de play
        play.setEnabled(true);
        // Ocultar el bot??n de cancel
        cancel.setVisibility(View.INVISIBLE);
    }

    /**
     * Poner los botones en el estado "Loading"
     */
    void setButtonsLoading() {
        // Cambiar el texto de bot??n Play a Loading
        play.setText(getString(R.string.button_play_loading));
        // Marcar como desactivado el bot??n de play
        play.setEnabled(false);
        // Mostrar el bot??n de cancel
        cancel.setVisibility(View.VISIBLE);
    }
}