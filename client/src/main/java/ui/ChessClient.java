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
    private List<Object> result = new ArrayList<>();


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
//        this.serverUrl = serverUrl;
        this.authToken = "";
        this.result.add("");
        this.result.add(0);
    }

    public List<Object> getResult() {
        return result;
    }

    public void evalSignedOut(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "login" -> {
                    login(params);
                    result.set(1, 1);
                }
                case "register" -> register(params);
                case "quit" -> result.set(0, "exit");
                default -> helpLoggedOut();
            }
        } catch (Exception ex) {
            result.set(0,ex.getMessage());
        }
    }

    public void evalSignedIn(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "logout" -> {
                    logout();
                    result.set(1, 0);
                }
                case "play" -> {
                    result.set(0, playGame(params));
                    result.set(1, 2);
                }
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "observe" -> observeGame(params);
                default -> helpLoggedIn();
            }
        } catch (Exception ex) {
            result.set(0,ex.getMessage());
        }
    }

    public void helpLoggedOut() {
        result.set(0,"""
                - login <USERNAME> <PASSWORD> - to play chess
                - register <USERNAME> <PASSWORD> <EMAIL> -to create an account
                - quit - exit chess
                - help - show menu""");
    }

    public void login(String... params) throws Exception {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = server.login(username,password);
            if (!authData.authToken().isEmpty()) {result.set(1, 1);}
            this.authToken = authData.authToken();
            result.set(0,String.format("You signed in as %s", authData.username()));
        } else {throw new DataAccessException("Login failed");}
    }

    public void register(String... params) throws Exception {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = server.register(username,password,email);
            if (!authData.authToken().isEmpty()) {result.set(1, 1);}
            this.authToken = authData.authToken();
            result.set(0,String.format("You signed up as %s", authData.username()));
        } else {throw new DataAccessException("Incorrect number of arguments");}
    }

    public void helpLoggedIn() {
        result.set(0,"""
                - logout - logs out user
                - create <NAME> - creates a game
                - list - lists games
                - play - <ID> [WHITE|BLACK] - joins game
                - observe <ID> - observes a game""");
    }

    public void logout() throws Exception {
        server.logout(this.authToken);
        result.set(0,"you have successfully logged out");
        result.set(1,0);
    }

    public void createGame(String... params) throws Exception {
        if (params.length == 1) {
            String gameName = params[0];
            server.createGame(this.authToken,gameName);
            result.set(0,String.format("You created game %s", gameName));
        } else {throw new DataAccessException("Could not create game");}
    }

    public void listGames() throws Exception {
        String str = "";
        if (!authToken.isEmpty()) {
            int i = 0;
            for (GameData game : server.listGames(this.authToken)) {
                i += 1;
                str = String.format("%s%d %s white player: %s black player %s\n", str, i, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
        }
        result.set(0,str);
    }

    public String playGame(String... params) throws Exception {
        if (params.length == 3) {
            String authToken = params[0];
            String playerColor = params[1];
            int gameNumber = Integer.parseInt(params[2]);
            int gameId = server.listGames(authToken).get(gameNumber-1).gameID();
            server.playGame(authToken,playerColor,gameId);
            result.set(1,2);

            return String.format("You played game %s", gameId);
        }
        throw new DataAccessException("Could not play game");
    }

    public String observeGame(String... params) throws Exception {
        if (params.length == 2) {
            String authToken = params[0];
            int gameNumber = Integer.parseInt(params[1]);
            int gameId = server.listGames(authToken).get(gameNumber-1).gameID();
            server.observeGame(authToken,null,gameId);
            return String.format("You observed game %s", gameId);
        }
        throw new DataAccessException("Could not play game");
    }

}
