package com.dgarcia841.tres_en_linea;
import android.util.Log;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;

public class GameServer {

    private static GameServer instance;

    public static GameServer get() {
        if(instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    private Socket socket;

    private IOnConnect onConnect;
    private IOnRivalPlay onRivalPlay;
    private IOnGameStarted onGameStarted;
    private GameServer() { }

    public GameServer connect(String uri) {
        Log.d("GAME", "connecting to the URI: " + uri);
        socket = IO.socket(URI.create(uri));
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("GAME", "Connected to the server");
            if(onConnect != null) {
                onConnect.onConnect(socket);
            }
        });
        socket.on("onRivalPlay", args -> {
            Log.d("EVENT", String.valueOf(args));
            if(onRivalPlay != null) {
                onRivalPlay.onRivalPlay("", "", 0, 0);
            }
        });
        socket.on("onGameStarted", (Object... args) -> {
            String gameid = (String) args[0];
            String rivalname = (String) args[1];
            boolean yourturn = (boolean) args[2];

            if(onGameStarted != null){
                onGameStarted.onGameStarted(gameid, rivalname, yourturn);
            }
        });
        socket.connect();
        return this;
    }

    public interface  IOnConnect {
        void onConnect(Socket socket);
    }
    public interface IOnRivalPlay {
        void onRivalPlay(String gameid, String rivalname, int x, int y);
    }
    public interface IOnGameStarted {
        void onGameStarted(String gameid, String rivalname, boolean yourturn);
    }

    public GameServer onConnect(IOnConnect event) {
        this.onConnect = event;
        return this;
    }
    public GameServer onRivalPlay(IOnRivalPlay event) {
        this.onRivalPlay = event;
        return this;
    }

    public GameServer onGameStarted(IOnGameStarted event) {
        onGameStarted = event;
        return this;
    }

    public Socket getSocket() {
        return socket;
    }
}
