package models;

import chess.ChessGame;

public class GameData {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;
    ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public void setWhiteUsername(String userName) {
        whiteUsername = userName;
    }

    public void setBlackUsername(String userName) {
        this.blackUsername = userName;
    }

    public String blackUsername() {
        return blackUsername;
    }

    public String whiteUsername() {
        return whiteUsername;
    }

    public String gameName() {
        return gameName;
    }

    public int gameID() {
        return gameID;
    }

    public ChessGame game() {
        return game;
    }
}
