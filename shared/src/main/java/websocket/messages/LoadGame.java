package websocket.messages;

import chess.ChessBoard;
import models.GameData;

public class LoadGame extends ServerMessage {
    private GameData game;
    public LoadGame(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    public GameData getGameData() {
        return game;
    }

    public void setGameData(GameData game) {
        this.game = game;
    }
}
