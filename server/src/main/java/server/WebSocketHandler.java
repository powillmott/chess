package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private DataAccess dataAccess;
    private ChessGame.TeamColor otherColor;


    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.otherColor = null;

    }

    private final WebSocketSessions sessions = new WebSocketSessions();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            System.out.println(message);
            UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> connect(action, session);
                case MAKE_MOVE -> makeMove(message, session);
                case LEAVE -> leaveGame(action, session);
                case RESIGN -> resignGame(action, session);
            }
        } catch (Exception e) {
            sendError(e.getMessage(),session);
        }
    }

    private void connect(UserGameCommand action, Session session) throws Exception {
        if (dataAccess.validAuth(action.getAuthToken()) && dataAccess.getGame(action.getGameID()) != null) {
            sessions.addSessionToGame(action.getGameID(), session);
            String username = dataAccess.getUserName(action.getAuthToken());
            String message = String.format("%s joined the game", username);
            if (Objects.equals(dataAccess.getGame(action.getGameID()).blackUsername(), username)) {
                message = message + " as black player";
                this.otherColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(dataAccess.getGame(action.getGameID()).whiteUsername(), username)) {
                message = message + " as white player";
                this.otherColor = ChessGame.TeamColor.BLACK;
            }
            broadcastMessage(action.getGameID(), message, session);
            GameData game = dataAccess.getGame(action.getGameID());
            sendGame(game, session);
        } else {
            String errorMessage = "Error, could not connect, please ensure game number is valid";
            sendError(errorMessage, session);
        }
    }

    private void makeMove(String message, Session session) throws Exception {
        MakeMoveCommand action = new Gson().fromJson(message, MakeMoveCommand.class);
        String userName = dataAccess.getUserName(action.getAuthToken());
        ChessMove chessMove = action.getMove();
        GameData gameData = dataAccess.getGame(action.getGameID());

        if (!dataAccess.validAuth(action.getAuthToken())) {
            String errorMessage = "Authorization Error";
            sendError(errorMessage, session);
        } else if (dataAccess.getGame(action.getGameID()) == null) {
            String errorMessage = "Valid Game Error";
            sendError(errorMessage, session);
        } else if (!(gameData.blackUsername().equals(userName) || gameData.whiteUsername().equals(userName))) {
            String errorMessage = "Error: only players can make moves";
            sendError(errorMessage, session);
        } else if (!checkIfTurn(gameData,userName)) {
            String errorMessage = "Error: not your turn";
            sendError(errorMessage, session);
        }else if (!gameData.game().isCurrentGame()) {
            String errorMessage = "Error: game is over, cannot make move";
            sendError(errorMessage, session);
        } else {
            gameData.game().makeMove(chessMove);
            dataAccess.updateGame(gameData.game(),action.getGameID());
            ChessPiece piece = gameData.game().getBoard().getPiece(chessMove.getEndPosition());
            ChessPiece.PieceType pieceType;
            pieceType = piece.getPieceType();
            ChessPosition piecePos = chessMove.getEndPosition();
            String bMessage = String.format("%s moved %s to %s", dataAccess.getUserName(action.getAuthToken()),pieceType,piecePos);
            sendGame(gameData,session);
            broadcastGame(action.getGameID(),gameData,session);
            broadcastMessage(action.getGameID(),bMessage,session);
            if (gameData.game().isInCheckmate(this.otherColor)) {
                bMessage = String.format("Checkmate! %s wins",userName);
                broadcastMessage(action.getGameID(),bMessage,session);
                sendMessage(bMessage,session);
                gameData.game().setCurrentGame(false);
                dataAccess.updateGame(gameData.game(),action.getGameID());
            } else if (gameData.game().isInStalemate(this.otherColor)) {
                bMessage = "Stalemate! it's a draw";
                broadcastMessage(action.getGameID(),bMessage,session);
                sendMessage(bMessage,session);
                gameData.game().setCurrentGame(false);
                dataAccess.updateGame(gameData.game(),action.getGameID());
            } else if (gameData.game().isInCheck(this.otherColor)) {
                bMessage = "You are in check";
                broadcastMessage(action.getGameID(),bMessage,session);
            }

        }
    }

    private boolean checkIfTurn(GameData gameData, String userName) {
        boolean isTurn;
        if (gameData.blackUsername().equals(userName)) {
            isTurn = gameData.game().getTeamTurn().equals(ChessGame.TeamColor.BLACK);
        } else {
            isTurn = gameData.game().getTeamTurn().equals(ChessGame.TeamColor.WHITE);
        }
        return isTurn;
    }

    private void leaveGame(UserGameCommand action, Session session) throws Exception {
        sessions.removeSessionFromGame(action.getGameID(),session);
        String username = dataAccess.getUserName(action.getAuthToken());
        GameData gameData = dataAccess.getGame(action.getGameID());
        if (username.equals(gameData.whiteUsername())) {
            gameData.setWhiteUsername(null);
            dataAccess.playerLeavesGame(gameData, ChessGame.TeamColor.WHITE, action.getGameID());
        } else if (username.equals(gameData.blackUsername())) {
            gameData.setBlackUsername(null);
            dataAccess.playerLeavesGame(gameData, ChessGame.TeamColor.BLACK, action.getGameID());
        }
        String message = String.format("%s left the game", dataAccess.getUserName(action.getAuthToken()));
        broadcastMessage(action.getGameID(),message,session);
        sessions.removeSession(session);
    }

    private void resignGame(UserGameCommand action, Session session) throws Exception {
        String username = dataAccess.getUserName(action.getAuthToken());
        GameData game = dataAccess.getGame(action.getGameID());
        boolean isBlack = Objects.equals(dataAccess.getGame(action.getGameID()).blackUsername(), username);
        boolean isWhite = Objects.equals(dataAccess.getGame(action.getGameID()).whiteUsername(), username);
        boolean isCurrentGame = game.game().isCurrentGame();
        if (!(isBlack | isWhite)) {
            sendError("Error: only players can forfeit", session);
        } else if (!isCurrentGame) {
            sendError("Error: game is already finished, cannot forfeit", session);
        } else {
            String message = String.format("%s forfeits the game. Game is over", dataAccess.getUserName(action.getAuthToken()));
            sendMessage(message, session);
            broadcastMessage(action.getGameID(), message, session);
            game.game().setCurrentGame(false);
            dataAccess.updateGame(game.game(),action.getGameID());
        }
    }

    private void sendMessage(String message, Session session) throws Exception {
        Notification not = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,message);
        session.getRemote().sendString(new Gson().toJson(not));
    }

    private void broadcastMessage(Integer gameID, String message, Session sessionException) throws Exception {
        Notification not = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,message);
        for (Session ses : sessions.getSessionForGame(gameID)) {
            if (ses != sessionException) {
                ses.getRemote().sendString(new Gson().toJson(not));
            }
        }
    }

    private void sendGame(GameData game, Session session) throws Exception {
        LoadGame loadGame= new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        session.getRemote().sendString(new Gson().toJson(loadGame));
    }

    private void broadcastGame(Integer gameID, GameData game, Session sessionException) throws Exception {
        LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        for (Session ses : sessions.getSessionForGame(gameID)) {
            if (ses != sessionException) {
                ses.getRemote().sendString(new Gson().toJson(loadGame));
            }
        }
    }

    private void sendError(String errorMessage, Session session) throws Exception {
        Error error = new Error(ServerMessage.ServerMessageType.ERROR,errorMessage);
        session.getRemote().sendString(new Gson().toJson(error));
    }
}
