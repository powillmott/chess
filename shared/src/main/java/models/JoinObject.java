package models;

public class JoinObject {
    private final String playerColor;
    private final Integer gameID;
    public JoinObject(String playerColor, Integer gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }


    public String getPlayerColor() {
        return playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
