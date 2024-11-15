package ui;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dataaccess.DataAccessException;
import models.AuthData;
import models.GameData;
import serverfacade.ServerFacade;

public class ChessClient {
//    private final String serverUrl;
    private final ServerFacade server;
    private String authToken;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
//        this.serverUrl = serverUrl;
        this.authToken = "";
    }

    public List<Object> evalSignedOut(String input) {
        List<Object> result = new ArrayList<>();
        result.add("");
        result.add(1);
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "login" -> {
                    result.set(0, login(params));
                    result.set(1, 1);
                }
                case "register" -> result.set(0, register(params));
                case "quit" -> result.set(0, "quit");
                default -> result.set(0, helpLoggedOut());
            }
        } catch (Exception ex) {
            result.set(0,ex.getMessage());
        }
        return result;
    }

    public List<Object> evalSignedIn(String input) {
        List<Object> result = new ArrayList<>();
        result.add("");
        result.add(0);
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "logout" -> {
                    result.set(0, logout());
                    result.set(1, 0);
                }
                case "play" -> {
                    result.set(0, playGame(params));
                    result.set(1, 2);
                }
                case "create" -> result.set(0, createGame(params));
                case "list" -> result.set(0, listGames());
                case "observe" -> result.set(0, observeGame(params));
                default -> result.set(0, helpLoggedIn());
            }
        } catch (Exception ex) {
            result.set(0,ex.getMessage());
        }
        return result;
    }

    public String helpLoggedOut() {
        return """
                - login
                - register
                - quit
                """;
    }

    public String login(String... params) throws DataAccessException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = server.login(username,password);
            this.authToken = authData.authToken();
            return String.format("You signed in as %s", authData.username());
        }
        throw new DataAccessException("Login failed");
    }

    public String register(String... params) throws Exception {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = server.register(username,password,email);
            this.authToken = authData.authToken();
            return String.format("You signed up as %s", authData.username());
        }
        throw new DataAccessException("Registration failed");
    }

    public String helpLoggedIn() {
        return """
                - logout
                - create
                - list
                - play
                - observe
                """;
    }

    public String logout() {
        return "you have successfully logged out";
    }

    public String createGame(String... params) throws DataAccessException {
        if (params.length == 2) {
            String authToken = params[0];
            String gameName = params[1];
            server.createGame(authToken,gameName);
            return String.format("You created game %s", gameName);
        }
        throw new DataAccessException("Could not create game");
    }

    public String listGames() throws DataAccessException {
        String result = null;
        if (authToken.isEmpty()) {
            int i = 0;
            for (GameData game : server.listGames(this.authToken)) {
                i += 1;
                result = String.format("%s%d %s white player: %s black player %s\n", result, i, game.whiteUsername(), game.blackUsername(), game.blackUsername());
            }
        }
        return result;
    }

    public String playGame(String... params) throws DataAccessException {
        if (params.length == 3) {
            String authToken = params[0];
            String playerColor = params[1];
            int gameNumber = Integer.parseInt(params[2]);
            int gameId = server.listGames(authToken).get(gameNumber-1).gameID();
            server.playGame(authToken,playerColor,gameId);
            return String.format("You played game %s", gameId);
        }
        throw new DataAccessException("Could not play game");
    }

    public String observeGame(String... params) throws DataAccessException {
        if (params.length == 2) {
            String authToken = params[0];
            int gameNumber = Integer.parseInt(params[1]);
            int gameId = server.listGames(authToken).get(gameNumber-1).gameID();
            server.playGame(authToken,null,gameId);
            return String.format("You observed game %s", gameId);
        }
        throw new DataAccessException("Could not play game");
    }

}
