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

    private GameServer() {
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
        socket.on("onRivalPlay", args -> {
            Log.d("EVENT", String.valueOf(args));
            if (onRivalPlay != null) {
                onRivalPlay.onRivalPlay("", "", 0, 0);
            }
        });
        socket.on("onGameStarted", (Object... args) -> {
            String gameid = (String) args[0];
            String rivalname = (String) args[1];
            boolean yourturn = (boolean) args[2];

            if (onGameStarted != null) {
                onGameStarted.onGameStarted(gameid, rivalname, yourturn);
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
            if(onGameEnded != null) {
                onGameEnded.onGameEnded(gameid, winnername, result);
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
        void onRivalPlay(String gameid, String rivalname, int x, int y);
    }
    private IOnRivalPlay onRivalPlay;
    public GameServer onRivalPlay(IOnRivalPlay event) {
        this.onRivalPlay = event;
        return this;
    }

    // EVENTO GAME STARTED
    public interface IOnGameStarted {
        void onGameStarted(String gameid, String rivalname, boolean yourturn);
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
    public interface IOnGameEnded {
        void onGameEnded(String gameid, String winnername, String result);
    }
    private IOnGameEnded onGameEnded;
    public GameServer onGameEnded(IOnGameEnded event) {
        this.onGameEnded = event;
        return this;
    }



    public Socket getSocket() {
        return socket;
    }
}
