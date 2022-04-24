package com.dgarcia841.tres_en_linea;
import android.util.Log;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;

public class GameServer {

    private static GameServer instance;

    public static GameServer get() {
        if (instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    private Socket socket;
    private String gameid;
    private String username;

    private GameServer() { }

    public void startGame(String username) {
        this.username = username;
        socket.emit("startGame", username);
    }

    public GameServer connect(String uri) {
        Log.d("GAME", "connecting to the URI: " + uri);
        socket = IO.socket(URI.create(uri));
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("GAME", "Connected to the server");
            if (onConnect != null) {
                onConnect.onConnect(socket);
            }
        });
        socket.on("onGameStarted", (Object... args) -> {
            gameid = (String) args[0];
            String rivalname = (String) args[1];
            boolean yourturn = (boolean) args[2];
            int id = (int) args[3];

            if (onGameStarted != null) {
                onGameStarted.onGameStarted(gameid, rivalname, yourturn, id);
            }
        });
        socket.on("onError", (Object... args) -> {
            int code = (int) args[0];
            String message = (String) args[1];
            if (onError != null) {
                onError.onError(code, message);
            }
        });
        socket.on("onGameEnded", (Object... args) -> {
            String gameid = (String) args[0];
            String winnername = (String) args[1];
            String result = (String) args[2];

            RESULT rid = RESULT.UNKNOWN;
            switch(result) {
                case "victory":
                    rid = RESULT.VICTORY;
                    break;
                case "defeat":
                    rid = RESULT.DEFEAT;
                    break;
                case "draw":
                    rid = RESULT.DRAW;
                    break;
            }
            if(onGameEnded != null) {
                onGameEnded.onGameEnded(gameid, winnername, rid);
            }
        });
        socket.on("onRivalPlay", (Object... args) -> {
            String gameid = (String) args[0];
            int x = (int) args[1];
            int y = (int) args[2];
            if(onRivalPlay != null) {
                onRivalPlay.onRivalPlay(gameid, x, y);
            }
        });
        socket.on("onDraw", (Object... args) -> {
            if(onDraw != null) {
                onDraw.onDraw();
            }
        });
        socket.on("onWin", (Object... args) -> {
            String gameid = (String) args[0];
            String winner = (String) args[1];
            String resultStr = (String) args[2];
            String whereStr = (String) args[3];
            int position = (int) args[4];

            RESULT result = RESULT.UNKNOWN;
            WHERE where = WHERE.UNKNOWN;

            switch (resultStr) {
                case "victory":
                    result = RESULT.VICTORY;
                    break;
                case "defeat":
                    result = RESULT.DEFEAT;
                    break;
                case "draw":
                    result = RESULT.DRAW;
                    break;
            }
            switch (whereStr) {
                case "row":
                    where = WHERE.ROW;
                    break;
                case "column":
                    where = WHERE.COLUMN;
                    break;
                case "diagonal":
                    where = WHERE.DIAGONAL;
                    break;
            }

            if(onWin != null) {
                onWin.onWin(gameid, winner, result, where, position);
            }
        });

        socket.on("onGameRestarted", (Object... args) -> {
            boolean yourturn = (boolean) args[0];
            if(onGameRestarted != null) {
                onGameRestarted.onGameRestarted(yourturn);
            }
        });
        socket.connect();
        return this;
    }

    /// EVENTO CONNECT

    public interface IOnConnect {
        void onConnect(Socket socket);
    }

    private IOnConnect onConnect;
    public GameServer onConnect(IOnConnect event) {
        this.onConnect = event;
        return this;
    }

    // EVENTO RIVAL PLAY
    public interface IOnRivalPlay {
        void onRivalPlay(String gameid, int x, int y);
    }
    private IOnRivalPlay onRivalPlay;
    public GameServer onRivalPlay(IOnRivalPlay event) {
        this.onRivalPlay = event;
        return this;
    }

    // EVENTO GAME STARTED
    public interface IOnGameStarted {
        void onGameStarted(String gameid, String rivalname, boolean yourturn, int id);
    }
    private IOnGameStarted onGameStarted;
    public GameServer onGameStarted(IOnGameStarted event) {
        onGameStarted = event;
        return this;
    }

    // EVENTO ERROR
    public interface IOnError {
        void onError(int code, String message);
    }
    private IOnError onError;
    public GameServer onError(IOnError event) {
        onError = event;
        return this;
    }

    // EVENTO GAME END
    public enum RESULT {
        VICTORY,
        DEFEAT,
        DRAW,
        UNKNOWN
    }
    public interface IOnGameEnded {
        void onGameEnded(String gameid, String winnername, RESULT result);
    }
    private IOnGameEnded onGameEnded;
    public GameServer onGameEnded(IOnGameEnded event) {
        this.onGameEnded = event;
        return this;
    }

    // EVENTO DRAW (RECIBIR RESULTADO DE EMPATE)
    public interface IOnDraw {
        void onDraw();
    }
    private IOnDraw onDraw;
    public GameServer onDraw(IOnDraw event) {
        onDraw = event;
        return this;
    }

    // EVENTO WIN (ALGUIEN GANÓ LA PARTIDA)
    public enum WHERE {
        ROW,
        COLUMN,
        DIAGONAL,
        UNKNOWN
    }
    public interface IOnWin {
        void onWin(String gameid, String winner, RESULT result, WHERE where, int position);
    }
    private IOnWin onWin;
    public GameServer onWin(IOnWin event) {
        onWin = event;
        return this;
    }

    // EVENTO RESTART (PARTIDA REINICIADA
    public interface IOnGameRestarted {
        void onGameRestarted(boolean yourturn);
    }
    private IOnGameRestarted onGameRestarted;
    public GameServer onGameRestarted(IOnGameRestarted event) {
        onGameRestarted = event;
        return this;
    }

    // HACER JUGADA
    public void play(int x, int y) {
        socket.emit("playGame", gameid, username, x, y);
    }



    public Socket getSocket() {
        return socket;
    }
}
