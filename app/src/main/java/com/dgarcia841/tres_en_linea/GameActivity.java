package com.dgarcia841.tres_en_linea;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity {

    TextView tvGameid;
    TextView tvRival;
    TextView tvTurn;
    TextView tvResult;
    TextView tvScore;
    TextView tvRivalScore;

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

        tvScore = (TextView) findViewById(R.id.tv_yourscore);
        tvRivalScore = (TextView) findViewById(R.id.tv_rivalscore);

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
        // Cuando el juego termina en empate
        GameServer.get().onDraw(() -> onDraw());

        // Cuando alguien gana en el juego
        GameServer.get().onWin((gameid, winner, result, where, position) -> {
            onWin(result, where, position);
        });

        // Cuando el juego reinicia
        GameServer.get().onGameRestarted((yourturn) -> onGameRestarted(yourturn));

        // Cuando alguien puntúa
        GameServer.get().onScore((score, rivalscore) -> onScore(score, rivalscore));
    }

    void onScore(int score, int rivalscore) {
        this.runOnUiThread(() -> {
            tvScore.setText(String.valueOf(score));
            tvRivalScore.setText(String.valueOf(rivalscore));
        });
    }

    void onWin(GameServer.RESULT result, GameServer.WHERE where, int position) {
        GameActivity.this.runOnUiThread(() -> {

            // marcar en el tablero las celdas ganadoras
            switch(where) {
                case ROW:
                    board.setWinner(0, position);
                    board.setWinner(1, position);
                    board.setWinner(2, position);
                    break;
                case COLUMN:
                    board.setWinner(position, 0);
                    board.setWinner(position, 1);
                    board.setWinner(position, 2);
                    break;
                case DIAGONAL:
                    if(position == 0) {
                        board.setWinner(0, 0);
                        board.setWinner(1, 1);
                        board.setWinner(2, 2);
                    }
                    else {
                        board.setWinner(2, 0);
                        board.setWinner(1, 1);
                        board.setWinner(0, 2);
                    }
                    break;
            }


            // seleccionar texto de resultado
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
            // Mostrar el resultado
            if(rid != -1)
                tvResult.setText(rid);
            tvTurn.setText("");
            adapter.yourturn = false;
            adapter.notifyDataSetChanged();
        });
    }

    void onGameRestarted(boolean yourturn) {
        this.runOnUiThread(() -> {
            tvResult.setText("");
            this.yourturn = yourturn;
            adapter.yourturn = yourturn;
            updateTurn();
            board.clear();
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Ocurre cuando el juego termina en empate
     */
    void onDraw() {
        GameActivity.this.runOnUiThread(() -> {
            // Mostrar el resultado
            tvResult.setText(R.string.label_result_draw);
            tvTurn.setText("");
            adapter.yourturn = false;
            adapter.notifyDataSetChanged();
        });
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
    protected void onDestroy() {
        super.onDestroy();
        finish();
        GameServer.get().getSocket().disconnect();
    }
}