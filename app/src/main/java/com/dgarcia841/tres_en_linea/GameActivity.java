package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    GameBoard board;
    GameCellAdapter adapter;
    GridLayoutManager layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Obtener componentes de la GUI
        tvGameid = (TextView) findViewById(R.id.tv_gameid);
        tvRival = (TextView) findViewById(R.id.tv_rival);
        tvTurn = (TextView) findViewById(R.id.tv_turn);
        tvResult = (TextView) findViewById(R.id.tv_result);

        // Obtener datos del juego
        Bundle data = getIntent().getExtras();

        // Cargar datos de la partida en la GUI
        tvGameid.setText(String.format("%s %s", getString(R.string.label_gameid), data.getString("gameid")));
        tvRival.setText(String.format("%s %s", getString(R.string.label_rival), data.getString("rivalname")));

        // Cargar turno inicial en la GUI
        yourturn = data.getBoolean("yourturn");
        updateTurn();

        // Crear tablero de juego y adaptador para la lista de celdas
        board = new GameBoard();
        adapter = new GameCellAdapter(board, yourturn, data.getInt("id"));
        layout = new GridLayoutManager(GameActivity.this, 3);
        RecyclerView rvBoard = (RecyclerView) findViewById(R.id.rv_board);
        rvBoard.setLayoutManager(layout);
        rvBoard.setAdapter(adapter);

        // Cuando el juego termina
        GameServer.get().onGameEnded((a, b, c) -> onGameEnded(c));
        // cuando el rival juega
        GameServer.get().onRivalPlay((gameid, x, y) -> onRivalPlay(x, y));
        // cuando el jugador juega
        adapter.onPlay((x, y) -> onPlay(x, y));
    }

    /**
     * Actualizar textview con el turno actual
     */
    void updateTurn() {
        this.runOnUiThread(() -> {
            if(yourturn) {
                tvTurn.setText(getString(R.string.label_yourturn));
            }
            else {
                tvTurn.setText(getString(R.string.label_rivalturn));
            }
        });
    }
    /**
     * Ocurre cuando se recibe notificación de que el rival hizo un movimiento
     * @param x posición del juego
     * @param y posición del juego
     */
    void onRivalPlay(int x, int y) {
        this.runOnUiThread(() -> {
            // marcar jugada rival en el tablero
            board.set(x, y, GameBoard.STATUS.RIVAL);
            yourturn = true;
            adapter.yourturn = true;
            updateTurn();
            adapter.notifyDataSetChanged();
        });
    }
    /**
     * Ocurre cuando se recibe notificación de que el rival hizo un movimiento
     * @param x posición del juego
     * @param y posición del juego
     */
    void onPlay(int x, int y) {
        this.runOnUiThread(() -> {
            // marcar jugada en el tablero
            board.set(x, y, GameBoard.STATUS.PLAYER);
            GameServer.get().play(x, y);
            yourturn = false;
            adapter.yourturn = false;
            updateTurn();
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Ocurre cuando se notifica que el juego terminó
     * @param result Cómo terminó el juego
     */
    void onGameEnded(GameServer.RESULT result) {
        this.runOnUiThread(() -> {
            int rid = -1;
            switch(result) {
                case VICTORY:
                    rid = R.string.label_result_winner;
                    break;
                case DEFEAT:
                    rid = R.string.label_result_looser;
                    break;
                case DRAW:
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