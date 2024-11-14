package ui;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessClient {
    private final String serverUrl;
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
//                case "register" -> result.set(0, register(params));
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
//                case "play" -> {
//                    result.set(0, playGame(params));
//                    result.set(1, 2);
//                }
//                case "create" -> result.set(0, createGame(params));
//                case "list" -> result.set(0, listGames());
//                case "observe" -> result.set(0, observeGame(params));
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

    public String login(String... params) {
        return "you have successfully logged in";
    }

//    public String register(String... params) {}

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

//    public String createGame(String... params) {}

//    public String listGames() {}

//    public String playGame(String... params) {}

//    public String observeGame(String... params) {}

}
