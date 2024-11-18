package serverfacade;

import com.google.gson.Gson;
import models.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData login(String username, String password) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/session")).toURL();
            UserData body = new UserData(username, password, null);
            String reqData = new Gson().toJson(body);
            return makeRequest(null,reqData,"POST",url,AuthData.class);
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    public AuthData register(String username, String password, String email) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/user")).toURL();
            UserData body = new UserData(username, password, email);
            String reqData = new Gson().toJson(body);
            return makeRequest(null,reqData,"POST",url,AuthData.class);
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    public void logout(String token) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/session")).toURL();
            makeRequest(token,null,"DELETE",url,null);
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    public int createGame(String token, String gameName) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/game")).toURL();
//            GameData body = new GameData(0,null,null,gameName,null);
//            String reqData = new Gson().toJson(body);
            String reqData = "{ \"gameName\": \"" + gameName + "\" }";
            return makeRequest(token,reqData,"POST",url,GameData.class).gameID();
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    public List<GameData> listGames(String token) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/game")).toURL();
            GamesObject gamesObject = makeRequest(token,null,"GET",url, GamesObject.class);
            return new ArrayList<>(gamesObject.games());
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }

    }

    public void playGame(String token, String playerColor, int gameId) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/game")).toURL();
            JoinObject body = new JoinObject(playerColor,gameId);
            String reqData = new Gson().toJson(body);
            makeRequest(token,reqData,"PUT",url,null);
            System.out.println("print out chessboard");

        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    public void observeGame(String token, String playerColor, int gameId) throws Exception {
        try {
            URL url = (new URI(serverUrl + "/game")).toURL();
            JoinObject body = new JoinObject(playerColor,gameId);
            String reqData = new Gson().toJson(body);
            makeRequest(token,reqData,"PUT",url,null);

        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    private <T> T makeRequest(String authToken, String reqData, String method, URL url, Class<T> responseClass) throws Exception {
        try {
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }
            if (reqData != null) {
                OutputStream reqBody = http.getOutputStream();
                reqBody.write(reqData.getBytes());
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http,responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.toString());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        int status = http.getResponseCode();
        if (status != 200) {
            throw new Exception(String.format("Connection not successful, error code %s",status));
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
