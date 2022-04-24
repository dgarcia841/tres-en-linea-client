package com.dgarcia841.tres_en_linea;

import static com.dgarcia841.tres_en_linea.R.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adaptador de la lista de celdas en el tablero de juego
 */
public class GameCellAdapter extends RecyclerView.Adapter<GameCellAdapter.ViewHolder> {

    public interface IOnPlay {
        void onPlay(int x, int y);
    }
    private IOnPlay onPlay;
    public  void onPlay(IOnPlay event) {
        onPlay = event;
    }

    // ¿Es el turno del jugador?
    public boolean yourturn;
    // Tablero de juego
    GameBoard board;
    // id del jugador (0 o 1)
    int id;
    public GameCellAdapter(GameBoard b, boolean yourturn, int id) {
        board = b;
        this.yourturn = yourturn;
        this.id = id;
    }

    /**
     * Crear viewholder con una instancia de la activity celda
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(layout.activity_game_cell, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Relacionar el viewholder con la celda del tablero
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setValue(board.get(position), position);
    }

    /**
     * Siempre son 9 celdas en el tablero
     * @return
     */
    @Override
    public int getItemCount() {
        return 9;
    }

    /**
     * Para referenciar desde el viewholder
     * @return
     */
    public GameCellAdapter getThis() {
        return this;
    }

    /**
     * Viewholder de la celda
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * El botón de la celda
         */
        ImageButton cell;
        ImageView background;
        // marcas de cada jugador
        int[] keys = {drawable.cancel, drawable.circle};
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cell = (ImageButton) itemView.findViewById(R.id.bt_cell);
            background = (ImageView) itemView.findViewById(R.id.iv_background);
        }

        /**
         * Relaciona el botón de la celda, con la celda en el tablero
         * @param status Estado actual de la celda
         * @param position Posición de la celda
         */
        public void setValue(GameBoard.STATUS status, int position) {
            // Obtener (x, y) de la celda según su posición
            int x = position % 3;
            int y = (int) Math.floor(position / 3);
            // Asignar texto al botón según el estado de la celda
            switch(status) {
                default:
                    // Añadir evento para hacer jugada en las celdas vacías
                case EMPTY:
                    cell.setImageResource(drawable.empty);
                    cell.setOnClickListener((e) -> {
                        if(!yourturn) return;
                        if(onPlay != null) {
                            onPlay.onPlay(x, y);
                        }
                    });
                    break;
                case PLAYER:
                    cell.setImageResource(keys[id]);
                    break;
                case RIVAL:
                    cell.setImageResource(keys[1 - id]);
                    break;
            }
            // resaltar celda como ganadora
            if(board.isWinner(x, y)) {
                background.setBackgroundResource(drawable.selected);
            }
            else {
                background.setBackgroundResource(drawable.empty);
            }
        }
    }
}
