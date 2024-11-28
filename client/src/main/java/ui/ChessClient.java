package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static ui.EscapeSequences.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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
        } catch (NumberFormatException ex) {
            result.set(0, "not a valid game number");
        } catch
        (Exception ex) {
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
                - play - <GAME NUMBER> [WHITE|BLACK] - joins game
                - observe <GAME NUMBER> - observes a game
                - help - show menu""");
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
                str = String.format("%s%d %s white player: %s black player %s\n",
                        str, i, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
        }
        result.set(0, str);
    }

    public void playGame(String... params) throws Exception {
        if (params.length == 2) {
            String playerColor = params[1].toUpperCase();
            int gameNumber = Integer.parseInt(params[0]);
            if (gameNumber > server.listGames(authToken).size() || gameNumber < 0) {
                throw new Exception("not a valid game");
            } else if (!(playerColor.equals("WHITE") || playerColor.equals("BLACK"))) {
                throw new Exception("choose either white or black for your team");
            } else {
                int gameId = server.listGames(authToken).get(gameNumber - 1).gameID();
                server.playGame(authToken, playerColor, gameId);
            }

//            System.out.println();
            result.set(0, printBoardWhite() + "\n\n" + printBoardBlack() + RESET_TEXT_COLOR);
        } else {
            throw new Exception("Could not play game");
        }
    }

    public void observeGame(String... params) throws Exception {
        if (params.length == 1) {
            int gameNumber = Integer.parseInt(params[0]);
            if (gameNumber > server.listGames(authToken).size() || gameNumber < 0) {
                throw new Exception("not a valid game");
            }
            int gameId = server.listGames(authToken).get(gameNumber - 1).gameID();
            result.set(0, printBoardWhite() + "\n\n" + printBoardBlack() + RESET_TEXT_COLOR);
        } else {
            throw new Exception("Could not play game");
        }
    }

    private String printBoardBlack() {
        String startSetting = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY;
        ChessBoard newBoard = new ChessBoard();
        newBoard.resetBoard();
        String header = startSetting + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
        StringBuilder board = new StringBuilder(header);
        boolean wb = true;
        for (int i = 1; i < 9; i++) {
            board.append(startSetting).append(" ").append(i).append(" ");
            for (int j = 8; j > 0; j--){
                wb = applyPiece(newBoard, board, wb, i, j);
            }
            wb = colorSwitch(wb, board);
            board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        board.append(header);
        return board.toString();
    }

    private String printBoardWhite() {
        String startSetting = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY;
        ChessBoard newBoard = new ChessBoard();
        newBoard.resetBoard();
        String header = startSetting + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        StringBuilder board = new StringBuilder(header);
        boolean wb = true;
        for (int i = 8; i > 0; i--) {
            board.append(startSetting).append(" ").append(i).append(" ");
            for (int j = 1; j < 9; j++){
                wb = applyPiece(newBoard, board, wb, i, j);
            }
            wb = colorSwitch(wb, board);
            board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        board.append(header);
        return board.toString();
    }

    private boolean applyPiece(ChessBoard newBoard, StringBuilder board, boolean wb, int i, int j) {
        wb = colorSwitch(wb, board);
        ChessPiece piece = newBoard.getPiece(new ChessPosition(i,j));
        if (piece == null){
            board.append("   ");
        } else{
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                board.append(SET_TEXT_COLOR_RED);
            } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                board.append(SET_TEXT_COLOR_BLUE);
            }
            board.append(" ").append(pieceSymbol(piece.getPieceType())).append(" ");
        }
        return wb;
    }

    private String pieceSymbol(ChessPiece.PieceType pieceType) {
        String symbol = "";
        if (pieceType == ChessPiece.PieceType.KING) {
            symbol = "K";
        } else if (pieceType == ChessPiece.PieceType.QUEEN) {
            symbol = "Q";
        } else if (pieceType == ChessPiece.PieceType.BISHOP) {
            symbol = "B";
        } else if (pieceType == ChessPiece.PieceType.ROOK) {
            symbol = "R";
        } else if (pieceType == ChessPiece.PieceType.KNIGHT) {
            symbol = "N";
        } else if (pieceType == ChessPiece.PieceType.PAWN) {
            symbol = "P";
        }
        return symbol;
    }

//    private boolean isWb(String startSetting, StringBuilder board, boolean wb, int i, boolean w) {
//        String input;
//        if (i == 1 | i == 8) {
//            if (!w) {
//                input = "RNBKQBNR";
//            } else {
//                input = "RNBQKBNR";
//            }
//        } else if (i == 2 | i == 7) {
//            input = "PPPPPPPP";
//        } else {
//            input = "        ";
//        }
//        board.append(startSetting).append(" ").append(i).append(" ");
//        for (int j = 0; j < input.length(); j++) {
//            if (i == 1 | i == 2) {
//                board.append(SET_TEXT_COLOR_RED);
//            } else {
//                board.append(SET_TEXT_COLOR_BLUE);
//            }
//            wb = colorSwitch(wb, board);
//            board.append(" ").append(input.charAt(j)).append(" ");
//        }
//        wb = colorSwitch(wb, board);
//        board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
//        return wb;
//    }

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