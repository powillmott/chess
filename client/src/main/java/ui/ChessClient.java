package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static ui.EscapeSequences.*;

import models.AuthData;
import models.GameData;
import serverfacade.ServerFacade;

public class ChessClient {
    private final ServerFacade server;
    private String authToken;
    private final List<Object> result = new ArrayList<>();


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
            result.set(0, ex.getMessage());
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
                    playGame(params);
//                    result.set(1, 2);
                }
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "observe" -> observeGame(params);
                default -> helpLoggedIn();
            }
        } catch (Exception ex) {
            result.set(0, ex.getMessage());
        }
    }

    public void helpLoggedOut() {
        result.set(0, """
                - login <USERNAME> <PASSWORD> - to play chess
                - register <USERNAME> <PASSWORD> <EMAIL> -to create an account
                - quit - exit chess
                - help - show menu""");
    }

    public void login(String... params) throws Exception {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = server.login(username, password);
            if (!authData.authToken().isEmpty()) {
                result.set(1, 1);
            }
            this.authToken = authData.authToken();
            result.set(0, String.format("You signed in as %s\n", authData.username()));
        } else {
            throw new Exception("Login failed");
        }
    }

    public void register(String... params) throws Exception {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = server.register(username, password, email);
            if (!authData.authToken().isEmpty()) {
                result.set(1, 1);
            }
            this.authToken = authData.authToken();
            result.set(0, String.format("You signed up as %s\n", authData.username()));
        } else {
            throw new Exception("Incorrect number of arguments");
        }
    }

    public void helpLoggedIn() {
        result.set(0, """
                - logout - logs out user
                - create <NAME> - creates a game
                - list - lists games
                - play - <ID> [WHITE|BLACK] - joins game
                - observe <ID> - observes a game""");
    }

    public void logout() throws Exception {
        server.logout(this.authToken);
        result.set(0, "you have successfully logged out");
        result.set(1, 0);
    }

    public void createGame(String... params) throws Exception {
        if (params.length == 1) {
            String gameName = params[0];
            server.createGame(this.authToken, gameName);
            result.set(0, String.format("You created game %s", gameName));
        } else {
            throw new Exception("Could not create game");
        }
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
        result.set(0, str);
    }

    public void playGame(String... params) throws Exception {
        if (params.length == 2) {
            String playerColor = params[1].toUpperCase();
            int gameNumber = Integer.parseInt(params[0]);
            int gameId = server.listGames(authToken).get(gameNumber - 1).gameID();
            server.playGame(authToken, playerColor, gameId);
            System.out.println();
            result.set(0, printBoardWhite() + "\n\n" + printBoardBlack() + RESET_TEXT_COLOR);
        } else {
            throw new Exception("Could not play game");
        }
    }

    public void observeGame(String... params) throws Exception {
        if (params.length == 1) {
            int gameNumber = Integer.parseInt(params[0]);
            int gameId = server.listGames(authToken).get(gameNumber - 1).gameID();
            result.set(0, printBoardWhite() + "\n\n" + printBoardBlack() + RESET_TEXT_COLOR);
        } else {
            throw new Exception("Could not play game");
        }
    }

    private String printBoardWhite() {
        String startSetting = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY;
        String header = startSetting + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
        StringBuilder board = new StringBuilder(header);
        String input = "";
        boolean wb = true;
        for (int i = 1; i < 9; i++) {
            if (i == 1 | i == 8) {
                input = "RNBKQBNR";
            } else if (i == 2 | i == 7) {
                input = "PPPPPPPP";
            } else {
                input = "        ";
            }
            board.append(startSetting).append(" ").append(i).append(" ");
            for (int j = 0; j < input.length(); j++) {
                if (i == 1 | i == 2) {
                    board.append(SET_TEXT_COLOR_RED);
                } else {
                    board.append(SET_TEXT_COLOR_BLUE);
                }
                wb = colorSwitch(wb, board);
                board.append(" ").append(input.charAt(j)).append(" ");
            }
            wb = colorSwitch(wb, board);
            board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        board.append(header);
        return board.toString();
    }

    private String printBoardBlack() {
        String startSetting = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY;
        String header = startSetting + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        StringBuilder board = new StringBuilder(header);
        String input = "";
        boolean wb = true;
        for (int i = 8; i > 0; i--) {
            if (i == 1 | i == 8) {
                input = "RNBKQBNR";
            } else if (i == 2 | i == 7) {
                input = "PPPPPPPP";
            } else {
                input = "        ";
            }
            board.append(startSetting).append(" ").append(i).append(" ");
            for (int j = 0; j < input.length(); j++) {
                if (i == 1 | i == 2) {
                    board.append(SET_TEXT_COLOR_RED);
                } else {
                    board.append(SET_TEXT_COLOR_BLUE);
                }
                wb = colorSwitch(wb, board);
                board.append(" ").append(input.charAt(j)).append(" ");
            }
            wb = colorSwitch(wb, board);
            board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        board.append(header);
        return board.toString();
    }

    private boolean colorSwitch(boolean wb, StringBuilder board) {
        if (wb) {
            board.append(SET_BG_COLOR_WHITE);
            wb = false;
        } else {
            board.append(SET_BG_COLOR_BLACK);
            wb = true;
        }
        return wb;
    }
}