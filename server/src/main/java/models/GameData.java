package models;

import chess.ChessGame;

import java.util.Objects;

public class GameData {
    int gameID;
    String whiteUsername;
    String blackUsername;
    String gameName;
    ChessGame game;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID && Objects.equals(whiteUsername, gameData.whiteUsername) && Objects.equals(blackUsername, gameData.blackUsername) && Objects.equals(gameName, gameData.gameName) && Objects.equals(game, gameData.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

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
