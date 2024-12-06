package ui;

import java.util.*;

import static ui.EscapeSequences.*;

import chess.*;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import serverfacade.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private String authToken;
    private String userName;
    private final List<Object> result = new ArrayList<>();
    private WebSocketFacade ws;
    private String serverUrl;
    private int gameID;
    private GameData gameData;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = "";
        this.userName = "";
        this.result.add("");
        this.result.add(0);
        this.gameData = null;
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
                case "play" -> playGame(params);
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

    public void evalInGame(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            switch (cmd) {
                case "redraw" -> redrawChessBoard(null);
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resignGame();
                case "show" -> highlightLegalMove(params);
                default -> helpInGame();
            }
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
            this.userName = params[0];
            String password = params[1];
            AuthData authData = server.login(this.userName, password);
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
            this.userName = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = server.register(this.userName, password, email);
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

    public void helpInGame() {
        result.set(0, """
                - redraw - redraws chess board
                - leave - leaves game
                - move <start row> <start column> <end row> <end column> <promotion piece letter (optional)> - makes move on turn
                - resign - forfeits and ends game
                - show <piece row> <piece column> - shows legal moves
                - help - show options""");
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
                result.set(0,"");
                int gameId = server.listGames(authToken).get(gameNumber - 1).gameID();
                this.gameID = gameId;
                server.playGame(authToken, playerColor, gameId);
                UserGameCommand body = new UserGameCommand(UserGameCommand.CommandType.CONNECT, this.authToken, gameId);
                ws = new WebSocketFacade(this, serverUrl);
                ws.send(new Gson().toJson(body));
            }
            result.set(0,"");
            result.set(1, 2);
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
            this.gameID = gameId;
            UserGameCommand body = new UserGameCommand(UserGameCommand.CommandType.CONNECT, this.authToken, gameId);
            ws = new WebSocketFacade(this, serverUrl);
            ws.send(new Gson().toJson(body));
            result.set(0,"");
            result.set(1, 2);
        } else {
            throw new Exception("Could not play game");
        }
    }

    public String printBoardBlack(ChessBoard newBoard, Set<ChessPosition> highlightPosition) {
        System.out.println("\n");
        String startSetting = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY;
        String header = startSetting + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n";
        StringBuilder board = new StringBuilder(header);
        boolean wb = true;
        for (int i = 1; i < 9; i++) {
            board.append(startSetting).append(" ").append(i).append(" ");
            for (int j = 8; j > 0; j--) {
                wb = applyPiece(newBoard, board, wb, i, j, highlightPosition);
            }
            wb = colorSwitch(wb, board, false);
            board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        board.append(header).append(RESET_TEXT_COLOR);
        return board.toString();
    }

    public String printBoardWhite(ChessBoard newBoard, Set<ChessPosition> highlightPosition) {
        System.out.println("\n");
        String startSetting = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY;
        String header = startSetting + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n";
        StringBuilder board = new StringBuilder(header);
        boolean wb = true;
        for (int i = 8; i > 0; i--) {
            board.append(startSetting).append(" ").append(i).append(" ");
            for (int j = 1; j < 9; j++) {
                wb = applyPiece(newBoard, board, wb, i, j,highlightPosition);
            }
            wb = colorSwitch(wb, board, false);
            board.append(startSetting).append(" ").append(i).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        board.append(header).append(RESET_TEXT_COLOR);
        return board.toString();
    }

    private boolean applyPiece(ChessBoard newBoard, StringBuilder board, boolean wb, int i, int j, Set<ChessPosition> validMoves) {
        ChessPosition currentPos = new ChessPosition(i, j);
        ChessPiece piece = newBoard.getPiece(currentPos);
        boolean highlight = false;
        if (validMoves != null) {
            highlight = validMoves.contains(currentPos);
        }
        wb = colorSwitch(wb, board, highlight);
        if (piece == null) {
            board.append("   ");
        } else {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                board.append(SET_TEXT_COLOR_RED);
            } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
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


    private boolean colorSwitch(boolean wb, StringBuilder board, boolean highlight) {
        if (wb) {
            if (highlight) {
                board.append(SET_BG_COLOR_YELLOW);
            } else {
                board.append(SET_BG_COLOR_WHITE);
            }
            wb = false;
        } else {
            if (highlight) {
                board.append(SET_BG_COLOR_MAGENTA);
            } else {
                board.append(SET_BG_COLOR_BLACK);
            }
            wb = true;
        }
        return wb;
    }

    public void leaveGame() throws Exception {
        UserGameCommand body = new UserGameCommand(UserGameCommand.CommandType.LEAVE, this.authToken, this.gameID);
        ws.send(new Gson().toJson(body));
        result.set(1, 1);
        helpLoggedIn();
    }

    public void resignGame() throws Exception {
        System.out.print("Are you sure you want to resign? <Y> <N>");
        String line = new Scanner(System.in).nextLine();
        if (line.equalsIgnoreCase("Y")) {
            UserGameCommand body = new UserGameCommand(UserGameCommand.CommandType.RESIGN, this.authToken, this.gameID);
            result.set(0,"");
            ws.send(new Gson().toJson(body));
        }
    }

    public String getUserName() {
        return userName;
    }

    public void makeMove(String... params) throws Exception {
        if (params.length >= 4) {
            int startRow;
            int startCol;
            int endRow;
            int endCol;
            try {
                startRow = Integer.parseInt(params[0]);
                startCol = Integer.parseInt(params[1]);
                endRow = Integer.parseInt(params[2]);
                endCol = Integer.parseInt(params[3]);
            } catch (NumberFormatException e) {
                System.out.println("please input chess position as numbers");
                return;
            }
            ChessPiece.PieceType promotionPiece = null;
            if (params.length == 5) {
                promotionPiece = promotion(params[4]);
            }
            ChessPosition startPosition = new ChessPosition(startRow,startCol);
            ChessPosition endPosition = new ChessPosition(endRow,endCol);
            ChessMove move = new ChessMove(startPosition,endPosition,promotionPiece);
            MakeMoveCommand moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,this.authToken,this.gameID,move);
            ws.send(new Gson().toJson(moveCommand));
        }
    }

    private ChessPiece.PieceType promotion(String letter) {
        ChessPiece.PieceType piece = null;
        switch (letter.toUpperCase()) {
            case "Q" -> piece = ChessPiece.PieceType.QUEEN;
            case "R" -> piece = ChessPiece.PieceType.ROOK;
            case "B" -> piece = ChessPiece.PieceType.BISHOP;
            case "N" -> piece = ChessPiece.PieceType.KNIGHT;
        }
        return piece;
    }

    public void redrawChessBoard(Set<ChessPosition> highlightPosition) {
        String boardPrintOut;
        if (getUserName().equals(gameData.blackUsername())) {
            boardPrintOut = printBoardBlack(gameData.game().getBoard(),highlightPosition);
        } else {
            boardPrintOut = printBoardWhite(gameData.game().getBoard(),highlightPosition);
        }
        result.set(0,"");
        System.out.println(boardPrintOut);
    }

    public void highlightLegalMove(String...params) {
        if (params.length == 2) {
            int pieceRow = Integer.parseInt(params[0]);
            int pieceCol = Integer.parseInt(params[1]);
            ChessPosition piecePosition = new ChessPosition(pieceRow,pieceCol);
            Collection<ChessMove> validMoves = this.gameData.game().validMoves(piecePosition);
            Set<ChessPosition> validPositions = new HashSet<ChessPosition>();
            for (ChessMove move : validMoves) {
                validPositions.add(move.getEndPosition());
            }
            redrawChessBoard(validPositions);

        }
    }

    @Override
    public void notify(ServerMessage notification, String message) {
        switch (notification.getServerMessageType()) {
            case NOTIFICATION -> {
                Notification not = new Gson().fromJson(message,Notification.class);
                System.out.println(not.getMessage());
            }
            case LOAD_GAME -> loadGame(new Gson().fromJson(message,LoadGame.class));
            case ERROR -> {
                websocket.messages.Error err = new Gson().fromJson(message,websocket.messages.Error.class);
                System.out.println(err.getErrorMessage());
            }
        }
    }

    private void loadGame(LoadGame loadGame) {
        GameData gameData = loadGame.getGameData();
        this.gameData = gameData;
        String boardPrintOut;
        if (getUserName().equals(gameData.blackUsername())) {
            boardPrintOut = printBoardBlack(gameData.game().getBoard(),null);
        } else {
            boardPrintOut = printBoardWhite(gameData.game().getBoard(),null);
        }
        System.out.println(boardPrintOut);
    }
}